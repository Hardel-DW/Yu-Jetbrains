# Yu pour JetBrains - Architecture interne

## La décision de parsing : langage custom, pas réutilisation de XML

Le .mcui ressemble à du XML mais n'en est pas. La grammaire réelle de Yu (McuiParser dans yu-ui) le prouve : un nom de tag de composant porte un namespace puis un chemin où le slash et le point sont légaux, donc studio:demo/card est un seul nom de tag. Le XML interdit le slash dans un nom. La grammaire distingue aussi trois formes de nom : minuscule pour un tag du moteur, namespace deux-points chemin pour un composant, PascalCase pour un alias. L'interpolation double-accolade vit dans le texte et dans les valeurs d'attribut, et l'attribut class est lui-même un mini-langage de jetons à préfixes de variants. Réutiliser le PSI XML casserait sur les noms de tags et n'offrirait pas de jetons propres pour les classes et les interpolations.

Décision : un langage custom via JFlex pour le lexer et Grammar-Kit pour le parser et le PSI. La grammaire de syntaxe est stable dans le temps, ce qui change est le vocabulaire, et celui-ci passe par le schéma. L'alternative XML est écartée pour la raison ci-dessus.

## La grammaire .mcui résumée

Tirée de McuiParser, c'est la cible du lexer et du parser.

- Élément : chevron ouvrant, qualifier optionnel suivi de deux-points, nom de tag ou chemin, attributs, puis soit slash chevron fermant pour une feuille, soit chevron fermant, contenu, et tag fermant apparié.
- Nom de tag du moteur : commence par une minuscule, contient minuscules, chiffres, tiret, souligné. Alias : commence par une majuscule. Chemin de composant : minuscules, chiffres, tiret, souligné, slash, point.
- Attribut : nom à démarrage minuscule, signe égal, valeur entre guillemets simples ou doubles. Le chevron ouvrant est interdit dans une valeur. Entités reconnues : lt, gt, amp, quot, apos.
- Texte : entre les éléments, espaces compactées. Interpolation par double-accolade, résolue en aval par TextTemplate côté moteur.
- Commentaires chevron-bang. Pas de prolog XML. Tag fermant qui doit correspondre au tag ouvrant.

## Les couches du plugin

- Langage et fichier : McuiLanguage, McuiFileType pour l'extension .mcui, McuiParserDefinition qui relie lexer, parser et PSI.
- Coloration : un SyntaxHighlighter sur les jetons du lexer, plus une page de réglages de couleurs.
- Injection de langage : un MultiHostInjector injecte un langage mcui-class dans les valeurs de l'attribut class et un langage mcui-expr dans les interpolations double-accolade et les valeurs d'attributs réactifs. La complétion et la validation des classes et des chemins s'attachent à ces langages injectés, ce qui évite de parser ces sous-langages dans la grammaire principale.
- Références : un PsiReferenceContributor côté .mcui et un côté Java, chaque arête du graphe étant une classe de référence dérivée de PsiReferenceBase. Le rename passe par l'ElementManipulator qui écrit le nouveau texte dans la plage de la référence, donc renommer un id, un chemin de signal ou un composant se propage des deux côtés.
- Inutilisés : un ImplicitUsageProvider côté Java déclare référencés les champs et méthodes visés par un id ou un chemin depuis un .mcui.
- Complétion : des CompletionContributor pour les tags et attributs depuis le schéma et la grammaire, pour les jetons de classe avec variants et couleurs, pour les chemins de propriétés du controller.
- Inspections : un Annotator pour le live par fragment qui rejoue le resolver strict (classe, tag, attribut, identifier inconnu), et des LocalInspectionTool pour le batch (clé de traduction absente, asset absent, type de liaison value). Quickfixes attachés quand l'action est évidente.
- Navigation : gratuite dès que les références résolvent, via le mécanisme standard de déclaration et de find usages.

## Contraintes de plateforme, actées à l'audit du 2026-06-28

Ces contraintes façonnent le PSI dès le lot 1, pas plus tard : le rename et l'injection dictent la forme des nœuds, les découvrir au lot 3 obligerait à refaire le PSI.

- Hosts d'injection : les nœuds PSI de valeur d'attribut et de texte qui portent class et les interpolations doivent implémenter PsiLanguageInjectionHost avec des plages stables. Grammar-Kit ne le génère pas, c'est un choix de forme du PSI à faire dès le lot 1, sinon mcui-class et mcui-expr arrivent trop tard.
- Noms renommables : tout nom .mcui renommable (id, slot, chemin, composant, alias) doit être un vrai PsiNamedElement avec plage et getTextOffset, plus un ElementManipulator. Un NamesValidator dédié est obligatoire, sinon les règles d'identifiant Java s'appliquent et cassent sur les deux-points, le slash et le point.
- Index de mots : un WordsScanner adapté aux jetons contenant deux-points, slash et point, sinon find usages et rename ne proposent jamais certains usages.
- Inutilisés : l'ImplicitUsageProvider est appelé pour chaque symbole pendant le highlighting, il doit rester local et bon marché, sans recherche de références ni requête d'index. La résolution .mcui vers Java passe par le mécanisme standard de recherche par références, pas par ce provider.
- Résolution Java : déclarer la dépendance au module Java (com.intellij.java, com.intellij.modules.java) et résoudre par scope de module, pas projet global, pour ne pas confondre des controllers homonymes de modules différents.
- Tolérance du parser : le parser d'IDE encaisse l'édition incomplète (pin et recoverWhile de Grammar-Kit), il n'est pas fidèle au fail-fast du resolver runtime, sinon le PSI est instable dès l'édition.
- Exposition du PSI généré : ne pas diffuser les interfaces générées par Grammar-Kit dans tout le plugin, passer par des abstractions, pour limiter la rigidité quand la grammaire évolue.

