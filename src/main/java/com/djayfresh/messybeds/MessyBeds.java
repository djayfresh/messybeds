package com.djayfresh.messybeds;

import com.djayfresh.messybeds.block.MessyBlocks;
import com.djayfresh.messybeds.block.entity.MessyEntities;
import com.djayfresh.messybeds.item.MessyItems;
import com.djayfresh.messybeds.client.renderer.blockentity.MessyBedRenderer;
import com.mojang.logging.LogUtils;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

import org.slf4j.Logger;

@Mod(MessyBeds.MOD_ID)
public class MessyBeds {
    public static final String MOD_ID = "messybeds";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MessyBeds() {
        // Register the setup method for modloading
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);
        
        MessyEntities.registerBus(eventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("MESSY BEDS >> {}", MessyBlocks.WHITE_BED.getRegistryName());
    }

    private void clientSetup(final FMLClientSetupEvent event){
        LOGGER.info("HELLO FROM ClIENT SETUP");

        event.enqueueWork(() -> {
            MessyBedRenderer.register();
        });
    }

    // You can use EventBusSubscriber to automatically subscribe events on the
    // contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onBlockEntitiesRegistry(final RegistryEvent.Register<BlockEntityType<?>> blockRegistryEvent) {
            // Register a new block here
            LOGGER.info("HELLO from Register Block Entities");
        }

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // Register a new block here
            LOGGER.info("HELLO from Register Block");
            IForgeRegistry<Block> registry = blockRegistryEvent.getRegistry();

            MessyBlocks.registerAll(registry);
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            // Register a new block here
            LOGGER.info("HELLO from Register Item");
            IForgeRegistry<Item> registry = itemRegistryEvent.getRegistry();

            MessyItems.registerAll(registry);
        }
    }

    // @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    // public static class ClientRegistryEvents {

    //     @SubscribeEvent
    //     public static void onBlockEntityRendererRegistry(final EntityRenderersEvent.RegisterRenderers blockEntityRendererRegistryEvent) {
    //         // Register a new block here
    //         LOGGER.info("HELLO from Register Block Entity Renderers");

    //         blockEntityRendererRegistryEvent.registerBlockEntityRenderer(MessyEntities.BED, () -> MessyBedRenderer::new);
    //     }
    // }
}
