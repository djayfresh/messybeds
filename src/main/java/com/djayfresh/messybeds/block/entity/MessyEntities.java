package com.djayfresh.messybeds.block.entity;

import com.djayfresh.messybeds.MessyBeds;
import com.djayfresh.messybeds.block.MessyBlocks;
import com.mojang.datafixers.types.Type;

import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MessyEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MessyBeds.MOD_ID);
    
    public static final BlockEntityType<MessyBedEntity> BED = build("messybed", BlockEntityType.Builder.of(MessyBedEntity::new, MessyBlocks.RED_BED, MessyBlocks.WHITE_BED, MessyBlocks.ORANGE_BED));

    private static BlockEntityType<MessyBedEntity> build(String name, BlockEntityType.Builder<?> builder) {

        Type<?> type = Util.fetchChoiceType(References.BLOCK_ENTITY, name);
        return (BlockEntityType<MessyBedEntity>) BLOCK_ENTITY_REGISTER.register("messybed", () -> builder.build(type)).get();
    }
}
