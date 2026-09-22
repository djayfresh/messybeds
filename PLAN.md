# Messy Beds modernization plan

## Findings (2026-09-22)

### Current state of the repo
- Forge 1.18.2 (forge 40.0.42, ForgeGradle 5, Java 17, official mappings). 23 commits, last one 2022-04-26. Never tagged, never released (`version = '1.0'`).
- 16 `MessyBedBlock`s (one per `DyeColor`) extend vanilla `BedBlock` and add a `MESSY` boolean state. Registered the old way (`RegistryEvent.Register<Block>` + `setRegistryName`), items through a `DeferredRegister`.
- `MessyBedEntity` + `MessyBedRenderer`: a copy of vanilla's bed block-entity renderer that picks one of three 64x64 entity texture sheets per color: `clean_bed`, `messy_bed`, `turn_down_bed` (48 PNGs under `textures/entity/`). Turn-down is chosen client side from `level.dayTime()` with hard-coded tick numbers (12542..23459, widened by 532 when raining) or when thundering.
- Behaviour: right-click a bed -> sleep; on a successful `startSleepInBed` both halves get `MESSY=true`. Right-click a messy bed -> `MESSY=false` and a "cleaned" overlay message. `PlayerWakeUpEvent` shows a "messy" overlay message.
- Data: every vanilla bed recipe (`data/minecraft/recipes/*_bed*.json`) is overridden to output the messy bed; every vanilla bed loot table (`data/minecraft/loot_tables/blocks/*_bed.json`, 15 files, `black_bed` is missing) drops the messy bed; messy beds have their own loot tables (15, `black_bed` missing again); messy beds are appended to `#minecraft:beds`.
- World gen: **nothing**. The only attempt is a Forge global loot modifier (`messybeds_container_loot`) that *adds* a messy bed next to any vanilla bed found in chest loot instead of replacing it, and it is gated on a `block_state_property` condition for `minecraft:chest` that never matches a chest loot context. Village, igloo and other structure beds are untouched.
- Leftovers: `HELLO` debug logging everywhere, commented-out code, `examplemod` run configs, `mcmodsrepo` publishing, README is one line.

### What changed in Minecraft 26.3 (verified against the decompiled 26.3 sources in `C:\dev\FreshCookieMod\build\moddev\artifacts`)
- **Beds are no longer block entities.** There is no `BedBlockEntity`, no `BedRenderer`, no `Sheets.BED_TEXTURES`. Beds are plain JSON block models: `blockstates/<color>_bed.json` maps `facing` x `part` to `block/<color>_bed_head` / `_foot`, each built on `template_bed_head` / `template_bed_foot` with per-face textures (`<color>_bed_head_up/east/west`, `<color>_bed_foot_up/east/west/south`, shared `bed_down`, `bed_head_north`). Items use `items/<color>_bed.json` with a `composite` of the head and foot models.
- New hierarchy `AbstractBedBlock` -> `BedBlock` (dyed, has `getColor()`) and `StrawBedBlock` (new in 26.3: one-use camping bed, no spawn point). `AbstractBedBlock` owns `FACING`, `PART`, `OCCUPIED`, all sleeping logic (`useWithoutItem`), villager kicking, and two hooks we can override: `destroyOnUse`, `destroyOnLeave`, plus `onStopSleeping(level, pos)`, called when the sleeper leaves the bed.
- Sleep rules moved to environment attributes: `BedRule(canSleep, canSetSpawn, destroyOnUse, destroyOnLeave, errorMessage)` fetched through `level.environmentAttributes().getValue(EnvironmentAttributes.BED_RULE, pos)`. `BedRule.canSleep(level)` is the exact "can a player sleep now" test (`WHEN_DARK` -> `level.isDarkOutside()`), so the hard-coded day-time numbers go away.
- Vanilla beds are registered as a `ColorCollection<Block>` (`Blocks.BED`), and `PoiTypes.HOME` is built in code from `Blocks.BED` head states. **Villagers only recognise blocks in that POI set**, so replaced village beds must be added to it or villagers stop sleeping and breeding. NeoForge provides `ExtendPoiTypesEvent` / `PoiTypeExtender` for exactly this.
- New tags: `#minecraft:villagers_can_sleep_on_bed` and `#minecraft:villager_babies_can_jump_on_bed` (both default to `#minecraft:beds`, so staying in `#minecraft:beds` is enough).
- Data pack paths are singular now: `loot_table/`, `recipe/`, `tags/block/`, `tags/item/`; loot conditions use `match_block` with `blocks`/`state` and tables carry `random_sequence`.
- NeoForge 26.3 is still beta only (26.3.0.10-beta is the latest on the project listing; FreshCookieMod is on 26.3.0.8-beta). Same toolchain as FreshCookieMod: NeoForge ModDev Gradle plugin 2.0.147, Gradle 9.2.1, Java 25.
- Mod ids are still `messybeds`; `Identifier` replaces `ResourceLocation`; `Component.translatable` replaces `TranslatableComponent`; `player.sendOverlayMessage` replaces `displayClientMessage(..., true)`.

