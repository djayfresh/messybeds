package com.djayfresh.messybeds.item;

import com.djayfresh.messybeds.MessyBeds;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MessyItems {
    public static final CreativeModeTab TAB_MESSY_ITEMS = new CreativeModeTab(1, "messyItems") {
        public ItemStack makeIcon() {
            return new ItemStack(Blocks.WHITE_BED);
        }
    };

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MessyBeds.MOD_ID);

    public static void registerBus(IEventBus eventBus) {
        MessyItems.ITEMS.register(eventBus);
    }
}
