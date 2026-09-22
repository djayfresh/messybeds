package com.djayfresh.messybeds.registry;

import com.djayfresh.messybeds.MessyBeds;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ColorCollection;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MessyBeds.MOD_ID);

    /** Block items for the beds; single-stack like vanilla beds. */
    public static final Map<DyeColor, DeferredItem<BlockItem>> BEDS;

    static {
        Map<DyeColor, DeferredItem<BlockItem>> beds = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values()) {
            beds.put(color, ITEMS.registerSimpleBlockItem(ModBlocks.byColor(color), p -> p.stacksTo(1)));
        }
        BEDS = Collections.unmodifiableMap(beds);
    }

    public static DeferredItem<BlockItem> byColor(DyeColor color) {
        return BEDS.get(color);
    }

    /** Visits the bed items in vanilla's creative-tab colour order (white first, black last). */
    public static void forEachInColorOrder(Consumer<DeferredItem<BlockItem>> consumer) {
        ColorCollection.VALUES.forEach(color -> consumer.accept(BEDS.get(color)));
    }

    private ModItems() {}
}
