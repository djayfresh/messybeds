package com.djayfresh.messybeds.block.entity;

import com.djayfresh.messybeds.MessyBeds;
import com.djayfresh.messybeds.block.MessyBlocks;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MessyEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MessyBeds.MOD_ID);
    
    public static final RegistryObject<BlockEntityType<MessyBedEntity>> BED = BLOCK_ENTITY_REGISTER.register("messybed", () -> BlockEntityType.Builder.of(MessyBedEntity::new, MessyBlocks.RED_BED, MessyBlocks.WHITE_BED, MessyBlocks.ORANGE_BED).build(null));

    public static void registerBus(IEventBus bus){
        BLOCK_ENTITY_REGISTER.register(bus);
    }

}
