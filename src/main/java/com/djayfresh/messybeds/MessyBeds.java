package com.djayfresh.messybeds;

import com.djayfresh.messybeds.block.MessyBedBlock;
import com.djayfresh.messybeds.block.MessyBlocks;
import com.djayfresh.messybeds.block.entity.MessyEntities;
import com.djayfresh.messybeds.item.MessyItems;
import com.djayfresh.messybeds.client.renderer.MessySheets;
import com.djayfresh.messybeds.client.renderer.blockentity.MessyBedRenderer;
import com.mojang.logging.LogUtils;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
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
        // Register the setup method for mod loading
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);

        MessyEntities.registerBus(eventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM");
        LOGGER.info("MESSY BEDS >> {}", MessyBlocks.WHITE_BED.getRegistryName());
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("HELLO FROM ClIENT SETUP");

        event.enqueueWork(() -> {
            MessyBedRenderer.register();
        });
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class MinecraftEvents {

        @SubscribeEvent
        public static void onPlayerWakeup(final PlayerWakeUpEvent event) {
            LOGGER.info("HELLO from Player Slept in Bed");

            Level level = event.getPlayer().getLevel();

            if (level == null || level.isClientSide())  {
                LOGGER.info("HELLO Client Side");
                return;
            }

            event.getPlayer().getSleepingPos().ifPresent((blockPos) -> {
                ServerPlayer player = (ServerPlayer) event.getPlayer();

                LOGGER.info("Player was sleeping: {}", blockPos);

                BlockState blockState = level.getBlockState(blockPos);
                if (blockState.isBed(level, blockPos, player) && blockState.getBlock() instanceof MessyBedBlock) {
                    player.displayClientMessage(new TranslatableComponent("block.messybed.bed.messy"), true);
                }
            });
        }
    }

    // You can use EventBusSubscriber to automatically subscribe events on the
    // contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onBlockEntitiesRegistry(
                final RegistryEvent.Register<BlockEntityType<?>> blockRegistryEvent) {
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

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientRegistryEvents {

        @SubscribeEvent
        public static void onTextureStitch(TextureStitchEvent.Pre event) {
            ResourceLocation location = event.getAtlas().location();
            if (location.equals(Sheets.BED_SHEET)) {
                for (Material material : MessySheets.MESSY_BED_TEXTURES) {
                    event.addSprite(material.texture());
                }

                for (Material material : MessySheets.CLEAN_BED_TEXTURES) {
                    event.addSprite(material.texture());
                }

                for (Material material : MessySheets.TURN_DOWN_BED_TEXTURES) {
                    event.addSprite(material.texture());
                }
            }
        }

        // @SubscribeEvent
        // public static void onBlockEntityRendererRegistry(final
        // EntityRenderersEvent.RegisterRenderers blockEntityRendererRegistryEvent) {
        // // Register a new block here
        // LOGGER.info("HELLO from Register Block Entity Renderers");

        // blockEntityRendererRegistryEvent.registerBlockEntityRenderer(MessyEntities.BED,
        // () -> MessyBedRenderer::new);
        // }
    }
}
