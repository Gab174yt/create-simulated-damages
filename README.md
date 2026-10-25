# Create: Simulated Damages

Mod NeoForge pour Minecraft 1.21.1 qui ajoute des dégâts physiques progressifs aux blocs. Il s'intègre aux collisions des sous-niveaux physiques de Sable, utilisés par Create: Aeronautics.

## Prérequis

- Minecraft 1.21.1
- NeoForge 21.1.247 ou supérieur pour Minecraft 1.21.1
- Create 6.0.10 ou supérieur
- Create: Aeronautics 1.3.2 ou supérieur
- Sable 2.0.6 ou supérieur

Les trois mods de contenu sont obligatoires et sont déclarés dans les métadonnées du mod.

## Fonctionnalités

- Les collisions des constructions physiques de Sable endommagent progressivement les blocs touchés. Les blocs fissurent avant de se disloquer.
- Les blocs pris en charge par la physique tombent en débris lorsqu'ils sont disloqués.
- Les chocs des entités en mouvement et les contacts répétés à faible vitesse usent les blocs et le sol par frottement.
- Les blocs du tag `create_simulated_damages:impact_explosives` explosent après un choc suffisamment violent. Le tag contient TNT par défaut et peut être étendu par un pack de données.
- Les blocs du tag `create_simulated_damages:explosion_ejectable` sont éjectés sous forme de blocs tombants lors d'une explosion au lieu d'être détruits.
- Le progrès des dégâts est sauvegardé dans les données du monde.

## Construction

Installer un JDK 21, puis lancer `gradlew.bat build`. Le JAR est produit dans `build/libs`.

Pour lancer le client de développement, utiliser `gradlew.bat runClient`.
