package com.djayfresh.messybeds.event;

import com.djayfresh.messybeds.Config;
import com.djayfresh.messybeds.MessyBeds;
import com.djayfresh.messybeds.registry.ModAttachments;
import com.djayfresh.messybeds.worldgen.BedConversion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

/** Game-bus listeners: world-gen bed replacement and placed-bed replacement. */
@EventBusSubscriber(modid = MessyBeds.MOD_ID)
public final class GameEvents {

    /**
     * Runs once per chunk, the first time it is loaded with this mod present. NeoForge fires this on the server
     * thread after the chunk's tick container is registered, so block changes and scheduled ticks are safe here,
     * and before the chunk is sent to any player, so nobody sees the vanilla beds.
     */
    @SubscribeEvent
    static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel)) {
            return;
        }
        LevelChunk chunk = event.getChunk();
        boolean enabled = event.isNewChunk() ? Config.REPLACE_WORLD_GEN_BEDS.get() : Config.CONVERT_EXISTING_CHUNKS.get();
        if (!enabled || chunk.getData(ModAttachments.BEDS_CONVERTED)) {
            return;
        }
        int converted = BedConversion.convertChunk(chunk);
        chunk.setData(ModAttachments.BEDS_CONVERTED, true);
        chunk.markUnsaved();
        if (converted > 0) {
            MessyBeds.LOGGER.debug("Replaced {} vanilla bed halves in chunk {}", converted, chunk.getPos());
        }
    }

    /** A vanilla bed placed by a player or dispenser becomes the messy bed. Fires after both halves are in the world. */
    @SubscribeEvent
    static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!Config.REPLACE_PLACED_BEDS.get() || event.getLevel().isClientSide() || !BedConversion.isVanillaBed(event.getPlacedBlock())) {
            return;
        }
        if (event instanceof BlockEvent.EntityMultiPlaceEvent multi) {
            for (BlockSnapshot snapshot : multi.getReplacedBlockSnapshots()) {
                BedConversion.convertAt(event.getLevel(), snapshot.getPos());
            }
        } else {
            BedConversion.convertAt(event.getLevel(), event.getPos());
        }
    }

    private GameEvents() {}
}
