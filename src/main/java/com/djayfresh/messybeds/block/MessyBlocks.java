package com.djayfresh.messybeds.block;

import com.djayfresh.messybeds.MessyBeds;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class MessyBlocks {

    public static final MessyBedBlock WHITE_BED = MessyBlocks.buildMessyBed(DyeColor.WHITE);
    public static final MessyBedBlock RED_BED = MessyBlocks.buildMessyBed(DyeColor.RED);
    public static final MessyBedBlock ORANGE_BED = MessyBlocks.buildMessyBed(DyeColor.ORANGE);

    public static void registerAll(IForgeRegistry<Block> registry) {
        registry.register(setup(MessyBlocks.WHITE_BED, "white_bed"));
        registry.register(setup(MessyBlocks.RED_BED, "red_bed"));
        registry.register(setup(MessyBlocks.ORANGE_BED, "orange_bed"));
    }

    private static MessyBedBlock buildMessyBed(DyeColor color) {
        return new MessyBedBlock(color, BlockBehaviour.Properties.of(Material.WOOL, (blockState) -> {
            return blockState.getValue(BedBlock.PART) == BedPart.FOOT ? color.getMaterialColor()
                    : MaterialColor.WOOL;
        }).sound(SoundType.WOOD).strength(0.2F).noOcclusion());
    }

    private static <T extends IForgeRegistryEntry<Block>> T setup(final T entry, final String name) {
        return setup(entry, new ResourceLocation(MessyBeds.MOD_ID, name));
    }

    private static <T extends IForgeRegistryEntry<Block>> T setup(final T entry, final ResourceLocation registryName) {
        entry.setRegistryName(registryName);
        return entry;
    }
}
