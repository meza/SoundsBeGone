# Sounds Be Gone! [![Crowdin](https://badges.crowdin.net/sounds-be-gone/localized.svg)](https://crowdin.com/project/sounds-be-gone)

[![Modrinth](https://img.shields.io/modrinth/dt/soundsbegone?logo=modrinth&label=Modrinth)](https://modrinth.com/mod/soundsbegone)
[![CurseForge](https://img.shields.io/curseforge/dt/874633?logo=curseforge&label=CurseForge)](https://www.curseforge.com/minecraft/mc-mods/soundsbegone)  
[![](https://dcbadge.limes.pink/api/server/dvg3tcQCPW)](https://discord.gg/dvg3tcQCPW)

Inspired by my own misophonia, Sounds Be Gone keeps Minecraft playable when specific noises are overwhelming. The mod passively records every sound you have heard in the past minute and lets you mute any of them permanently with a couple of clicks. Everything is client-side, so your preferences affect only your game.

### _Fabric is supported on releases below 1.20. NeoForge builds are available for Minecraft 1.20.4 and newer._

## Why players use Sounds Be Gone

- Instantly silence any sound event (vanilla or modded) without memorizing filenames.
- The "recently played" list removes guesswork—scroll, select, and mute.
- Keeps an audit trail so you can easily unmute later when you change your mind.
- Built for sensory accessibility: predictable UI, reversible actions, and zero server requirements.

## Infrequent Sounds!

Some sounds are useful in small doses but miserable when Minecraft repeats them constantly. Mark those sounds as **Infrequent** instead of disabling them.

Infrequent sounds still play sometimes. Sounds Be Gone checks each playback attempt and only lets a percentage of those attempts through. The default frequency is `10%`, and you can change it from the **Infrequent sounds** category in the config screen.

This is not just a flat random toggle. When the same sound fires repeatedly, Sounds Be Gone counts recent attempts for that sound and lowers the chance for each attempt in the burst. A sound that only happens occasionally stays close to your configured percentage, while a sound that is spamming the client is suppressed more aggressively.

Use **Infrequent** for sounds where silence would remove useful feedback, but normal playback is too much. Examples include repeated ambient loops, machine noises, villager chatter, or any modded sound that you only want to hear once in a while.

## Quick start

### Minecraft 1.20+

1. When you hear an unwanted sound, press `B` (default keybind) to open the Sound Sanctuary.
2. Pick the sound(s) from the "Played in the last 60 seconds" list.
3. Toggle them off to mute permanently; the change saves immediately to your local profile.

### Minecraft 1.19 and below

1. Press `ESC`, choose `MODS`, then select **Sounds Be Gone**.
2. Click the settings cog in the top-right corner to open the configuration screen.
3. Use the list of recent sounds to disable whatever is bothering you.

## Installation

### CurseForge

> Using [mmm](https://github.com/meza/minecraft-mod-manager)? Run `mmm add curseforge 874633` to install the latest build.

- [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config)
- [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) (optional, but makes the settings screen easier to reach)

### Modrinth

> Using [mmm](https://github.com/meza/minecraft-mod-manager)? Run `mmm add modrinth FOIvwGKz` to install the latest build.

- [Cloth Config](https://modrinth.com/mod/cloth-config)
- [Mod Menu](https://modrinth.com/mod/modmenu) (optional)

## Configuration tips

- Rebind the `B` key if it clashes with another mod under `Options → Controls → Key Binds → Sounds Be Gone`.
- Sounds stay muted until you manually re-enable them, even if you swap worlds or restart the client.
- Mod Menu makes it easy to open the config without jumping into a world, especially on older Minecraft versions.
- Keep Cloth Config up to date—new releases occasionally add UX improvements for navigating large sound lists.

## Telemetry & privacy

Sounds Be Gone collects **anonymous telemetry** to understand which Minecraft versions and mod loaders are still in active use. This helps prioritize support, testing, and build tooling. The data never includes personal information or gameplay analytics.

- You can opt out at any time from the in-game settings screen (`Enable telemetry`).
- Collection happens client-side and only runs when the toggle is enabled.
- Implementation is open-source in [meza_core](https://github.com/meza/meza_core/blob/main/src/main/java/gg/meza/telemetry/Telemetry.java), so you can review the exact payload.

## Community & support

- Questions or feedback? Join the [Discord server](https://discord.gg/dvg3tcQCPW).
- Found a bug or have UX suggestions? Open an issue or PR—see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## Credits

Thanks to all the supporters who make this project possible!

<!-- marker:patrons-start -->

Schauweg

<!-- marker:patrons-end -->
