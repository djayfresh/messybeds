package com.djayfresh.messybeds.event.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import com.djayfresh.messybeds.MessyBeds;

@Mod.EventBusSubscriber(modid = MessyBeds.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MessyBedsBusEvents {

    @SubscribeEvent
    public static void register(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        IForgeRegistry<GlobalLootModifierSerializer<?>> registry = event.getRegistry();
        registry.register(new MessyBedsContainerModifier.Serializer().setRegistryName(new ResourceLocation(MessyBeds.MOD_ID,"messybeds_container_loot")));
    }
}