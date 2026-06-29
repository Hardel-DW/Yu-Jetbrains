# Project Overview
Plugin IntelliJ Platform pour le langage .mcui de Yu. Projet separe, ce n'est pas un mod Fabric. Le but est le support d'edition des fichiers .mcui dans les IDE JetBrains, avec la navigation croisee Java vers .mcui au centre.

La documentation vit dans docs/SUMMARY.md (table des matieres). Il faut toujours la lire en debut de session, elle porte les regles et les fichiers importants.

# Stack
- Langage du plugin : Java. Build : IntelliJ Platform Gradle Plugin 2.x (id org.jetbrains.intellij.platform), taches runIde et verifyPlugin, pas de ./gradlew build facon mod.
- Grammaire .mcui : JFlex (lexer) plus Grammar-Kit (parser et PSI).
- Cible IDE : IntelliJ IDEA et derives, version a fixer au lot 0.

# Le moteur Yu (source de verite)
Le moteur vit dans repository\voxel.studio.mods\yu-ui (module Gradle yu-ui, namespace fr.hardel.yu). Le plugin ne reimplemente jamais le vocabulaire de Yu : tags, classes, couleurs, variants et familles de polices viennent d'un schema JSON exporte par Yu. La grammaire de syntaxe (.mcui) est stable ; le vocabulaire est dynamique via le schema.

Les specs du moteur a connaitre : yu-ui\docs\Overview.md, UtilityClasses.md, Architecture.md, Roadmap.md.

# Global Rules:
- No redundancy, we must avoid duplicating truth sources. Le vocabulaire ne se hardcode jamais, il vient du schema.
- No function/variable with a single line/reference. Except Getter/Setter.
- Avoid over engineering.
- No support of Legacy/Deprecated.
- A class should have a single dominant responsibility.
- Avoid dirty code / temporary code.
- It's better to tell me what you have in mind before doing it.
- Translation Key instead of Literal. Cote plugin, les chaines UI passent par un message bundle IntelliJ.
- Think long term, consider future scenarios.
- Don't lie, prefer the truth even when negative. Criticize my choices.
- Avoid useless comments.
- Prioritize OOP, correct patterns, static only when it fits.
- Avoid unchecked casts, find architectural solutions.
- Avoid inline imports. No commented code.
- Huge rename/move refactor : je le fais dans IntelliJ.
- Avoid tons of parameters, derive from the id when predictable.

# Long Term:
No leaving work for later. End-to-end, propre et complet. Plusieurs commits possibles, mais pas de raccourcis ni de hacks. On nettoie le code mort et la duplication. Qualite sur plusieurs annees, pas vitesse de livraison.
