# Yu pour JetBrains - Vue d'ensemble

Le plugin apporte le support d'édition des fichiers .mcui dans les IDE JetBrains. Le cœur est la navigation croisée Java vers .mcui dans les deux sens, qui débloque aussi le rename refactoring natif de l'IDE et fait taire les faux avertissements de variables et méthodes inutilisées côté Java. Le reste est de la complémentarité d'édition : coloration, complétion, fermeture automatique des tags, inspections live qui rejouent la rigueur du resolver de Yu.

Le plugin ne connaît aucun vocabulaire en dur. Les tags, les classes, les couleurs, les variants et les familles de polices viennent d'un schéma JSON exporté par le moteur Yu. La grammaire de syntaxe est stable et vit dans le plugin ; le vocabulaire est dynamique et vit dans le schéma, ce qui couvre aussi les classes et tags ajoutés par des mods tiers.

## Le périmètre

- Coloration syntaxique des tags, attributs, valeurs de classe, interpolations et mots de contrôle.
- Complétion : noms de tags et d'alias, attributs, jetons de classe avec préfixes de variants et couleurs de la palette, chemins de propriétés du controller.
- Fermeture automatique des tags, repliage, appariement de tag ouvrant et fermant.
- Navigation croisée Java vers .mcui et retour, find usages, rename refactoring.
- Suppression des faux inutilisés sur les membres Java référencés uniquement par le .mcui.
- Inspections : classe, tag, attribut ou identifier inconnu, comme le resolver strict de Yu ; clé de traduction absente ; asset absent ; incohérence de type sur une liaison value.

## Le graphe de références

C'est l'objet central du plugin. Chaque arête se résout dans le projet ouvert, qui contient à la fois les .mcui et les controllers Java.

- @UIController("ns:chemin") relie le controller au fichier assets/ns/yu/chemin.mcui. Le segment yu/ est le marqueur de la racine des ressources Yu.
- L'attribut id d'un élément relie aux annotations qui portent cet id (@OnClick, @OnPress, @OnRelease, @OnInput, @OnChange, @OnKey) et aux appels element("id") et instances("id"). C'est cet usage qui rend ces membres Java référencés.
- Les chemins réactifs ({{label}}, value="nickname", data-hot="isHot", test="!isHot", on="state", show, each="history", open, enabled) relient aux signal et computed du controller, résolus par chemin.
- Dans un template de liste, {{item.text}} et {{index}} résolvent contre l'item de boucle, pas contre le controller. Le résolveur de chemins porte cette notion de portée locale.
- Un usage de composant, par tag namespace comme studio:demo/card ou par alias PascalCase comme ReloadButton, relie au fichier .mcui du composant. L'alias se déclare par l'attribut alias sur la racine du composant.
- slot="titre" relie au slot nommé correspondant dans le composant cible.
- Hors Java, deux liaisons à haute valeur : key="cle.i18n" relie aux fichiers lang/*.json du pack, et src="ns:chemin" relie à l'asset SVG ou texture.

## Le principe directeur

Une seule source de vérité par sujet. La syntaxe est au plugin, le vocabulaire est au schéma de Yu, la structure du projet est lue par le PSI de l'IDE. Le plugin ne duplique ni la liste des classes, ni la table des couleurs, ni la grammaire des variants : il les reçoit.
