# Messy Beds

A NeoForge mod for Minecraft 26.3 that makes beds behave a little more like real beds.

- **Sleeping makes a mess.** When you get out of a bed, both halves switch to the messy look and stay that way.
- **Make your bed.** Right-click a messy bed to tidy it. It cannot be slept in until it is made.
- **Turned down at bedtime.** While the game would let you sleep (dark outside, or a thunderstorm), a clean bed shows the blanket folded back. This follows vanilla's bed rules, so it also respects dimension and datapack overrides.
- **Every bed is a messy bed.** Crafting or dyeing a bed gives a messy bed, breaking a vanilla bed drops a messy bed, loot that would contain a vanilla bed contains a messy bed, and beds placed by world generation (villages, igloos, camps, modded structures) are replaced with messy beds. Worlds generated before the mod was installed are converted as their chunks load.
- Villagers claim messy beds as homes like any other bed. Straw beds are left alone.

## Config (`config/messybeds-common.toml`)

| Key | Default | Meaning |
| --- | --- | --- |
| `turnDownEnabled` | `true` | Show the turned-down look while sleeping is allowed |
| `turnDownCheckInterval` | `100` | Ticks between turn-down checks per bed |
| `replaceWorldGenBeds` | `true` | Replace vanilla beds in newly generated chunks |
| `convertExistingChunks` | `true` | Replace vanilla beds in pre-existing chunks the first time they load |
| `replacePlacedBeds` | `true` | Replace a vanilla bed with a messy bed when it is placed |

## Building

Requires JDK 25 (Gradle resolves it through the toolchain plugin).

```
./gradlew build          # jar in build/libs
./gradlew runClient      # dev client
./gradlew runServer      # dev server; run/server.properties enables RCON (see tools/rcon.py)
python tools/gen_resources.py   # regenerate blockstates, models, textures, loot tables, recipes, tags, lang
```

The messy and turned-down textures are sliced from the original 1.18.2 bed entity sheets kept under `legacy/forge-1.18.2/`. Clean beds use vanilla's own models and textures.

## History

- `v1.0-mc1.18.2`: original Forge 1.18.2 version (block-entity renderer, no world-gen replacement). Source under `legacy/forge-1.18.2/`.
- `2.0.0`: rewrite for NeoForge 26.3 with model-based rendering and world-gen bed replacement.
