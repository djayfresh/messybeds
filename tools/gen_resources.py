"""Generates every data-driven resource for Messy Beds.

Run from the repo root:  python tools/gen_resources.py

Output goes straight into src/main/resources and is committed (no datagen run needed).
- Blockstates: facing x part x messy x turned_down. Clean, not-turned-down beds use the vanilla
  models, so they look exactly like vanilla and cost no textures.
- Block models for the messy and turned-down looks extend vanilla's template_bed_head/_foot and
  override only the mattress ("up") faces and the foot's blanket-end ("south") face.
- Textures for those faces are sliced from the old 64x64 bed entity sheets under legacy/.
- Item models, lang, loot tables, recipes, tags, and the global loot modifier JSON.
"""
from __future__ import annotations

import json
import shutil
from pathlib import Path

from PIL import Image, ImageOps

# Decompiled 26.3 client, used only to read vanilla's bed textures for the leg rows.
VANILLA_JAR = Path(r"C:/dev/FreshCookieMod/build/moddev/artifacts/minecraft-patched-26.3.0.8-beta-sources.jar")


def vanilla_foot_south(color: str) -> Image.Image:
    import io
    import zipfile
    with zipfile.ZipFile(VANILLA_JAR) as jar:
        data = jar.read(f"assets/minecraft/textures/block/{color}_bed_foot_south.png")
    return Image.open(io.BytesIO(data)).convert("RGBA")

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "src" / "main" / "resources"
ASSETS = RES / "assets" / "messybeds"
DATA = RES / "data"
LEGACY_TEX = ROOT / "legacy" / "forge-1.18.2" / "src" / "main" / "resources" / "assets" / "messybeds" / "textures" / "entity"

MOD = "messybeds"
# Vanilla creative / dye order.
COLORS = ["white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
          "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"]
LOOKS = {"messy": "messy_bed", "turned_down": "turn_down_bed"}  # state name -> legacy sheet folder
FACINGS = {"north": None, "east": 90, "south": 180, "west": 270}

# Regions of the old 64x64 bed entity sheet (box UV layout of BedRenderer's head/foot cubes).
HEAD_UP = (6, 6, 22, 22)        # head cube front face = mattress top, 16x16
FOOT_UP = (6, 28, 22, 44)       # foot cube front face = mattress top, 16x16
FOOT_SOUTH = (22, 22, 38, 28)   # foot cube bottom face = blanket end, 16x6


def write_json(path: Path, obj) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(obj, indent=2) + "\n", encoding="utf-8")


def reset(path: Path) -> None:
    if path.exists():
        shutil.rmtree(path)
    path.mkdir(parents=True, exist_ok=True)


def gen_textures() -> None:
    out = ASSETS / "textures" / "block"
    reset(out)
    for color in COLORS:
        for look, folder in LOOKS.items():
            sheet = Image.open(LEGACY_TEX / folder / f"{color}.png").convert("RGBA")
            sheet.crop(HEAD_UP).save(out / f"{color}_bed_{look}_head_up.png")
            sheet.crop(FOOT_UP).save(out / f"{color}_bed_{look}_foot_up.png")
            # Foot south texture: vanilla's is a 16x16 sheet whose rows 7..13 hold the blanket end
            # (sampled by the main box) and rows 13..16 hold the small leg faces. Take the leg rows
            # from vanilla's own texture and drop the sliced blanket strip into rows 7..13.
            south = vanilla_foot_south(color).copy()
            # The box-UV bottom face is stored upside down; vanilla wants the blanket on top and the rail below.
            south.paste(ImageOps.flip(sheet.crop(FOOT_SOUTH)), (0, 7))
            south.save(out / f"{color}_bed_{look}_foot_south.png")


