# NeoForgematica Printer

An unofficial **NeoForge** port of the [Litematica Printer](https://github.com/aleksilassila/litematica-printer) mod for Minecraft **1.21.1**.

Automatically places blocks according to an active Litematica schematic – just walk around your loaded schematic and the printer places every block in reach for you.

---

## Requirements

| Dependency | Version |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.x |
| [Litematica (NeoForge)](https://github.com/ThinkingStudios/Litematica-Forge) | 0.4.0-mc1.21.1 |
| [MaLiLib (NeoForge)](https://github.com/ThinkingStudios/MaLib-Forge) | 0.4.3-mc1.21.1 |

---

## Features

- **Auto-placement** of blocks within a configurable reach range
- **Smart orientation** – player yaw/pitch is faked server-side to place directional blocks (barrels, dispensers, observers, etc.) in the correct facing
- **Optional Air Place (Experimental)** – can directly place to target positions without a clickable support block for non-support-dependent blocks
- **Sneak-placement** for container blocks (chests, barrels, shulker boxes, furnaces, hoppers, droppers, dispensers) – prevents accidentally opening the inventory after placement
- **Trapdoor & door delay** – a short cooldown is injected after placing trapdoors/doors to avoid the state being re-toggled immediately
- **Chest pairing** – single and double chests are placed correctly even when a neighbour chest is already present
- **Log stripping** – places a regular log and strips it automatically if a stripped variant cannot be found in the inventory
- **Fluid replacement** – optionally replaces fluid source blocks
- **Block state interaction** – cycles block states (comparators, repeaters, note blocks, levers …) to match the schematic
- **Built-in HUD overlay** – a configurable Jade-style tooltip displays the required block and target state when looking at a schematic position

---

## Configuration

All settings are accessible via **Litematica → Config → Generic** (and the **Info Overlays** tab for the HUD).

### Generic

| Setting | Default | Description |
|---|---|---|
| Printing Interval | `12` | Ticks between each placement attempt. Lower = faster. Raise if ghost blocks appear or blocks face the wrong way. |
| Printing Range | `5` | Max reach distance for placement (2.5–5 blocks). Lower values are safer on servers. |
| Printing Mode | `false` | Keep the printer active at all times (no hotkey required). |
| Printing Debug | `false` | Verbose debug logging to the game log. |
| Replace Fluid Source Blocks | `true` | Allow the printer to replace fluid source blocks. |
| Strip Logs | `true` | Use a regular log + axe if stripped logs are unavailable. |
| Interact Blocks | `true` | Allow the printer to cycle block states (comparator delay, repeater delay, …). |
| Block Orientation Fix (Experimental) | `false` | Extra validation pass for orientation-sensitive blocks. Reduce misfaced placements at the cost of speed. |
| Disable Placing Doors/Trapdoors/etc. | `false` | Completely skip door-like blocks (doors, trapdoors, fence gates). |
| Disable Placing Chests/Barrels/etc. | `false` | Completely skip storage blocks (chests, barrels, shulker boxes). |
| Disable Placing Stairs | `false` | Completely skip all stair blocks. |
| Disable Placing Slabs | `false` | Completely skip all slab blocks. |
| Allow Air Place (Experimental) | `false` | Allow direct target-position placement without requiring a clickable support block for blocks that do not require support. |

### Info Overlays (HUD)

| Setting | Default | Description |
|---|---|---|
| Use Matica Overlay | `true` | Show the built-in schematic helper overlay. |
| Font Size | `9` | Text size of the overlay (6–24). |
| Background Color | `#101318` | Background fill colour. |
| Font Color | `#E6ECF2` | Text colour. |
| Position | `Top Center` | Screen position (Top/Bottom × Left/Center/Right). |
| Transparency | `204` | Additional background transparency (0 = opaque, 255 = invisible). |

---

## Hotkeys

| Hotkey | Default | Description |
|---|---|---|
| Print | *(unbound)* | Hold to print while the key is pressed. |
| Toggle Printing Mode | *(unbound)* | Toggle auto-print mode on/off. |

Hotkeys are configured under **Litematica → Config → Hotkeys**.

---

## Usage

1. Load a schematic in Litematica and activate a placement.
2. Either hold the **Print** hotkey or enable **Printing Mode**.
3. Walk near the blocks that need to be placed – the printer fills them in automatically.

> **Tip:** If blocks are placed with the wrong orientation, try increasing the **Printing Interval**. The default value of `12` is a good starting point for most setups.

---

## Known Limitations

- **Placing liquids** (water/lava source placement) is not fully implemented yet.
- **Rails** use a conservative algorithm; in complex cases some rails may be skipped to avoid incorrect placements.
- **Optional Air Place** is experimental and can be more sensitive to server behavior and edge-case block states.
- **Legit mode / anticheat-friendly behavior** is not yet a dedicated mode and may need manual tuning (for example higher printing interval).

---

## Development Note

This project uses **GitHub Copilot** during development.

Even with reviews and testing, bugs, regressions, or edge-case placement issues can still occur.
If you find an issue, please report it with reproduction steps so it can be fixed quickly.

---

## Credits

- **aleksilassila** – original Litematica Printer (Fabric)
- **TexTrue / ThinkingStudio** – NeoForge port of Litematica & MaLiLib
- This project builds on their work

---

## Source Code and License

This fork is released under the GNU Affero General Public License v3.0.

Source code:
https://github.com/ThinkingStudio/ForgematicaPrinter
