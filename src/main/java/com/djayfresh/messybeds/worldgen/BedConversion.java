package com.djayfresh.messybeds.worldgen;

import com.djayfresh.messybeds.block.MessyBedBlock;
import com.djayfresh.messybeds.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

/** Replaces vanilla beds with the messy bed of the same colour, keeping facing, part and occupancy. */
public final class BedConversion {
    /** Client update only, and no shape updates, so the two halves never see each other half-converted. */
    private static final int FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;

    /** A vanilla (non-messy) bed. Straw beds are not {@link BedBlock}s and are never touched. */
    public static boolean isVanillaBed(BlockState state) {
        return state.getBlock() instanceof BedBlock && !(state.getBlock() instanceof MessyBedBlock);
    }

    /** The messy bed state equivalent to a vanilla bed state. */
    public static BlockState toMessy(BlockState vanilla) {
        BedBlock bed = (BedBlock) vanilla.getBlock();
        BlockState messy = ModBlocks.byColor(bed.getColor()).get().defaultBlockState();
        for (var property : vanilla.getProperties()) {
            messy = copyProperty(vanilla, messy, property);
        }
        return messy;
    }

    /**
     * Scans a whole chunk and converts every vanilla bed in it. Cheap for chunks without beds: each section's
     * block palette is checked first and the 4096 positions are only walked when the palette holds a vanilla bed.
     *
     * @return the number of bed halves converted
     */
    public static int convertChunk(LevelChunk chunk) {
        int converted = 0;
        LevelChunkSection[] sections = chunk.getSections();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        for (int index = 0; index < sections.length; index++) {
            LevelChunkSection section = sections[index];
            if (section.hasOnlyAir() || !section.maybeHas(BedConversion::isVanillaBed)) {
                continue;
            }
            int minY = chunk.getSectionYFromSectionIndex(index) << 4;
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        BlockState state = section.getBlockState(x, y, z);
                        if (isVanillaBed(state)) {
                            pos.set(minX + x, minY + y, minZ + z);
                            chunk.setBlockState(pos, toMessy(state), FLAGS);
                            converted++;
                        }
                    }
                }
            }
        }
        return converted;
    }

    /** Converts one position if it holds a vanilla bed. */
    public static boolean convertAt(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!isVanillaBed(state)) {
            return false;
        }
        level.setBlock(pos, toMessy(state), FLAGS);
        return true;
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, net.minecraft.world.level.block.state.properties.Property<T> property) {
        return to.hasProperty(property) ? to.setValue(property, from.getValue(property)) : to;
    }

    private BedConversion() {}
}
