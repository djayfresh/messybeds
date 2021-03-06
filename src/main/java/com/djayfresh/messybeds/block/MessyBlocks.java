package com.djayfresh.messybeds.block;

import com.djayfresh.messybeds.MessyBeds;
import com.djayfresh.messybeds.item.MessyItems;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public class MessyBlocks {

    public static final MessyBedBlock BLACK_BED = MessyBlocks.buildMessyBed(DyeColor.BLACK);
    public static final MessyBedBlock BLUE_BED = MessyBlocks.buildMessyBed(DyeColor.BLUE);
    public static final MessyBedBlock BROWN_BED = MessyBlocks.buildMessyBed(DyeColor.BROWN);
    public static final MessyBedBlock CYAN_BED = MessyBlocks.buildMessyBed(DyeColor.CYAN);
    public static final MessyBedBlock GRAY_BED = MessyBlocks.buildMessyBed(DyeColor.GRAY);
    public static final MessyBedBlock GREEN_BED = MessyBlocks.buildMessyBed(DyeColor.GREEN);
    public static final MessyBedBlock LIGHT_BLUE_BED = MessyBlocks.buildMessyBed(DyeColor.LIGHT_BLUE);
    public static final MessyBedBlock LIGHT_GRAY_BED = MessyBlocks.buildMessyBed(DyeColor.LIGHT_GRAY);
    public static final MessyBedBlock LIME_BED = MessyBlocks.buildMessyBed(DyeColor.LIME);
    public static final MessyBedBlock MAGENTA_BED = MessyBlocks.buildMessyBed(DyeColor.MAGENTA);
    public static final MessyBedBlock ORANGE_BED = MessyBlocks.buildMessyBed(DyeColor.ORANGE);
    public static final MessyBedBlock PINK_BED = MessyBlocks.buildMessyBed(DyeColor.PINK);
    public static final MessyBedBlock PURPLE_BED = MessyBlocks.buildMessyBed(DyeColor.PURPLE);
    public static final MessyBedBlock RED_BED = MessyBlocks.buildMessyBed(DyeColor.RED);
    public static final MessyBedBlock WHITE_BED = MessyBlocks.buildMessyBed(DyeColor.WHITE);
    public static final MessyBedBlock YELLOW_BED = MessyBlocks.buildMessyBed(DyeColor.YELLOW);

    public static void registerAll(IForgeRegistry<Block> registry) {
        registry.register(setup(MessyBlocks.BLACK_BED, "black_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.BLUE_BED, "blue_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.BROWN_BED, "brown_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.CYAN_BED, "cyan_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.GRAY_BED, "gray_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.GREEN_BED, "green_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.LIGHT_BLUE_BED, "light_blue_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.LIGHT_GRAY_BED, "light_gray_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.LIME_BED, "lime_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.MAGENTA_BED, "magenta_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.ORANGE_BED, "orange_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.PINK_BED, "pink_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.PURPLE_BED, "purple_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.RED_BED, "red_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.WHITE_BED, "white_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.YELLOW_BED, "yellow_bed", MessyItems.TAB_MESSY_ITEMS));
    }

    public static MessyBedBlock getByDyeColor(DyeColor color){
        switch(color){
            case BLACK:
                return MessyBlocks.BLACK_BED;
            case BLUE:
                return MessyBlocks.BLUE_BED;
            case BROWN:
                return MessyBlocks.BROWN_BED;
            case CYAN:
                return MessyBlocks.CYAN_BED;
            case GRAY:
                return MessyBlocks.GRAY_BED;
            case GREEN:
                return MessyBlocks.GREEN_BED;
            case LIGHT_BLUE:
                return MessyBlocks.LIGHT_BLUE_BED;
            case LIGHT_GRAY:
                return MessyBlocks.LIGHT_GRAY_BED;
            case LIME:
                return MessyBlocks.LIME_BED;
            case MAGENTA:
                return MessyBlocks.MAGENTA_BED;
            case ORANGE:
                return MessyBlocks.ORANGE_BED;
            case PINK:
                return MessyBlocks.PINK_BED;
            case PURPLE:
                return MessyBlocks.PURPLE_BED;
            case RED:
                return MessyBlocks.RED_BED;
            case WHITE:
                return MessyBlocks.WHITE_BED;
            case YELLOW:
                return MessyBlocks.YELLOW_BED;
        }

        return MessyBlocks.WHITE_BED;
    }

    private static MessyBedBlock buildMessyBed(DyeColor color) {
        return new MessyBedBlock(color, BlockBehaviour.Properties.of(Material.WOOL, (blockState) -> {
            return blockState.getValue(BedBlock.PART) == BedPart.FOOT ? color.getMaterialColor()
                    : MaterialColor.WOOL;
        }).sound(SoundType.WOOD).strength(0.2F).noOcclusion());
    }

    private static <T extends Block> T setup(final T entry, final String name, final CreativeModeTab tab) {
        return setup(entry, new ResourceLocation(MessyBeds.MOD_ID, name), tab);
    }

    private static <T extends Block> T setup(final T entry, final ResourceLocation registryName, final CreativeModeTab tab) {
        entry.setRegistryName(registryName);
        registerBlockItem(registryName, entry, tab);
        return entry;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(final ResourceLocation registryName, T block, CreativeModeTab tab) {
        return MessyItems.ITEMS.register(registryName.getPath(), () -> new BlockItem(block, new Item.Properties().stacksTo(1).tab(tab)));
    }
}