## Le pont schéma

La source de vérité du vocabulaire est dans les registres de Yu (ClassRegistry, McuiTagRegistry, Palette, VariantRegistry, FontFamilies). Au runtime, tous les mods chargés se sont enregistrés dans ces mêmes registres partagés : la fusion a déjà eu lieu dans le registre vivant. Le plugin ne collecte donc pas un schéma par mod, il lit un instantané du registre déjà fusionné. Le dispositif est pleinement automatique en deux temps, rien de manuel.

- Baseline intégré, instantané, sans lancement. Le build de yu-ui sérialise le vocabulaire intégré dans une ressource yu/schema.json du jar yu-ui. À l'ouverture du projet, le plugin lit cette ressource via le modèle de projet (workspace model, racines de librairies) et le VFS, jamais via son propre classloader qui est isolé des dépendances du projet, donc il suit la version exacte de Yu utilisée. Ce baseline couvre la grande majorité : classes, couleurs, variants et polices intégrés.
- Snapshot runtime, complet, automatique au lancement. En environnement de développement, au démarrage du client, Yu écrit le registre fusionné complet (intégré plus tous les mods chargés) dans un fichier de la zone de run, sans commande. Le plugin le superpose au baseline. C'est ce snapshot qui apporte le vocabulaire des mods tiers, puisqu'à cet instant ils sont tous dans le registre. À l'exécution Minecraft est chargé, donc lire les registres ne pose aucun problème de bootstrap, contrairement à un export build.
- Limite incompressible. Connaître une classe enregistrée par code au runtime exige qu'un lancement ait eu lieu une fois, et un relancement pour capter un vocabulaire fraîchement ajouté. Aucun mécanisme ne devine un nom enregistré par code sans l'exécuter ou le déclarer à la main : c'est le coût du zéro-manuel, et c'est le plus bas possible.
- Côté plugin : un service de projet lit le baseline et le snapshot, les fusionne, met en cache et recharge à chaud quand le jar ou le fichier de snapshot change. La lecture se fait par le VFS hors dumb mode, avec invalidation à la modification des roots ou du snapshot et une source active affichée (baseline jar, snapshot runtime, ou aucun). En included build local, la ressource n'existe qu'après processResources, le service doit l'encaisser. Avant toute résolution, mode permissif, syntaxe et coloration seules.
- Contenu : jetons littéraux et préfixes de classes avec leurs drapeaux variantable, lifecycle et negatable ; couleurs unitaires et familles à nuances ; états de variants et scopes ; familles de polices ; tags du moteur et namespaçage, plus les alias découverts dans le projet.
- Amélioration ultérieure possible : scanner les appels register du source du projet via le PSI pour afficher le vocabulaire custom du projet sans lancement. Bonus, pas le socle.

## Limite à acter sur le schéma

Les attributs autorisés par tag ne sont pas déclaratifs dans Yu aujourd'hui : chaque tag valide ses attributs de manière impérative dans CoreTags. Une complétion et une validation fidèles des attributs sans dupliquer cette connaissance demandent que Yu fasse grandir une déclaration légère d'attributs par tag, que l'export porterait et que le moteur pourrait consommer aussi pour sa propre validation. C'est un manque révélé par le plugin, à corriger de manière générique dans Yu. En attendant, la version 1 traite les attributs de façon permissive : pas d'erreur sur un attribut inconnu, complétion limitée aux attributs communs.

## Arborescence prévue

- build.gradle.kts, settings.gradle.kts, gradle.properties : le projet Gradle 2.x.
- src/main/resources/META-INF/plugin.xml : le descripteur, points d'extension.
- src/main/resources/icons : l'icône de fichier et les icônes de gutter.
- src/main/resources/messages : le bundle de chaînes UI.
- src/main/java/fr/hardel/yu/idea/lang : McuiLanguage, McuiFileType, McuiParserDefinition, la grammaire Mcui.bnf, le lexer Mcui.flex, le paquet psi généré et McuiPsiImplUtil.
- src/main/java/fr/hardel/yu/idea/highlight : le highlighter, sa fabrique, la page de couleurs.
- src/main/java/fr/hardel/yu/idea/inject : les langages mcui-class et mcui-expr, le MultiHostInjector.
- src/main/java/fr/hardel/yu/idea/schema : le modèle du schéma, le service de projet, le chargeur.
- src/main/java/fr/hardel/yu/idea/ref : le contributeur de références côté .mcui, le contributeur côté Java, une classe par type de référence, l'ImplicitUsageProvider.
- src/main/java/fr/hardel/yu/idea/completion : les contributeurs de complétion pour les tags, les classes, les chemins.
- src/main/java/fr/hardel/yu/idea/inspection : l'Annotator live et les inspections batch avec leurs quickfixes.
- docs : la documentation du plugin.

Côté yu-ui, un seul ajout : la tâche Gradle d'export du schéma et son point d'entrée dans les registres.
