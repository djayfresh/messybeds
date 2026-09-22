package com.djayfresh.messybeds.registry;

import com.djayfresh.messybeds.MessyBeds;
import com.djayfresh.messybeds.block.MessyBedBlock;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MessyBeds.MOD_ID);

    /** One messy bed per dye colour, registered as {@code messybeds:<colour>_bed}. */
    public static final Map<DyeColor, DeferredBlock<MessyBedBlock>> BEDS;

    static {
        Map<DyeColor, DeferredBlock<MessyBedBlock>> beds = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values()) {
            beds.put(color, BLOCKS.registerBlock(color.getName() + "_bed", p -> new MessyBedBlock(color, p), () -> bedProperties(color)));
        }
        BEDS = Collections.unmodifiableMap(beds);
    }

    public static DeferredBlock<MessyBedBlock> byColor(DyeColor color) {
        return BEDS.get(color);
    }

    /** Same properties vanilla gives its beds (see {@code Blocks.BED}). */
    private static BlockBehaviour.Properties bedProperties(DyeColor color) {
        return BlockBehaviour.Properties.of()
                .mapColor(state -> state.getValue(BedBlock.PART) == BedPart.FOOT ? color.getMapColor() : MapColor.WOOL)
                .sound(SoundType.WOOD)
                .strength(0.2F)
                .bounceRestitution(0.75F)
                .fallDistanceReduction(0.5F)
                .noOcclusion()
                .ignitedByLava()
                .pushReaction(PushReaction.POPPED);
    }

    private ModBlocks() {}
}