def gen_models_and_blockstates() -> None:
    models = ASSETS / "models" / "block"
    items = ASSETS / "items"
    states = ASSETS / "blockstates"
    for d in (models, items, states):
        reset(d)
    for color in COLORS:
        for look in LOOKS:
            write_json(models / f"{color}_bed_{look}_head.json", {
                "parent": "minecraft:block/template_bed_head",
                "textures": {
                    "east": f"minecraft:block/{color}_bed_head_east",
                    "up": f"{MOD}:block/{color}_bed_{look}_head_up",
                    "west": f"minecraft:block/{color}_bed_head_west",
                },
            })
            write_json(models / f"{color}_bed_{look}_foot.json", {
                "parent": "minecraft:block/template_bed_foot",
                "textures": {
                    "east": f"minecraft:block/{color}_bed_foot_east",
                    "south": f"{MOD}:block/{color}_bed_{look}_foot_south",
                    "up": f"{MOD}:block/{color}_bed_{look}_foot_up",
                    "west": f"minecraft:block/{color}_bed_foot_west",
                },
            })

        variants = {}
        for facing, rot in FACINGS.items():
            for part in ("head", "foot"):
                for messy in ("false", "true"):
                    for turned_down in ("false", "true"):
                        if messy == "true":
                            model = f"{MOD}:block/{color}_bed_messy_{part}"
                        elif turned_down == "true":
                            model = f"{MOD}:block/{color}_bed_turned_down_{part}"
                        else:
                            model = f"minecraft:block/{color}_bed_{part}"
                        variant = {"model": model}
                        if rot:
                            variant["y"] = rot
                        variants[f"facing={facing},messy={messy},part={part},turned_down={turned_down}"] = variant
        write_json(states / f"{color}_bed.json", {"variants": variants})

        # Item: vanilla's composite of head + foot, using the clean vanilla models.
        write_json(items / f"{color}_bed.json", {
            "model": {
                "type": "minecraft:composite",
                "models": [
                    {"type": "minecraft:model", "model": f"minecraft:block/{color}_bed_head"},
                    {"type": "minecraft:model", "model": f"minecraft:block/{color}_bed_foot",
                     "transformation": {
                         "left_rotation": [0.0, 0.0, 0.0, 1.0],
                         "right_rotation": [0.0, 0.0, 0.0, 1.0],
                         "scale": [1.0, 1.0, 1.0],
                         "translation": [0.0, 0.0, 1.0],
                     }},
                ],
            }
        })


def gen_lang() -> None:
    lang = {
        "itemGroup.messybeds": "Messy Beds",
        "block.messybeds.bed.cleaned": "Thank you for making your bed",
        "block.messybeds.bed.messy": "Looks like you made a mess of your bed",
        "messybeds.configuration.title": "Messy Beds",
        "messybeds.configuration.turnDownEnabled": "Turn beds down at bedtime",
        "messybeds.configuration.turnDownCheckInterval": "Turn-down check interval (ticks)",
        "messybeds.configuration.replaceWorldGenBeds": "Replace beds in new chunks",
        "messybeds.configuration.convertExistingChunks": "Replace beds in existing chunks",
        "messybeds.configuration.replacePlacedBeds": "Replace placed vanilla beds",
    }
    for color in COLORS:
        lang[f"block.{MOD}.{color}_bed"] = color.replace("_", " ").title() + " Bed"
    write_json(ASSETS / "lang" / "en_us.json", dict(sorted(lang.items())))


def loot_table(block_id: str, drop_id: str, sequence: str):
    return {
        "type": "minecraft:block",
        "pools": [{
            "rolls": 1,
            "condition": {"type": "minecraft:survives_explosion"},
            "entries": [{
                "type": "minecraft:item",
                "condition": {"type": "minecraft:match_block", "blocks": block_id, "state": {"part": "head"}},
                "name": drop_id,
            }],
        }],
        "random_sequence": sequence,
    }


def gen_data() -> None:
    reset(DATA)
    mc = DATA / "minecraft"
    mod = DATA / MOD
    for color in COLORS:
        messy = f"{MOD}:{color}_bed"
        vanilla = f"minecraft:{color}_bed"
        # Our beds drop themselves; vanilla beds drop the messy bed (overrides vanilla's table).
        write_json(mod / "loot_table" / "blocks" / f"{color}_bed.json", loot_table(messy, messy, f"{MOD}:blocks/{color}_bed"))
        write_json(mc / "loot_table" / "blocks" / f"{color}_bed.json", loot_table(vanilla, messy, f"minecraft:blocks/{color}_bed"))
        # Crafting a bed gives a messy bed (overrides vanilla's recipe, keeps its advancement).
        write_json(mc / "recipe" / f"{color}_bed.json", {
            "type": "minecraft:crafting_shaped",
            "group": "bed",
            "key": {"#": f"minecraft:{color}_wool", "X": "#minecraft:planks"},
            "pattern": ["###", "XXX"],
            "result": {"id": messy},
        })
        # Dyeing any bed (vanilla or messy) gives a messy bed of the dye colour.
        others = [c for c in COLORS if c != color]
        write_json(mc / "recipe" / f"dye_{color}_bed.json", {
            "type": "minecraft:crafting_shapeless",
            "group": "bed_dye",
            "ingredients": [
                f"minecraft:{color}_dye",
                [f"minecraft:{c}_bed" for c in others] + [f"{MOD}:{c}_bed" for c in others],
            ],
            "result": {"id": messy},
        })

    all_beds = [f"{MOD}:{c}_bed" for c in COLORS]
    write_json(mc / "tags" / "block" / "beds.json", {"replace": False, "values": all_beds})
    write_json(mc / "tags" / "item" / "beds.json", {"replace": False, "values": all_beds})

    # NeoForge loads every data/<ns>/loot_modifiers/*.json directly; there is no index file any more.
    write_json(mod / "loot_modifiers" / "replace_beds.json", {"type": f"{MOD}:replace_beds"})


def main() -> None:
    gen_textures()
    gen_models_and_blockstates()
    gen_lang()
    gen_data()
    print("generated resources under", RES)


if __name__ == "__main__":
    main()
