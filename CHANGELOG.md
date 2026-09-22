# Changelog

## 2.0.1 (2026-09-22)

- Fix the foot end of messy and turned-down beds rendering upside down (rail above the blanket).

## 2.0.0 (2026-09-22) - Minecraft 26.3, NeoForge

- Rewritten for NeoForge 26.3. Beds are rendered with JSON block models; the block entity and renderer are gone.
- Messy state is set when any sleeper leaves the bed and cleared by right-clicking it.
- Turned-down look follows vanilla's bed rule (dark outside or thunderstorm) instead of hard-coded times.
- Every bed in the world is a messy bed: crafting, dyeing, vanilla bed drops, generated loot, and structure beds in new and pre-existing chunks. Placed vanilla beds are swapped too.
- Villagers claim messy beds as homes.
- Config: `turnDownEnabled`, `turnDownCheckInterval`, `replaceWorldGenBeds`, `convertExistingChunks`, `replacePlacedBeds`.
- Straw beds are untouched.

## 1.0 (2022-04) - Minecraft 1.18.2, Forge

- Original version, tagged `v1.0-mc1.18.2`. Never published.
