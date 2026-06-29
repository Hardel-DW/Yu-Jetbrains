# Yu pour JetBrains - Roadmap et constat

Ce document est la trace des décisions et le plan de travail. Les specs sont Overview.md et Architecture.md dans docs.

## Doctrine

- Une seule source de vérité par sujet. Le vocabulaire de Yu ne se hardcode jamais dans le plugin, il vient du schéma exporté.
- Qualité long terme. Chaque sujet se discute avant de se coder. Pas de code mort, dupliqué ou bricolé.
- La grammaire de syntaxe est stable et vit dans le plugin. Le vocabulaire est dynamique et vit dans le schéma.
- Le plugin est générique : il sert n'importe quel projet consommant Yu, pas seulement Studio.

## Décisions actées le 2026-06-28

- Repo séparé Yu-Jetbrains, hors du workspace voxel.studio.mods.
- Pont schéma, plein auto en deux temps : baseline du vocabulaire intégré sérialisé dans le jar yu-ui (instantané, sans lancement), plus un snapshot du registre fusionné complet écrit par Yu au démarrage du client en dev (sans commande, capte les mods tiers). Le plugin lit et fusionne les deux. La fusion est gratuite : au runtime le registre partagé est déjà la fusion. Limite incompressible : capter un vocabulaire enregistré par code exige un lancement.
- La navigation croisée Java vers .mcui dans les deux sens est le cœur, avec le rename refactoring natif et la suppression des faux inutilisés.
- Parsing : langage custom JFlex plus Grammar-Kit, pas de réutilisation du PSI XML. Le .mcui est du XML à environ quatre-vingt pour cent, mais ses subtilités (slash dans les noms de tags de composants, interpolation, attribut class) justifient le langage custom.
- Langage du plugin : Java.
- Cible IDE : IntelliJ Platform 2026.1 minimum (since-build 261), développée contre la dernière patch 2026.1.3, until-build sur la branche 2026.1.x. IDEA Community suffit, le module Java y est présent. Le bytecode et le runtime du plugin suivent le JBR de 2026.1, à confirmer au lot 0 contre le SDK réel (vraisemblablement Java 21, pas le Java 17 supposé à l'audit). Le Java 25 du mod ne change pas le runtime du plugin.

## Recommandations à valider avant de coder

- Attributs par tag : Yu n'a pas de déclaration d'attributs déclarative. Reco : faire grandir cette déclaration dans Yu pour que l'export la porte, et traiter les attributs de façon permissive en version 1. Le design de cette déclaration se fait quand le lot 2 du plugin la réclame, informé par le besoin réel, pas à l'aveugle.

## Audit Codex du 2026-06-28

Audit externe du plan avant le code. Verdict : les grands choix sont bons (repo séparé, Java, JFlex plus Grammar-Kit, vocabulaire par schéma, pas de PSI XML). Le défaut principal est l'ordre : le plan repousse trop tard les preuves qui peuvent invalider le PSI initial. Les contraintes de plateforme détaillées sont dans Architecture.md.

- Le vrai MVP technique n'est pas la coloration complète, c'est un PSI minimal qui supporte l'injection, la référence, le rename et le schéma absent sans mentir. La forme du PSI (hosts d'injection, PsiNamedElement, manipulators) est dictée par ces besoins, donc décidée dès le lot 1.
- Reco actée : insérer avant le lot 2 complet deux spikes de preuve, un d'injection et un de référence, qui démontrent attribut hostable, interpolation hostable, annotation Java vers fichier .mcui et id .mcui vers méthode Java. Pas la feature finale, juste valider les choix de PSI du lot 1.
- Le pont schéma est l'autre zone dure : sans protocole de version et sans stratégie de démarrage à froid, le plugin paraîtra aléatoire dès le premier usage. Lecture par le modèle de projet et le VFS, pas le classloader du plugin (corrigé dans Architecture.md).
- La fermeture auto complète dépend du schéma (certains tags sont structurellement vides), donc elle n'est pleinement validable qu'avec le vocabulaire, pas au seul lot 1.

## Décisions à trancher avant Lot 0 et Lot 1

