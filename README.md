# Create: Simulated Damages

[Français](#français)

A NeoForge mod for Minecraft 1.21.1 that adds progressive, physics-based block damage. It integrates with the physical sub-level collisions provided by Sable and used by Create: Aeronautics.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.247 or newer for Minecraft 1.21.1
- Create 6.0.10 or newer
- Create: Aeronautics 1.3.2 or newer
- Sable 2.0.6 or newer

All three content mods are required and are declared as dependencies in the mod metadata.

## Features

- Sable physics collisions progressively damage blocks. Blocks crack before breaking apart.
- Blocks supported by the physics system fall as debris when dislodged.
- Impacts from moving entities and repeated low-speed contacts wear down blocks and floors through friction.
- Blocks in the `create_simulated_damages:impact_explosives` tag explode after a sufficiently strong impact. TNT is included by default; data packs can add other blocks.
- Blocks in the `create_simulated_damages:explosion_ejectable` tag are launched as falling blocks during explosions instead of simply being destroyed.
- Block damage progress is saved with the world.

## Building

Install JDK 21 and run `gradlew.bat build`. The mod JAR is generated in `build/libs`.

To launch the development client, run `gradlew.bat runClient`.

---

## Français

Un mod NeoForge pour Minecraft 1.21.1 qui ajoute des dégâts progressifs liés à la physique. Il s'intègre aux collisions des sous-niveaux physiques de Sable utilisés par Create: Aeronautics.

## Prérequis

- Minecraft 1.21.1
- NeoForge 21.1.247 ou supérieur pour Minecraft 1.21.1
- Create 6.0.10 ou supérieur
- Create: Aeronautics 1.3.2 ou supérieur
- Sable 2.0.6 ou supérieur

Les trois mods de contenu sont obligatoires et déclarés comme dépendances dans les métadonnées du mod.

## Fonctionnalités

- Les collisions physiques de Sable endommagent progressivement les blocs. Ceux-ci se fissurent avant de se disloquer.
- Les blocs pris en charge par le système physique tombent en débris lorsqu'ils sont disloqués.
- Les chocs des entités en mouvement et les contacts répétés à faible vitesse usent les blocs et le sol par frottement.
- Les blocs du tag `create_simulated_damages:impact_explosives` explosent après un choc suffisamment violent. Le tag contient TNT par défaut ; les packs de données peuvent y ajouter d'autres blocs.
- Les blocs du tag `create_simulated_damages:explosion_ejectable` sont projetés sous forme de blocs tombants lors d'une explosion, au lieu d'être simplement détruits.
- La progression des dégâts est sauvegardée avec le monde.

## Compilation

Installez JDK 21, puis lancez `gradlew.bat build`. Le fichier JAR du mod est généré dans `build/libs`.

Pour lancer le client de développement, utilisez `gradlew.bat runClient`.
