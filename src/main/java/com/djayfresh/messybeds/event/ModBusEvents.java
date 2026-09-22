package com.djayfresh.messybeds.event;

import com.djayfresh.messybeds.registry.ModBlocks;
import com.djayfresh.messybeds.registry.ModItems;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.neoforge.common.world.poi.ExtendPoiTypesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/** Mod-bus listeners registered from the mod constructor. */
public final class ModBusEvents {

    /**
     * Villagers only claim beds whose head state is in the {@code minecraft:home} point of interest.
     * Vanilla builds that set from its own beds in code, so every messy bed head state is added here.
     * Without this, a village whose beds were all replaced would have no homes.
     */
    public static void onExtendPoiTypes(ExtendPoiTypesEvent event) {
        Set<BlockState> headStates = new HashSet<>();
        ModBlocks.BEDS.values().forEach(bed -> bed.get().getStateDefinition().getPossibleStates().stream()
                .filter(state -> state.getValue(BedBlock.PART) == BedPart.HEAD)
                .forEach(headStates::add));
        event.addStatesToPoi(PoiTypes.HOME, headStates);
    }

    /** Lists the messy beds right after the vanilla beds in the Functional Blocks tab. */
    public static void onBuildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
            return;
        }
        ItemStack after = new ItemStack(Items.BED.black());
        ModItems.forEachInColorOrder(item -> {
            ItemStack stack = new ItemStack(item.get());
            event.insertAfter(after, stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        });
    }

    private ModBusEvents() {}
}
