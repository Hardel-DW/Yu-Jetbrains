# Yu pour JetBrains - Architecture interne

## La decision de parsing : langage custom, pas reutilisation de XML

Le .mcui ressemble a du XML mais n'en est pas. La grammaire reelle de Yu (McuiParser dans yu-ui) le prouve : un nom de tag de composant porte un namespace puis un chemin ou le slash et le point sont legaux, donc studio:demo/card est un seul nom de tag. Le XML interdit le slash dans un nom. La grammaire distingue aussi trois formes de nom : minuscule pour un tag du moteur, namespace deux-points chemin pour un composant, PascalCase pour un alias. L'interpolation double-accolade vit dans le texte et dans les valeurs d'attribut, et l'attribut class est lui-meme un mini-langage de jetons a prefixes de variants. Reutiliser le PSI XML casserait sur les noms de tags et n'offrirait pas de jetons propres pour les classes et les interpolations.

Decision : un langage custom via JFlex pour le lexer et Grammar-Kit pour le parser et le PSI. La grammaire de syntaxe est stable dans le temps, ce qui change est le vocabulaire, et celui-ci passe par le schema. L'alternative XML est ecartee pour la raison ci-dessus.

## La grammaire .mcui resumee

Tiree de McuiParser, c'est la cible du lexer et du parser.

- Element : chevron ouvrant, qualifier optionnel suivi de deux-points, nom de tag ou chemin, attributs, puis soit slash chevron fermant pour une feuille, soit chevron fermant, contenu, et tag fermant apparie.
- Nom de tag du moteur : commence par une minuscule, contient minuscules, chiffres, tiret, souligne. Alias : commence par une majuscule. Chemin de composant : minuscules, chiffres, tiret, souligne, slash, point.
- Attribut : nom a demarrage minuscule, signe egal, valeur entre guillemets simples ou doubles. Le chevron ouvrant est interdit dans une valeur. Entites reconnues : lt, gt, amp, quot, apos.
- Texte : entre les elements, espaces compactes. Interpolation par double-accolade, resolue en aval par TextTemplate cote moteur.
- Commentaires chevron-bang. Pas de prolog XML. Tag fermant qui doit correspondre au tag ouvrant.

## Les couches du plugin

- Langage et fichier : McuiLanguage, McuiFileType pour l'extension .mcui, McuiParserDefinition qui relie lexer, parser et PSI.
- Coloration : un SyntaxHighlighter sur les jetons du lexer, plus une page de reglages de couleurs.
- Injection de langage : un MultiHostInjector injecte un langage mcui-class dans les valeurs de l'attribut class et un langage mcui-expr dans les interpolations double-accolade et les valeurs d'attributs reactifs. La completion et la validation des classes et des chemins s'attachent a ces langages injectes, ce qui evite de parser ces sous-langages dans la grammaire principale.
- References : un PsiReferenceContributor cote .mcui et un cote Java, chaque arete du graphe etant une classe de reference derivee de PsiReferenceBase. Le rename passe par l'ElementManipulator qui ecrit le nouveau texte dans la plage de la reference, donc renommer un id, un chemin de signal ou un composant se propage des deux cotes.
- Inutilises : un ImplicitUsageProvider cote Java declare references les champs et methodes vises par un id ou un chemin depuis un .mcui.
- Completion : des CompletionContributor pour les tags et attributs depuis le schema et la grammaire, pour les jetons de classe avec variants et couleurs, pour les chemins de proprietes du controller.
- Inspections : un Annotator pour le live par fragment qui rejoue le resolver strict (classe, tag, attribut, identifier inconnu), et des LocalInspectionTool pour le batch (cle de traduction absente, asset absent, type de liaison value). Quickfixes attaches quand l'action est evidente.
- Navigation : gratuite des que les references resolvent, via le mecanisme standard de declaration et de find usages.

## Le pont schema

La source de verite du vocabulaire est dans les registres de Yu (ClassRegistry, McuiTagRegistry, Palette, VariantRegistry, FontFamilies). Au runtime, tous les mods charges se sont enregistres dans ces memes registres partages : la fusion a deja eu lieu dans le registre vivant. Le plugin ne collecte donc pas un schema par mod, il lit un instantane du registre deja fusionne. Le dispositif est pleinement automatique en deux temps, rien de manuel.

