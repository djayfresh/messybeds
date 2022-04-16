package com.djayfresh.messybeds.item;

import com.djayfresh.messybeds.MessyBeds;
import com.djayfresh.messybeds.block.MessyBed;
import com.djayfresh.messybeds.block.MessyBlocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class MessyItems {
    public static final BlockItem WHITE_BED = MessyItems.buildBedItem(MessyBlocks.WHITE_BED);
    public static final BlockItem RED_BED = MessyItems.buildBedItem(MessyBlocks.RED_BED);
    public static final BlockItem ORANGE_BED = MessyItems.buildBedItem(MessyBlocks.ORANGE_BED);

    public static final CreativeModeTab TAB_MESSY_ITEMS = new CreativeModeTab(-1, "messyItems") {
        public ItemStack makeIcon() {
            return new ItemStack(Blocks.WHITE_BED);
        }
    };

    public static void registerAll(IForgeRegistry<Item> registry) {
        registry.register(setup(MessyItems.WHITE_BED, "white_bed"));
        registry.register(setup(MessyItems.RED_BED, "red_bed"));
        registry.register(setup(MessyItems.ORANGE_BED, "orange_bed"));
    }

    private static BlockItem buildBedItem(MessyBed bed) {
        return new BedItem(bed, (new Item.Properties()).stacksTo(1).tab(MessyItems.TAB_MESSY_ITEMS));
    }

    private static <T extends IForgeRegistryEntry<Item>> T setup(final T entry, final String name) {
        return setup(entry, new ResourceLocation(MessyBeds.MOD_ID, name));
    }

    private static <T extends IForgeRegistryEntry<Item>> T setup(final T entry, final ResourceLocation registryName) {
        entry.setRegistryName(registryName);
        return entry;
    }
}
