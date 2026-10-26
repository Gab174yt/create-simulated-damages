<div align="center">

# Create: Simulated Damages

**Physics-based block damage for Minecraft.**

Cracks, friction, flying debris, and explosive impacts —<br>
integrated with the moving structures of Create: Aeronautics.

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-56A64B?style=flat-square&logo=minecraft&logoColor=white)](https://www.minecraft.net/)
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1.247+-E86F2C?style=flat-square)](https://neoforged.net/)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-397D54?style=flat-square)](LICENSE)

**English** · [Français](#français)

</div>

## Overview

Create: Simulated Damages adds progressive, persistent physical damage to blocks. It hooks into Sable's sub-level collision callbacks to bring physics damage to the moving contraptions used by Create: Aeronautics.

## Features

| Effect | What happens |
| --- | --- |
| **Impacts** | Collision damage scales with impact speed and Sable block mass. High-speed impacts deal half damage per block while affecting a larger area. Repeated impacts on the same block are throttled. |
| **Friction** | Repeated contact wears blocks according to their speed, Sable mass, and Sable friction properties. |
| **Disintegration** | Tagged blocks break free as falling debris. |
| **Impact explosions** | Blocks in the `create_simulated_damages:impact_explosives` tag detonate when struck above 8 blocks/second. TNT is included by default; data packs can add other blocks. |
| **Explosion debris** | By default, 50% of affected blocks are ejected as separate Sable sub-levels and launched upward and outward before the explosion can destroy them. Nearby blocks are grouped when the per-explosion sub-level limit is reached. |
| **Block resistance** | Each block resists damage according to its hardness; fragile blocks such as glass have a configurable resistance multiplier. |
| **Gradual repair** | Damaged blocks recover one crack level every 10 seconds by default. |
| **Persistent damage** | Block damage and repair progress are saved with the world. |

Block behavior can be extended with data packs using these block tags:

- `create_simulated_damages:dislocatable`
- `create_simulated_damages:impact_explosives`
- `create_simulated_damages:impact_fragile`

Blocks in `impact_fragile` take more impact damage. Add other brittle blocks to this tag with a data pack.

## Configuration

Open **Mods → Create: Simulated Damages → Config** to adjust impact damage and thresholds, the maximum damage radius of high-speed impacts, the percentage of explosion blocks ejected as sub-levels, the maximum number of sub-levels created per explosion, fragile block resistance, friction wear, and repair time. High-speed impacts affect a larger area but deal half damage to each block. Damage is stored as ten crack levels; a block breaks at level ten and repairs by one level after each configured interval. Block hardness, mass, and Sable friction properties are accounted for.

## Requirements

All of the following are required on both the client and server:

- Minecraft **1.21.1**
- NeoForge **21.1.247 or newer**
- Create **6.0.10 or newer**
- Create: Aeronautics **1.3.2 or newer**
- Sable **2.0.6 or newer**

## Build from source

Install **JDK 21**, then run:

```bat
gradlew.bat build
```

The mod JAR is created in `build/libs`. To start the development client, run:

```bat
gradlew.bat runClient
```

---

## Français

<div align="center">

**Des dégâts physiques pour les blocs de Minecraft.**

Fissures, frottements, débris projetés et explosions au choc —<br>
en harmonie avec les structures mobiles de Create: Aeronautics.

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-56A64B?style=flat-square&logo=minecraft&logoColor=white)](https://www.minecraft.net/)
[![NeoForge](https://img.shields.io/badge/NeoForge-21.1.247+-E86F2C?style=flat-square)](https://neoforged.net/)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Licence : MIT](https://img.shields.io/badge/Licence-MIT-397D54?style=flat-square)](LICENSE)

[English](#create-simulated-damages) · **Français**

</div>

## Présentation

Create: Simulated Damages ajoute des dégâts physiques progressifs et persistants aux blocs. Le mod s'intègre aux callbacks de collision des sous-niveaux de Sable pour appliquer ces dégâts aux constructions mobiles utilisées par Create: Aeronautics.

## Fonctionnalités

| Effet | Comportement |
| --- | --- |
| **Chocs** | Les dégâts dépendent de la vitesse d'impact et de la masse du bloc selon Sable. Les chocs rapides touchent une zone plus large, mais infligent deux fois moins de dégâts à chaque bloc. Les chocs répétés sur un même bloc sont limités. |
| **Frottements** | Les contacts répétés usent les blocs selon leur vitesse, leur masse et leurs propriétés de friction Sable. |
| **Dislocation** | Les blocs marqués se détachent et tombent en débris. |
| **Explosions au choc** | Les blocs du tag `create_simulated_damages:impact_explosives` détonent lorsqu'ils sont frappés à plus de 8 blocs/seconde. Le TNT est inclus par défaut ; les packs de données peuvent ajouter d'autres blocs. |
| **Débris d'explosion** | Par défaut, 50 % des blocs touchés sont éjectés vers le haut et vers l'extérieur dans des sous-niveaux Sable séparés avant que l'explosion ne les détruise. Les blocs proches sont regroupés lorsque la limite de sous-niveaux par explosion est atteinte. |
| **Résistance des blocs** | Chaque bloc résiste selon sa dureté ; les blocs fragiles comme le verre ont un multiplicateur de résistance configurable. |
| **Réparation progressive** | Par défaut, les blocs récupèrent un niveau de fissure toutes les 10 secondes. |
| **Dégâts persistants** | Les dégâts et la réparation sont sauvegardés avec le monde. |

Les comportements peuvent être étendus avec des packs de données à l'aide des tags suivants :

- `create_simulated_damages:dislocatable`
- `create_simulated_damages:impact_explosives`
- `create_simulated_damages:impact_fragile`

Les blocs dans `impact_fragile` reçoivent davantage de dégâts de choc. Ajoutez-y d'autres blocs cassants avec un pack de données.

## Configuration

Ouvrez **Mods → Create: Simulated Damages → Config** pour régler les dégâts et seuils de choc, le rayon maximal des gros impacts, le pourcentage de blocs d'explosion éjectés en sous-niveaux, le nombre maximal de sous-niveaux créés par explosion, la résistance des blocs fragiles, l'usure par frottement et le délai de réparation. Les chocs rapides affectent une zone plus large, mais infligent deux fois moins de dégâts à chaque bloc. Les dégâts sont enregistrés en dix niveaux de fissure ; le bloc casse au niveau dix et récupère un niveau après chaque intervalle configuré. La dureté, la masse et les propriétés de friction Sable sont prises en compte.

## Prérequis

Tous les éléments suivants sont requis côté client et côté serveur :

- Minecraft **1.21.1**
- NeoForge **21.1.247 ou supérieur**
- Create **6.0.10 ou supérieur**
- Create: Aeronautics **1.3.2 ou supérieur**
- Sable **2.0.6 ou supérieur**

## Compiler depuis les sources

Installez **JDK 21**, puis lancez :

```bat
gradlew.bat build
```

Le fichier JAR du mod est créé dans `build/libs`. Pour lancer le client de développement, utilisez :

```bat
gradlew.bat runClient
```

[↑ Retour en haut](#create-simulated-damages)
