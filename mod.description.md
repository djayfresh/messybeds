# Messy Beds - store description

Source of truth for the text on the Planet Minecraft page (https://www.planetminecraft.com/mod/messy-beds/) and any other listing. Update this file with each release, then copy it over.

## Description

Welcome to your new bed. Kinda looks like your old one, right? Well, you'll get to loving these new beds soon enough. In fact, every bed in the world is one now: villages, igloos, camps, the one you craft, the one you dye, the one you dig out of a chest. Even worlds you started before installing get their beds swapped as you wander back through.

Can't tell when to fall asleep? No more, with our turndown bed service. When the night is dark enough to sleep, the blanket is already folded back for you.

Always wanted to tidy up your sheets? That's right! No more clean beds after a night of sound sleep. Wake up, admire the mess, and give it a right-click when you're ready to be a responsible adult. Villagers get the same treatment, and yes, they still find their way home.

Straw beds are left alone. One night only, no housekeeping.

## Requirements

- Minecraft 26.3 (Java Edition)
- NeoForge 26.3.0.8-beta or newer
- Works alongside Fresh Cookies

## Config

`config/messybeds-common.toml`

- `turnDownEnabled` - show the turned-down look while sleeping is allowed (default true)
- `turnDownCheckInterval` - ticks between turn-down checks per bed (default 100)
- `replaceWorldGenBeds` - replace vanilla beds in newly generated chunks (default true)
- `convertExistingChunks` - replace vanilla beds in pre-existing chunks the first time they load (default true)
- `replacePlacedBeds` - swap a vanilla bed for a messy bed when it is placed (default true)

## Downloads

- GitHub releases: https://github.com/djayfresh/messybeds/releases
- Current: 2.0.1 - https://github.com/djayfresh/messybeds/releases/tag/v2.0.1-mc26.3

## Changelog (page version)

**2.0.1** - Fixed the foot end of messy and turned-down beds rendering upside down.

**2.0.0** - Rewritten for NeoForge 26.3. Beds are plain block models (no block entity). Messy after sleeping, right-click to make. Turned down by vanilla's bed rule. Every bed in the world becomes a messy bed, including pre-existing chunks. Villagers claim messy beds. Straw beds untouched.

**1.0** - Original Forge 1.18.2 version, never published.

## Images

- Cover: `tools/out/cover_planetminecraft.png` (1280x720, generated from the textures; regenerate with the PIL snippet in the session notes or take an in-game screenshot)
- Logo: `src/main/resources/messybeds_logo.png` (128x128)

## Tags

beds, sleep, decoration, villagers, world generation, neoforge, 26.3
