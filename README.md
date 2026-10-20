# D&D Initiative Tracker

![Java](https://img.shields.io/badge/Language-Java-orange.svg)
![Build](https://img.shields.io/badge/Build-Maven-blue.svg)
![UI](https://img.shields.io/badge/UI-Swing%20%7C%20FlatLaf%20%7C%20MigLayout-informational.svg)

A desktop combat managment and inititive tracking applicvation for D&D. designed for DMs, it automates turn order, resolves ties, tracks NPC hit points and manages death saves.

## Table of Contents
* Installation & Running
* Features
* Project Structure
* Authors


## Installation & Running
### Via repository
#### Prerequisites
Ensure you have the following installed on your machine:

* Java Development Kit (JDK): Version 17 or higher (21 recommended):
  ```java -version```
* Apache Maven:
  ```mvn -version ```
* Git:
  ```git --version```
1. Clone the reopository

Windows:
``` shell
cd C:/Users/YourUsername/Documents/
git clone https://github.com/EubiWan/InitiativeTracker
cd C:/Users/YourUsername/Documents/InitiativeTracker/
```

Linux:
``` bash
cd home/YourUsername/Documents/
git clone https://github.com/EubiWan/InitiativeTracker
cd home/YourUsername/Documents//InitiativeTracker/
```

2. Comple and run using Maven

```mvn clean compile exec:java -Dexec.mainClass="com.eubi.initiativetracker.Main"```

or

```mvn clean package```

and run it from the target/ directory
### Via Releases
Go to [Releases](https://github.com/EubiWan/InitiativeTracker/releases) and install the latest version for your device.


## Features
* Automatic Initiative & tie-Braker Calaculation
* Circular Turn Queue & active Highlighting
* Dynamic Death Saving Throws
* NPC Health managment
* Live Mid-Combat Editing
* Batch Adding & Removal
* Input Validation

## Project Structure

```
src/main/java/com/eubi/initiativetracker/
├── characters/
│   ├── Character.java              # Base combatant model with composite initiative math and death saves
│   ├── CharacterManager.java       # Roster collection, stream sorting, and circular turn rotation
│   ├── CharacterType.java          # Enum for alignment categories (PLAYER, ENEMY, ALLY)
│   ├── DeathStatus.java            # Enum for health states (ALIVE, DOWN, DEAD)
│   └── NonePlayerCharacter.java    # NPC/Enemy subclass adding current/max HP and auto-death triggers
├── swingui/
│   ├── AddCharacterMenu.java       # Modal dialog for creating combatants with duplicate name validation
│   ├── CharacterCellRenderer.java  # Custom JList renderer for turn highlighting and death save pips
│   ├── EditCharacterMenu.java      # Modal dialog for updating initiative, dexterity, and status
│   ├── EditNPCHealth.java          # Modal dialog for bounded damage/healing (-/+) adjustments
│   ├── MainMenu.java               # Primary JFrame window hosting toolbars, buttons, and initiative list
│   ├── RemoveCharacterMenu.java    # Modal dialog for removing single characters, NPCs, or all records
│   └── UIManager.java              # Controller synchronizing Swing UI events with the backend manager
└── Main.java
```

## Authors
* [EubiWan](https://github.com/EubiWan) - UI, Semi-Backend
* [C0d3-404](https://github.com/c0d3-404) - Backend, Bridge, Documentation
