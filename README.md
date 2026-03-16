# Forgematica Printer Reworked

An open-source NeoForge add-on for Forgematica that adds automatic schematic printing and improves placement behavior for directional and state-based blocks.

Forgematica Printer Reworked is a maintained fork of the original Forgematica Printer project. It is designed to help players build large schematics faster by automatically placing the correct blocks around the player while improving reliability in more complex placement scenarios.

## Features

- automatic schematic block placement for Forgematica
- configurable printing speed and range
- improved support for directional and state-based blocks
- maintained for NeoForge
- open-source fork with ongoing fixes and adjustments

## How To Use

Using the printer is straightforward:

- Toggle the printer with `CAPS_LOCK` by default
- Hold `V` by default to print temporarily, even if the printer is turned off
- Open Forgematica settings with `M + C`
- Printer-related settings can be found in the **Generic** tab
- The printer toggle key can be changed in the **Hotkeys** tab

## Fork Notice

This project is a modified open-source fork based on the original Forgematica Printer work and related upstream projects.

This fork is maintained independently. Please do not report issues from this version to the original upstream authors or to the original creator of Litematica.

## Changes in this fork

This fork focuses on improving placement reliability and maintenance for newer NeoForge versions.

Current improvements include:

- improved stair placement direction handling
- improved slab placement behavior in connected structures
- improved trapdoor placement and interaction behavior
- NeoForge 1.21.1 build support and maintenance updates
- ongoing fixes for schematic auto-printing edge cases

## Reporting Issues

If you encounter a bug or want to request a feature, please use the issue tracker for this fork:

[GitHub Issues](https://github.com/ThinkingStudio/ForgematicaPrinter/issues)

Before creating an issue, please make sure you are using the latest available version and include as much useful information as possible, such as:

- Minecraft version
- Forgematica version
- Printer version
- a clear description of how to reproduce the problem
- logs, screenshots, or incorrectly printed schematics if available

## Known Issues

The following limitations are still known:

- placing liquids is not fully supported, although printing inside liquids may still work
- printing unsupported blocks directly in mid-air (`printInAir`) is still incomplete
- rail placement is not perfect in every situation and may refuse to place rails rather than place them incorrectly
- compatibility with strict anti-cheat environments is still uncertain

Features intended to repair or rewrite existing builds, such as automatic excavation or automatic correction of wrongly placed blocks, are currently outside the scope of this fork.

## Source Code and License

This fork is released under the GNU Affero General Public License v3.0.

Source code:
https://github.com/ThinkingStudio/ForgematicaPrinter