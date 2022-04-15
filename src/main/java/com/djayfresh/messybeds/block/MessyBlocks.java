package com.djayfresh.messybeds.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.IForgeRegistry;

public class MessyBlocks {

    public static final MessyBed WHITE_BED = MessyBlocks.buildMessyBed("white_bed", DyeColor.WHITE);
    public static final MessyBed RED_BED = MessyBlocks.buildMessyBed("red_bed", DyeColor.RED);
    public static final MessyBed ORANGE_BED = MessyBlocks.buildMessyBed("orange_bed", DyeColor.ORANGE);
    

    public static void registerAll(IForgeRegistry<Block> registry) {
        registry.register(MessyBlocks.WHITE_BED);
        registry.register(MessyBlocks.RED_BED);
        registry.register(MessyBlocks.ORANGE_BED);
    }

    private static MessyBed buildMessyBed(String name, DyeColor color) {
        return new MessyBed(name, color, BlockBehaviour.Properties.of(Material.WOOL, (blockState) -> {
            return blockState.getValue(BedBlock.PART) == BedPart.FOOT ? color.getMaterialColor()
                    : MaterialColor.WOOL;
        }).sound(SoundType.WOOD).strength(0.2F).noOcclusion());
    }
}