- Format du schema.json : version, provenance, règle de fusion baseline plus snapshot, conflits, dépréciations, types d'attributs, alias, slots, variants, couleurs, polices.
- Compatibilité de schéma : version minimale et maximale de Yu supportée, comportement face à un schéma plus récent ou plus ancien, avertissement contre mode permissif.
- Multi-module Gradle : quel module possède quel namespace, quel yu-ui gagne si plusieurs versions coexistent, quels controllers sont visibles depuis quel resource pack.
- Snapshot runtime : chemin exact, configuration de lancement concernée, écriture atomique, rafraîchissement du VFS, invalidation, affichage de la source active.
- Caching : dépendances de cache sur les roots, le VFS, la modification du PSI et le DumbService.

## Plan en lots

- Lot 0 : socle. Projet Gradle 2.x, plugin.xml minimal, McuiFileType et McuiLanguage vides, runIde qui ouvre un projet, verifyPlugin vert.
- Lot 1 : langage. Lexer flex, grammaire bnf, PSI généré, coloration, fermeture automatique des tags, appariement, repliage, commentaires. Le PSI intègre dès le départ les contraintes de plateforme (hosts d'injection, PsiNamedElement et manipulators pour le rename, WordsScanner, tolérance pin et recoverWhile), voir Architecture.md. Le .mcui s'édite avec couleurs et structure, sans connaissance du vocabulaire.
- Spikes avant le lot 2 : une preuve d'injection et une preuve de référence Java vers .mcui et retour, pour valider la forme du PSI du lot 1 avant d'investir dans le schéma et les features.
- Lot 2 : schéma et complétion. Export côté yu-ui, service de schéma côté plugin, injection des langages class et expr, complétion des tags, des jetons de classe avec variants et couleurs, des attributs communs.
- Lot 3 : références. Le graphe Java vers .mcui de bout en bout : controller vers fichier, id vers handlers et refs, chemins vers signals et computed, composants et alias, slots, clé i18n, asset. Navigation, find usages, rename, ImplicitUsageProvider.
- Lot 4 : inspections. Annotator live miroir du resolver strict pour classe, tag, attribut et identifier inconnu. Inspections batch pour clé de traduction absente, asset absent, type de liaison value. Quickfixes évidents.
- Lot 5 : finition. Vue de structure, fil d'ariane, icônes, formateur, réglages.

## Risques

- Cible mouvante : le .mcui n'est pas figé côté Yu (div pas livré, select absent, audit Tailwind en cours). Mitigation : le vocabulaire passe par le schéma, donc une classe ou un tag nouveau n'exige pas de toucher le plugin. Le risque résiduel est la grammaire d'un nouveau tag structurel ; la version 1 encaisse l'inconnu sans erreur plutôt que de valider rigidement.
- Attributs non déclaratifs côté Yu, voir Architecture.md. Sans la déclaration, la validation d'attributs reste permissive.
- Le rename d'un chemin de signal touche du texte Java à l'intérieur d'une valeur d'attribut .mcui : la plage de la référence doit être exacte pour ne renommer que le segment concerné.

## Sources de recherche

- Custom Language Support : https://plugins.jetbrains.com/docs/intellij/custom-language-support.html
- Lexer et Parser : https://plugins.jetbrains.com/docs/intellij/lexer-and-parser-definition.html
- Langage et File Type : https://plugins.jetbrains.com/docs/intellij/language-and-filetype.html
- Références et Resolve : https://plugins.jetbrains.com/docs/intellij/references-and-resolve.html
- Reference Contributor : https://plugins.jetbrains.com/docs/intellij/reference-contributor.html
- Rename Refactoring : https://plugins.jetbrains.com/docs/intellij/rename-refactoring.html
- Language Injection : https://plugins.jetbrains.com/docs/intellij/language-injection.html
- Completion Contributor : https://plugins.jetbrains.com/docs/intellij/completion-contributor.html
- Code Inspections et Intentions : https://plugins.jetbrains.com/docs/intellij/code-inspections-and-intentions.html
- XML DOM API, pour l'alternative écartée : https://plugins.jetbrains.com/docs/intellij/xml-dom-api.html
- IntelliJ Platform Gradle Plugin 2.x : https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
- Modèle de projet : https://github.com/JetBrains/intellij-platform-plugin-template
- ImplicitUsageProvider : https://dploeger.github.io/intellij-api-doc/com/intellij/codeInsight/daemon/ImplicitUsageProvider.html
