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
| **Impacts** | Collisions above 4 blocks/second progressively crack and break blocks. Repeated impacts on the same block are throttled. |
| **Friction** | Repeated low-speed contacts wear down blocks over time. |
| **Disintegration** | Tagged blocks break free as falling debris. |
| **Impact explosions** | Tagged explosive blocks detonate after an impact above 8 blocks/second. TNT is included by default. |
| **Explosion debris** | Selected blocks are launched as falling blocks instead of being destroyed by an explosion. |
| **Persistent damage** | Block damage is saved with the world. |

Block behavior can be extended with data packs using these block tags:

- `create_simulated_damages:dislocatable`
- `create_simulated_damages:impact_explosives`
- `create_simulated_damages:explosion_ejectable`

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
| **Chocs** | Les collisions dépassant 4 blocs/seconde fissurent progressivement les blocs, puis les brisent. Les chocs répétés sur un même bloc sont limités. |
| **Frottements** | Les contacts répétés à faible vitesse usent les blocs avec le temps. |
| **Dislocation** | Les blocs marqués se détachent et tombent en débris. |
| **Explosions au choc** | Les blocs explosifs marqués détonent après un impact dépassant 8 blocs/seconde. Le TNT est inclus par défaut. |
| **Débris d'explosion** | Certains blocs sont projetés sous forme de blocs tombants au lieu d'être détruits par une explosion. |
| **Dégâts persistants** | Les dégâts des blocs sont sauvegardés avec le monde. |

Les comportements peuvent être étendus avec des packs de données à l'aide des tags suivants :

- `create_simulated_damages:dislocatable`
- `create_simulated_damages:impact_explosives`
- `create_simulated_damages:explosion_ejectable`

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
