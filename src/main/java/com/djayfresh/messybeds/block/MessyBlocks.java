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

    public static final MessyBedBlock WHITE_BED = MessyBlocks.buildMessyBed(DyeColor.WHITE);
    public static final MessyBedBlock RED_BED = MessyBlocks.buildMessyBed(DyeColor.RED);
    public static final MessyBedBlock ORANGE_BED = MessyBlocks.buildMessyBed(DyeColor.ORANGE);

    public static void registerAll(IForgeRegistry<Block> registry) {
        registry.register(setup(MessyBlocks.WHITE_BED, "white_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.RED_BED, "red_bed", MessyItems.TAB_MESSY_ITEMS));
        registry.register(setup(MessyBlocks.ORANGE_BED, "orange_bed", MessyItems.TAB_MESSY_ITEMS));
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
        return MessyItems.ITEMS.register(registryName.getPath(), () -> new BlockItem(block, new Item.Properties().tab(tab)));
    }
}
