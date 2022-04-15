package com.djayfresh.messybeds.item;

import com.djayfresh.messybeds.block.MessyBed;
import com.djayfresh.messybeds.block.MessyBlocks;

import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.IForgeRegistry;

public class MessyItems {
    public static final BlockItem WHITE_BED = MessyItems.buildBedItem(MessyBlocks.WHITE_BED);
    public static final BlockItem RED_BED = MessyItems.buildBedItem(MessyBlocks.RED_BED);
    public static final BlockItem ORANGE_BED = MessyItems.buildBedItem(MessyBlocks.ORANGE_BED);

    public static final CreativeModeTab TAB_MESSY_ITEMS = new CreativeModeTab(1, "messyItems") {
        public ItemStack makeIcon() {
            return new ItemStack(Blocks.WHITE_BED);
        }
    };

    public static void registerAll(IForgeRegistry<Item> registry) {
        registry.register(MessyItems.WHITE_BED);
        registry.register(MessyItems.RED_BED);
        registry.register(MessyItems.ORANGE_BED);
    }

    private static BlockItem buildBedItem(MessyBed bed) {
        return new BedItem(bed, (new Item.Properties()).stacksTo(1).tab(MessyItems.TAB_MESSY_ITEMS));
    }
}