- Baseline integre, instantane, sans lancement. Le build de yu-ui serialise le vocabulaire integre dans une ressource yu/schema.json du jar yu-ui. A l'ouverture du projet, le plugin lit cette ressource depuis le jar present sur le classpath, donc il suit la version exacte de Yu utilisee. Ce baseline couvre la grande majorite : classes, couleurs, variants et polices integres.
- Snapshot runtime, complet, automatique au lancement. En environnement de developpement, au demarrage du client, Yu ecrit le registre fusionne complet (integre plus tous les mods charges) dans un fichier de la zone de run, sans commande. Le plugin le superpose au baseline. C'est ce snapshot qui apporte le vocabulaire des mods tiers, puisqu'a cet instant ils sont tous dans le registre. A l'execution Minecraft est charge, donc lire les registres ne pose aucun probleme de bootstrap, contrairement a un export build.
- Limite incompressible. Connaitre une classe enregistree par code au runtime exige qu'un lancement ait eu lieu une fois, et un relancement pour capter un vocabulaire fraichement ajoute. Aucun mecanisme ne devine un nom enregistre par code sans l'executer ou le declarer a la main : c'est le cout du zero-manuel, et c'est le plus bas possible.
- Cote plugin : un service de projet lit le baseline et le snapshot, les fusionne, met en cache et recharge a chaud quand le jar ou le fichier de snapshot change. Avant toute resolution, mode permissif, syntaxe et coloration seules.
- Contenu : jetons litteraux et prefixes de classes avec leurs drapeaux variantable, lifecycle et negatable ; couleurs unitaires et familles a nuances ; etats de variants et scopes ; familles de polices ; tags du moteur et namespacage, plus les alias decouverts dans le projet.
- Amelioration ulterieure possible : scanner les appels register du source du projet via le PSI pour afficher le vocabulaire custom du projet sans lancement. Bonus, pas le socle.

## Limite a acter sur le schema

Les attributs autorises par tag ne sont pas declaratifs dans Yu aujourd'hui : chaque tag valide ses attributs de maniere imperative dans CoreTags. Une completion et une validation fideles des attributs sans dupliquer cette connaissance demandent que Yu fasse grandir une declaration legere d'attributs par tag, que l'export porterait et que le moteur pourrait consommer aussi pour sa propre validation. C'est un manque revele par le plugin, a corriger de maniere generique dans Yu. En attendant, la version 1 traite les attributs de facon permissive : pas d'erreur sur un attribut inconnu, completion limitee aux attributs communs.

## Arborescence prevue

- build.gradle.kts, settings.gradle.kts, gradle.properties : le projet Gradle 2.x.
- src/main/resources/META-INF/plugin.xml : le descripteur, points d'extension.
- src/main/resources/icons : l'icone de fichier et les icones de gutter.
- src/main/resources/messages : le bundle de chaines UI.
- src/main/java/fr/hardel/yu/idea/lang : McuiLanguage, McuiFileType, McuiParserDefinition, la grammaire Mcui.bnf, le lexer Mcui.flex, le paquet psi genere et McuiPsiImplUtil.
- src/main/java/fr/hardel/yu/idea/highlight : le highlighter, sa fabrique, la page de couleurs.
- src/main/java/fr/hardel/yu/idea/inject : les langages mcui-class et mcui-expr, le MultiHostInjector.
- src/main/java/fr/hardel/yu/idea/schema : le modele du schema, le service de projet, le chargeur.
- src/main/java/fr/hardel/yu/idea/ref : le contributeur de references cote .mcui, le contributeur cote Java, une classe par type de reference, l'ImplicitUsageProvider.
- src/main/java/fr/hardel/yu/idea/completion : les contributeurs de completion pour les tags, les classes, les chemins.
- src/main/java/fr/hardel/yu/idea/inspection : l'Annotator live et les inspections batch avec leurs quickfixes.
- docs : la documentation du plugin.

Cote yu-ui, un seul ajout : la tache Gradle d'export du schema et son point d'entree dans les registres.
