package com.djayfresh.messybeds.client.renderer;

import java.util.Arrays;
import java.util.Comparator;

import com.djayfresh.messybeds.MessyBeds;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class MessySheets {

    public static final Material[] CLEAN_BED_TEXTURES = Arrays.stream(DyeColor.values())
            .sorted(Comparator.comparingInt(DyeColor::getId)).map((dyeColor) -> {
                return new Material(Sheets.BED_SHEET, new ResourceLocation(MessyBeds.MOD_ID, "entity/clean_bed/" + dyeColor.getName()));
            }).toArray((index) -> {
                return new Material[index];
            });

    public static final Material[] MESSY_BED_TEXTURES = Arrays.stream(DyeColor.values())
            .sorted(Comparator.comparingInt(DyeColor::getId)).map((dyeColor) -> {
                return new Material(Sheets.BED_SHEET, new ResourceLocation(MessyBeds.MOD_ID, "entity/messy_bed/" + dyeColor.getName()));
            }).toArray((index) -> {
                return new Material[index];
            });
}
