# Project Overview
Plugin IntelliJ Platform pour le langage .mcui de Yu. Projet séparé, ce n'est pas un mod Fabric. Le but est le support d'édition des fichiers .mcui dans les IDE JetBrains, avec la navigation croisée Java vers .mcui au centre.

La documentation vit dans docs/SUMMARY.md (table des matières). Il faut toujours la lire en début de session, elle porte les règles et les fichiers importants.

# Stack
- Langage du plugin : Java. Build : IntelliJ Platform Gradle Plugin 2.x (id org.jetbrains.intellij.platform), tâches runIde et verifyPlugin, pas de ./gradlew build façon mod.
- Grammaire .mcui : JFlex (lexer) plus Grammar-Kit (parser et PSI).
- Cible IDE : IntelliJ IDEA et dérivés, version à fixer au lot 0.

# Le moteur Yu (source de vérité)
Le moteur vit dans repository\voxel.studio.mods\yu-ui (module Gradle yu-ui, namespace fr.hardel.yu). Le plugin ne réimplémente jamais le vocabulaire de Yu : tags, classes, couleurs, variants et familles de polices viennent d'un schéma JSON exporté par Yu. La grammaire de syntaxe (.mcui) est stable ; le vocabulaire est dynamique via le schéma.

Les specs du moteur à connaître : yu-ui\docs\Overview.md, UtilityClasses.md, Architecture.md, Roadmap.md.

# Global Rules:
- No redundancy, we must avoid duplicating truth sources. Le vocabulaire ne se hardcode jamais, il vient du schéma.
- No function/variable with a single line/reference. Except Getter/Setter.
- Avoid over engineering.
- No support of Legacy/Deprecated.
- A class should have a single dominant responsibility.
- Avoid dirty code / temporary code.
- It's better to tell me what you have in mind before doing it.
- Translation Key instead of Literal. Côté plugin, les chaînes UI passent par un message bundle IntelliJ.
- Think long term, consider future scenarios.
- Don't lie, prefer the truth even when negative. Criticize my choices.
- Avoid useless comments.
- Prioritize OOP, correct patterns, static only when it fits.
- Avoid unchecked casts, find architectural solutions.
- Avoid inline imports. No commented code.
- Huge rename/move refactor : je le fais dans IntelliJ.
- Avoid tons of parameters, derive from the id when predictable.

# Long Term:
No leaving work for later. End-to-end, propre et complet. Plusieurs commits possibles, mais pas de raccourcis ni de hacks. On nettoie le code mort et la duplication. Qualité sur plusieurs années, pas vitesse de livraison.
