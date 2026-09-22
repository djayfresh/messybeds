package com.djayfresh.messybeds.registry;

import com.djayfresh.messybeds.MessyBeds;
import com.djayfresh.messybeds.loot.ReplaceBedsLootModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MessyBeds.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<ReplaceBedsLootModifier>> REPLACE_BEDS =
            SERIALIZERS.register("replace_beds", () -> ReplaceBedsLootModifier.CODEC);

    private ModLootModifiers() {}
}
