<h1 align="center">[Under Construction]</h1>
<p align="center">This mod is very early in development, come back later if you want to use it!</p>

![Logo](src/main/resources/assets/blackout/textures/gui/banner.png)

## block game utility mod

![Minecraft](https://img.shields.io/badge/Minecraft-1.19.4-488321?style=flat-square)
[![Fabric](https://img.shields.io/badge/Mod_Loader-Fabric-DBD0B4?style=flat-square)](https://fabricmc.net/use/installer/)

![Downloads](https://img.shields.io/github/downloads/chell-dev/Blackout-3.0/total?style=flat-square)
![Lines of code](https://img.shields.io/tokei/lines/github/chell-dev/Blackout-3.0?label=Lines%20of%20code&style=flat-square&color=blueviolet)

<details>
<summary>Not to be confused with the Meteor addon called BlackOut</summary>

- The name is inspired by Watch Dogs
- I made the first Blackout back in March 2020
- I was very sad after finding out someone else used the name but I'm not changing it now

</details>

<br>

<details>
<summary>Screenshots</summary>

GUI

![GUI](assets/gui.gif)

Discord RPC

![RPC](assets/discord.png)

</details>

## Installation

[Click here](https://github.com/2qb/Blackout-3.0-Installer/releases/download/1.2/BlackoutInstaller.exe) to download the installer.

<details>
<summary>Manual installation</summary>

1. Install [Fabric](https://fabricmc.net/use/installer/) for Minecraft 1.19.4 (Fabric API is **not** required)
2. Download the latest release [here](https://github.com/chell-dev/Blackout-3.0/releases)
3. Put the downloaded .jar file in your `.minecraft/mods` folder

</details>

<details>
<summary>Recommended mods</summary>

- [MultiConnect](https://github.com/Earthcomputer/multiconnect/releases) to play on servers that use an older minecraft version
- [Sodium](https://www.curseforge.com/minecraft/mc-mods/sodium/download/4381988) to make the game playable
- [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu/download/4159524) if you want the Mods button that's in forge

</details>

## Usage

- Open the GUI by pressing the `\` key
- Left / Right / Middle Click buttons to configure everything
- If you forget your GUI bind you can:
  - Change it in your config file (`.minecraft/Blackout/Config.txt`) under the line `Feature: GUI Bind`
  - If you have Mod Menu installed, open the GUI by pressing the config button

### Building

<details>
<summary>Click here</summary>

`git clone https://github.com/chell-dev/Blackout-3.0.git` or download the repository

After building, the output `.jar` will be in `build/libs/`

#### IntelliJ (recommended), Eclipse or VSCode

1. Import the project - see https://fabricmc.net/wiki/tutorial:setup, refer to the section for your IDE
2. Run the `build` gradle task

#### Windows
1. Open `cmd` in the project folder
2. Run `./gradlew.bat build`

#### Linux and Mac
1. `cd` to the project folder
2. Run `./gradlew build`
</details>

### Thank you

- [Fabric](https://fabricmc.net/)
- [Reflections](https://github.com/ronmamo/reflections)
- [KDiscordIPC](https://github.com/caoimhebyrne/KDiscordIPC)
- [hack.chat](https://hack.chat/)
- [DevLogin](https://github.com/PlanetTeamSpeakk/DevLogin)
- [Installer](https://github.com/2qb/Blackout-3.0-Installer)
