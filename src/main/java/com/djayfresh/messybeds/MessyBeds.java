package com.djayfresh.messybeds;

import com.djayfresh.messybeds.event.ModBusEvents;
import com.djayfresh.messybeds.registry.ModAttachments;
import com.djayfresh.messybeds.registry.ModBlocks;
import com.djayfresh.messybeds.registry.ModItems;
import com.djayfresh.messybeds.registry.ModLootModifiers;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

/**
 * Messy Beds for NeoForge.
 * Entry point: wires the deferred registers onto the mod event bus, defines the creative tab and registers the config.
 */
@Mod(MessyBeds.MOD_ID)
public class MessyBeds {
    public static final String MOD_ID = "messybeds";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MESSY_TAB = CREATIVE_MODE_TABS.register("messy_beds", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MOD_ID))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> ModItems.byColor(DyeColor.WHITE).get().getDefaultInstance())
            .displayItems((parameters, output) -> ModItems.forEachInColorOrder(item -> output.accept(item.get())))
            .build());

    public MessyBeds(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModLootModifiers.SERIALIZERS.register(modEventBus);
        ModAttachments.ATTACHMENTS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(ModBusEvents::onExtendPoiTypes);
        modEventBus.addListener(ModBusEvents::onBuildCreativeTabContents);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
