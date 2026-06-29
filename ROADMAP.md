# Yu pour JetBrains - Roadmap et constat

Ce document est la trace des decisions et le plan de travail. Les specs sont Overview.md et Architecture.md dans docs.

## Doctrine

- Une seule source de verite par sujet. Le vocabulaire de Yu ne se hardcode jamais dans le plugin, il vient du schema exporte.
- Qualite long terme. Chaque sujet se discute avant de se coder. Pas de code mort, duplique ou bricole.
- La grammaire de syntaxe est stable et vit dans le plugin. Le vocabulaire est dynamique et vit dans le schema.
- Le plugin est generique : il sert n'importe quel projet consommant Yu, pas seulement Studio.

## Decisions actees le 2026-06-28

- Repo separe Yu-Jetbrains, hors du workspace voxel.studio.mods.
- Pont schema, plein auto en deux temps : baseline du vocabulaire integre serialise dans le jar yu-ui (instantane, sans lancement), plus un snapshot du registre fusionne complet ecrit par Yu au demarrage du client en dev (sans commande, capte les mods tiers). Le plugin lit et fusionne les deux. La fusion est gratuite : au runtime le registre partage est deja la fusion. Limite incompressible : capter un vocabulaire enregistre par code exige un lancement.
- La navigation croisee Java vers .mcui dans les deux sens est le coeur, avec le rename refactoring natif et la suppression des faux inutilises.
- Parsing : langage custom JFlex plus Grammar-Kit, pas de reutilisation du PSI XML. Le .mcui est du XML a environ quatre-vingt pour cent, mais ses subtilites (slash dans les noms de tags de composants, interpolation, attribut class) justifient le langage custom.
- Langage du plugin : Java.

## Recommandations a valider avant de coder

- Attributs par tag : Yu n'a pas de declaration d'attributs declarative. Reco : faire grandir cette declaration dans Yu pour que l'export la porte, et traiter les attributs de facon permissive en version 1. Le design de cette declaration se fait quand le lot 2 du plugin la reclame, informe par le besoin reel, pas a l'aveugle.

## Plan en lots

- Lot 0 : socle. Projet Gradle 2.x, plugin.xml minimal, McuiFileType et McuiLanguage vides, runIde qui ouvre un projet, verifyPlugin vert.
- Lot 1 : langage. Lexer flex, grammaire bnf, PSI genere, coloration, fermeture automatique des tags, appariement, repliage, commentaires. Le .mcui s'edite avec couleurs et structure, sans connaissance du vocabulaire.
- Lot 2 : schema et completion. Export cote yu-ui, service de schema cote plugin, injection des langages class et expr, completion des tags, des jetons de classe avec variants et couleurs, des attributs communs.
- Lot 3 : references. Le graphe Java vers .mcui de bout en bout : controller vers fichier, id vers handlers et refs, chemins vers signals et computed, composants et alias, slots, cle i18n, asset. Navigation, find usages, rename, ImplicitUsageProvider.
- Lot 4 : inspections. Annotator live miroir du resolver strict pour classe, tag, attribut et identifier inconnu. Inspections batch pour cle de traduction absente, asset absent, type de liaison value. Quickfixes evidents.
- Lot 5 : finition. Vue de structure, fil d'ariane, icones, formateur, reglages.

## Risques

- Cible mouvante : le .mcui n'est pas fige cote Yu (div pas livre, select absent, audit Tailwind en cours). Mitigation : le vocabulaire passe par le schema, donc une classe ou un tag nouveau n'exige pas de toucher le plugin. Le risque residuel est la grammaire d'un nouveau tag structurel ; la version 1 encaisse l'inconnu sans erreur plutot que de valider rigidement.
- Attributs non declaratifs cote Yu, voir Architecture.md. Sans la declaration, la validation d'attributs reste permissive.
- Le rename d'un chemin de signal touche du texte Java a l'interieur d'une valeur d'attribut .mcui : la plage de la reference doit etre exacte pour ne renommer que le segment concerne.

## Sources de recherche

- Custom Language Support : https://plugins.jetbrains.com/docs/intellij/custom-language-support.html
- Lexer et Parser : https://plugins.jetbrains.com/docs/intellij/lexer-and-parser-definition.html
- Language et File Type : https://plugins.jetbrains.com/docs/intellij/language-and-filetype.html
- References et Resolve : https://plugins.jetbrains.com/docs/intellij/references-and-resolve.html
- Reference Contributor : https://plugins.jetbrains.com/docs/intellij/reference-contributor.html
- Rename Refactoring : https://plugins.jetbrains.com/docs/intellij/rename-refactoring.html
- Language Injection : https://plugins.jetbrains.com/docs/intellij/language-injection.html
- Completion Contributor : https://plugins.jetbrains.com/docs/intellij/completion-contributor.html
- Code Inspections et Intentions : https://plugins.jetbrains.com/docs/intellij/code-inspections-and-intentions.html
- XML DOM API, pour l'alternative ecartee : https://plugins.jetbrains.com/docs/intellij/xml-dom-api.html
- IntelliJ Platform Gradle Plugin 2.x : https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
- Modele de projet : https://github.com/JetBrains/intellij-platform-plugin-template
- ImplicitUsageProvider : https://dploeger.github.io/intellij-api-doc/com/intellij/codeInsight/daemon/ImplicitUsageProvider.html
