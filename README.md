# D&D Initiative Tracker

![Java](https://img.shields.io/badge/Language-Java-orange.svg)
![Build](https://img.shields.io/badge/Build-Maven-blue.svg)
![UI](https://img.shields.io/badge/UI-Swing%20%7C%20FlatLaf%20%7C%20MigLayout-informational.svg)

A desktop combat managment and inititive tracking applicvation for D&D. designed for DMs, it automates turn order, resolves ties, tracks NPC hit points and manages death saves.

## Table of Contents
* Getting Started
* Features
* Usage Guide
* Project Structure
* Future Plans
* Authors


## Getting Started
### Via repo
#### Prerequisites
Ensure you have the following installed on your machine:

* Java Development Kit (JDK): Version 17 or higher
 ```bash
java -version
```
* Apache Maven:
```bash
mvn -version
```
* Git

#### Installation & Running
##### Via repository
1. Clone the reopository
```bash
git clone https://github.com/EubiWan/InitiativeTracker

cd <your-repository-folder>

```
2. Comple and run using Maven
```
mvn clean compile exec:java -Dexec.mainClass="com.eubi.initiativetracker.Main"
```
or
```
mvn clean package
```
and run it from the target/ directory
##### Via Releases
go to https://github.com/EubiWan/InitiativeTracker/releases and install the latest version for your device.


## Features
