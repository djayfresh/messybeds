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
    
    public static final RegistryObject<BlockEntityType<MessyBedEntity>> BED = BLOCK_ENTITY_REGISTER.register("messybed", () -> BlockEntityType.Builder.of(MessyBedEntity::new, 
        MessyBlocks.BLACK_BED,
        MessyBlocks.BLUE_BED,
        MessyBlocks.BROWN_BED,
        MessyBlocks.CYAN_BED,
        MessyBlocks.GRAY_BED,
        MessyBlocks.GREEN_BED,
        MessyBlocks.LIGHT_BLUE_BED,
        MessyBlocks.LIGHT_GRAY_BED,
        MessyBlocks.LIME_BED,
        MessyBlocks.MAGENTA_BED,
        MessyBlocks.ORANGE_BED,
        MessyBlocks.PINK_BED,
        MessyBlocks.PURPLE_BED,
        MessyBlocks.RED_BED,
        MessyBlocks.WHITE_BED,
        MessyBlocks.YELLOW_BED
    ).build(null));

    public static void registerBus(IEventBus bus){
        BLOCK_ENTITY_REGISTER.register(bus);
    }

}
