package com.djayfresh.messybeds.event.loot;

import java.util.ArrayList;
import java.util.List;

import com.djayfresh.messybeds.block.MessyBlocks;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

public class MessyBedsContainerModifier extends LootModifier {

    protected MessyBedsContainerModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        // generatedLoot is the loot that would be dropped, if we wouldn't add or replace
        // anything!
        List<ItemStack> toAdd = new ArrayList<ItemStack>();

        generatedLoot.forEach((item) -> {
            if (item.getItem() instanceof BedItem) {
                BedItem bedItem = (BedItem) item.getItem();
                BedBlock bedBlock = (BedBlock) bedItem.getBlock();
                toAdd.add(new ItemStack(MessyBlocks.getByDyeColor(bedBlock.getColor()), 1));
            }
        });

        toAdd.forEach((item) -> {
            generatedLoot.add(item);
        });

        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<MessyBedsContainerModifier> {

        @Override
        public MessyBedsContainerModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditionsIn) {
            return new MessyBedsContainerModifier(conditionsIn);
        }

        @Override
        public JsonObject write(MessyBedsContainerModifier instance) {
            return new JsonObject();
        }
    }
}