## Phase 0: Tag history and branch  — DONE 2026-09-22
1. Tagged `master` as `v1.0-mc1.18.2` (the only complete 1.18.2 state; never released but works in game per the commit log).
2. Branched `neoforge-26.3` and moved the 1.18.2 tree under `legacy/forge-1.18.2/` so the old renderer and textures stay readable next to the new code.
3. Tag and branch are local only until pushed.

## Constraint: coexist with Fresh Cookies
Messy Beds and Fresh Cookies (`freshcaa`) must load side by side in the same instance. Keep the mod id `messybeds` and package `com.djayfresh.messybeds`, never share registry names or mixins, and override only vanilla files Fresh Cookies does not touch (bed recipes, bed loot tables, `#minecraft:beds`). Both mods pin the same NeoForge line, so bump `neo_version` in both when the 26.3 release lands. Every verification run in Phases 1-3 uses the existing MultiMC instance from the Fresh Cookies port with both jars installed.

## Phase 1: Port to NeoForge 26.3 (rewrite, guided by the old code)
Reuse the FreshCookieMod skeleton: `build.gradle`, `gradle.properties`, `settings.gradle`, wrapper, `src/main/templates/META-INF/neoforge.mods.toml`, `.gitignore`. `mod_id=messybeds`, `mod_group_id=com.djayfresh.messybeds`, `mod_version=2.0.0`.

### Blocks and items
- `MessyBedBlock extends BedBlock` with `MESSY` and `TURNED_DOWN` boolean properties. Register the 16 colors with a `DeferredRegister.Blocks` + `DeferredRegister.Items` loop over `DyeColor.values()` (`registerBlock("<color>_bed", ...)` and `registerSimpleBlockItem`), kept in a `Map<DyeColor, DeferredBlock<MessyBedBlock>>` so `byColor(DyeColor)` replaces the 16-case switch.
- No block entity, no renderer. Delete `MessyBedEntity`, `MessyEntities`, `MessyBedRenderer`, `MessySheets`, the texture-stitch handler.
- Creative tab through `DeferredRegister<CreativeModeTab>` (same as FreshCookieMod), icon = white messy bed. Also add the beds to the vanilla Functional tab with `BuildCreativeModeTabContentsEvent`.
- Behaviour:
  - `useWithoutItem`: if `MESSY` -> set both halves clean, overlay `block.messybeds.bed.cleaned`, return `SUCCESS_SERVER`; otherwise `super`.
  - `onStopSleeping(level, pos)`: `super`, then set both halves `MESSY=true`. This replaces the 1.18 "set messy on successful startSleepInBed" and covers being woken by monsters and by dawn.
  - `PlayerWakeUpEvent` handler keeps the "your bed is messy" overlay message.
  - A `setBothHalves(level, pos, property, value)` helper writing with `UPDATE_CLIENTS | UPDATE_KNOWN_SHAPE` so `updateShape` never sees a half-updated pair (the 1.18 `updateShape` override mutated a state without using the result and can be dropped).
