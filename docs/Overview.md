# Yu pour JetBrains - Vue d'ensemble

Le plugin apporte le support d'edition des fichiers .mcui dans les IDE JetBrains. Le coeur est la navigation croisee Java vers .mcui dans les deux sens, qui debloque aussi le rename refactoring natif de l'IDE et fait taire les faux avertissements de variables et methodes inutilisees cote Java. Le reste est de la complementarite d'edition : coloration, completion, fermeture automatique des tags, inspections live qui rejouent la rigueur du resolver de Yu.

Le plugin ne connait aucun vocabulaire en dur. Les tags, les classes, les couleurs, les variants et les familles de polices viennent d'un schema JSON exporte par le moteur Yu. La grammaire de syntaxe est stable et vit dans le plugin ; le vocabulaire est dynamique et vit dans le schema, ce qui couvre aussi les classes et tags ajoutes par des mods tiers.

## Le perimetre

- Coloration syntaxique des tags, attributs, valeurs de classe, interpolations et mots de controle.
- Completion : noms de tags et d'alias, attributs, jetons de classe avec prefixes de variants et couleurs de la palette, chemins de proprietes du controller.
- Fermeture automatique des tags, repliage, appariement de tag ouvrant et fermant.
- Navigation croisee Java vers .mcui et retour, find usages, rename refactoring.
- Suppression des faux inutilises sur les membres Java references uniquement par le .mcui.
- Inspections : classe, tag, attribut ou identifier inconnu, comme le resolver strict de Yu ; cle de traduction absente ; asset absent ; incoherence de type sur une liaison value.

## Le graphe de references

C'est l'objet central du plugin. Chaque arete se resout dans le projet ouvert, qui contient a la fois les .mcui et les controllers Java.

- @UIController("ns:chemin") relie le controller au fichier assets/ns/yu/chemin.mcui. Le segment yu/ est le marqueur de la racine des ressources Yu.
- L'attribut id d'un element relie aux annotations qui portent cet id (@OnClick, @OnPress, @OnRelease, @OnInput, @OnChange, @OnKey) et aux appels element("id") et instances("id"). C'est cet usage qui rend ces membres Java references.
- Les chemins reactifs ({{label}}, value="nickname", data-hot="isHot", test="!isHot", on="state", show, each="history", open, enabled) relient aux signal et computed du controller, resolus par chemin.
- Dans un template de liste, {{item.text}} et {{index}} resolvent contre l'item de boucle, pas contre le controller. Le resolveur de chemins porte cette notion de portee locale.
- Un usage de composant, par tag namespace comme studio:demo/card ou par alias PascalCase comme ReloadButton, relie au fichier .mcui du composant. L'alias se declare par l'attribut alias sur la racine du composant.
- slot="titre" relie au slot nomme correspondant dans le composant cible.
- Hors Java, deux liaisons a haute valeur : key="cle.i18n" relie aux fichiers lang/*.json du pack, et src="ns:chemin" relie a l'asset SVG ou texture.

## Le principe directeur

Une seule source de verite par sujet. La syntaxe est au plugin, le vocabulaire est au schema de Yu, la structure du projet est lue par le PSI de l'IDE. Le plugin ne duplique ni la liste des classes, ni la table des couleurs, ni la grammaire des variants : il les recoit.
