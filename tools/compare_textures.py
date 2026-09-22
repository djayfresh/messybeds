"""Side-by-side contact sheet: vanilla bed faces next to the generated messy / turned-down faces.

Usage:  python tools/compare_textures.py [color ...]     (default: red white)
Output: tools/out/texture_compare_<color>.png, each 16x16 face scaled 16x with a pixel grid and labels.
"""
from __future__ import annotations

import io
import sys
import zipfile
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
MINE = ROOT / "src" / "main" / "resources" / "assets" / "messybeds" / "textures" / "block"
OUT = ROOT / "tools" / "out"
VANILLA_JAR = Path("C:/dev/FreshCookieMod/build/moddev/artifacts/minecraft-patched-26.3.0.8-beta-sources.jar")
SCALE = 16
FACES = ["head_up", "foot_up", "foot_south"]
LOOKS = ["messy", "turned_down"]


def vanilla(name: str) -> Image.Image:
    with zipfile.ZipFile(VANILLA_JAR) as jar:
        return Image.open(io.BytesIO(jar.read(f"assets/minecraft/textures/block/{name}.png"))).convert("RGBA")


def mine(name: str) -> Image.Image:
    return Image.open(MINE / f"{name}.png").convert("RGBA")


def tile(im: Image.Image, label: str) -> Image.Image:
    big = im.resize((16 * SCALE, 16 * SCALE), Image.NEAREST)
    canvas = Image.new("RGBA", (big.width + 2, big.height + 24), (40, 40, 40, 255))
    canvas.paste(big, (1, 22), big)
    draw = ImageDraw.Draw(canvas)
    for i in range(17):
        c = (0, 0, 0, 90) if i % 4 else (255, 255, 0, 140)
        draw.line([(1 + i * SCALE, 22), (1 + i * SCALE, 22 + big.height)], fill=c)
        draw.line([(1, 22 + i * SCALE), (1 + big.width, 22 + i * SCALE)], fill=c)
    draw.text((4, 4), label, fill=(255, 255, 255, 255))
    return canvas


def sheet(color: str) -> Path:
    rows = []
    for face in FACES:
        tiles = [tile(vanilla(f"{color}_bed_{face}"), f"vanilla {face}")]
        for look in LOOKS:
            tiles.append(tile(mine(f"{color}_bed_{look}_{face}"), f"{look} {face}"))
        rows.append(tiles)
    w = sum(t.width + 8 for t in rows[0])
    h = sum(r[0].height + 8 for r in rows)
    out = Image.new("RGBA", (w, h), (20, 20, 20, 255))
    y = 0
    for r in rows:
        x = 0
        for t in r:
            out.paste(t, (x, y))
            x += t.width + 8
        y += r[0].height + 8
    OUT.mkdir(parents=True, exist_ok=True)
    path = OUT / f"texture_compare_{color}.png"
    out.save(path)
    return path


if __name__ == "__main__":
    for color in sys.argv[1:] or ["red", "white"]:
        print(sheet(color))