- Turn-down (bed looks ready when a player could sleep): `TURNED_DOWN` is a server-side block state driven by scheduled ticks. `onPlace` schedules a tick; `tick()` sets `TURNED_DOWN = getBedRule(level, pos).canSleep(level) && !MESSY` and reschedules itself every 100 ticks. The world-gen conversion (Phase 2) schedules the first tick too. This keeps the look correct with shaders and in item frames and needs no renderer. Only the head half needs to tick; it pushes the value to the foot.
- Villagers: `ExtendPoiTypesEvent` adds every messy bed head state with `OCCUPIED=false` to `PoiTypes.HOME` (verify the exact extender API when implementing; it is in `net.neoforged.neoforge.common.world.poi`). Keep messy beds in `#minecraft:beds`, which also satisfies the two new villager tags.
- Lang: keep `block.messybeds.<color>_bed`, the two overlay messages, tab title. Fix the `block.messybed.*` keys to `block.messybeds.*`.

### Assets (models replace the renderer)
- Blockstates: `facing` x `part` x `messy` x `turned_down`. Clean/not-turned-down variants point at the vanilla `minecraft:block/<color>_bed_head` / `_foot` models, so clean beds look exactly like vanilla and cost no textures. Messy and turned-down variants use `messybeds:block/<color>_bed_<state>_head` / `_foot` models that extend vanilla `template_bed_head` / `_foot` and override only the `up` faces (and the foot `south` face where the blanket hangs) with mod textures; side faces reuse the vanilla textures.
- Item models: `items/<color>_bed.json` composite of the vanilla head/foot models (copy vanilla's JSON, or reference the messy variant if the icon should look messy).
- Texture conversion script `tools/textures/slice_bed_sheets.py` (PIL): the old 64x64 entity sheets hold the head mattress at (6,6)-(22,22) and the foot mattress at (6,28)-(22,44); slice those into `<color>_bed_messy_head_up.png`, `<color>_bed_messy_foot_up.png`, `<color>_bed_turned_down_head_up.png`, `<color>_bed_turned_down_foot_up.png` (64 PNGs). The foot `south` face (blanket edge, 16x6) comes from the foot box's front strip. `clean_bed` sheets are not needed (vanilla). Run once, commit the output, keep the sheets under `legacy/`.
- Datagen (`data` run) for blockstates, models, loot tables, recipes and tags so the 16x4 variant explosion is generated, not hand-written.

### Data
- Recipes: override the 16 vanilla bed recipes and 15 dye recipes to output messy beds (as today, new `recipe/` path and format). Also dyeing a messy bed with dye -> messy bed of that color.
- Loot: override the 16 vanilla bed loot tables to drop the messy bed (fixes the missing `black_bed`), and 16 messy bed loot tables (drop from the head half, `survives_explosion`).
- Tags: `#minecraft:beds` += messy beds. Item tag `#minecraft:beds` likewise.
- Global loot modifier rewritten with a codec (`data/neoforge/loot_modifiers/global_loot_modifiers.json` + `data/messybeds/loot_modifiers/replace_beds.json`): replace, not add, any `BedItem` stack whose block is a vanilla `BedBlock` with the same-color messy bed, no conditions (applies to chests, minecarts, barrels, fishing, everything). Priority default.
- Config (`ModConfigSpec`, common): `replaceWorldGenBeds` (true), `convertExistingChunks` (true), `turnDownEnabled` (true), `turnDownCheckInterval` (100 ticks).

### Cleanup
- Remove all `HELLO` logging and commented code; README describing the mod (messy after sleeping, clean on click, turned down when you can sleep, all world beds are messy beds, config keys, straw beds are untouched).

## Phase 2: World gen: every bed in every world is a messy bed
Requirement: any world with the mod installed has its vanilla beds replaced, including villages generated before the mod was added.

### Design
- **Chunk conversion pass on `ChunkEvent.Load`** (NeoForge, server side). This is the primary mechanism because it covers every source at once: vanilla village/igloo/camp templates, modded structures, and chunks that already exist on disk.
  - A `boolean` chunk data attachment `messybeds:beds_converted` (NeoForge `AttachmentType`, `.serialize(Codec.BOOL)`) marks chunks that have been processed. New chunks and old chunks both start unmarked.
  - On load, if the chunk is unmarked and (`isNewChunk()` or `convertExistingChunks`), queue the work on the server thread (`level.getServer().execute`) so it runs after the chunk is fully in the level and not inside the load callback.
  - The scan is cheap: for each `LevelChunkSection`, `section.maybeHas(state -> state.getBlock() instanceof BedBlock && !(state.getBlock() instanceof MessyBedBlock))` checks only the palette. Only sections that report a possible match iterate their 4096 positions.
  - Each matching state is rewritten to `MessyBlocks.byColor(bed.getColor())` with `FACING`, `PART`, `OCCUPIED` copied, `MESSY=false`, and written with `UPDATE_CLIENTS | UPDATE_KNOWN_SHAPE` so the two halves never see each other as invalid mid-swap. Head halves get their first turn-down tick scheduled. Since 26.3 beds have no block entity, nothing else needs migrating.
  - After the pass, set the attachment and `chunk.markUnsaved()`. Villager POI entries are refreshed because `setBlock` updates the POI manager when the state's POI type changes (verify: `PoiManager` listens to `LevelChunk.setBlockState`; if the old vanilla head is removed and the new messy head is in the extended HOME set, the villager keeps its home).
  - `StrawBedBlock` is not a `BedBlock` and is deliberately left alone.
- **Structure processor** (secondary, optional): a `StructureProcessorType` `messybeds:replace_beds` that maps vanilla bed states in `StructureTemplate.StructureBlockInfo` to messy ones. Registering it is easy; injecting it into every vanilla template pool is not (village pools reference fixed processor lists such as `minecraft:mossify_10_percent`, and overriding those JSONs conflicts with other mods). Skip unless the chunk pass turns out to be visible to players (it is not: the conversion runs before the chunk is sent).
- **Player-placed vanilla beds** (creative menu, `/give`): optional `BlockEvent.EntityPlaceEvent` handler that swaps a just-placed vanilla bed for the messy one. Cheap and keeps the invariant "no vanilla beds exist"; put it behind config `replacePlacedBeds` (default true).

### Steps
1. Attachment type + config keys.
2. `BedConversion.convertChunk(ServerLevel, LevelChunk)` with the palette pre-check; unit-style gametest that places a vanilla bed pair in a chunk, runs the pass, and asserts state, facing, part, and that the attachment is set.
3. `ChunkEvent.Load` listener with the server-thread hand-off and the attachment guard.
4. POI extension and a gametest that a villager can claim a converted bed (`/data get` on the villager brain `home` memory over RCON is enough as a manual check).
5. Placement swap handler.
6. Manual verification on the dedicated server (RCON, same harness as FreshCookieMod): new world -> locate village -> `setblock`/`execute if block` checks show only `messybeds:*_bed`; stop the server, drop the mod, generate a village, re-add the mod, load: village beds are converted; villagers sleep at night; sleeping in a village bed leaves it messy; right-click cleans it; chest loot never contains `minecraft:*_bed`.

## Phase 3: Release
- README, CHANGELOG, `mod_version` 2.0.0, logo (`logoFile`), merge `neoforge-26.3` -> `master`, tag `v2.0.0-mc26.3`.
- Bump `neo_version` when the 26.3 release build lands (same note as FreshCookieMod).

## Open questions (decide during Phase 1, defaults in brackets)
- Should the clean bed look be vanilla or the old `clean_bed` sheet? [vanilla; the old clean textures were a copy of vanilla]
- Should the messy look also show when the bed is turned down? [messy wins, as in 1.18]
- Straw beds: leave them vanilla? [yes, one-use beds cannot be messy]
- Convert chunks that already exist on disk by default? [yes; that is the stated requirement]
