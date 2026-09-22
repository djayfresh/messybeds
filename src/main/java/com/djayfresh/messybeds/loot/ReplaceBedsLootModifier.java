package com.djayfresh.messybeds.loot;

import com.djayfresh.messybeds.block.MessyBedBlock;
import com.djayfresh.messybeds.registry.ModItems;
import com.djayfresh.messybeds.registry.ModLootModifiers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * Global loot modifier: any vanilla bed in generated loot (chests, barrels, minecarts, fishing, mob drops)
 * becomes the messy bed of the same colour. Straw beds are not {@link BedBlock}s and pass through untouched.
 */
public class ReplaceBedsLootModifier extends LootModifier {
    public static final MapCodec<ReplaceBedsLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
            codecStart(instance).apply(instance, ReplaceBedsLootModifier::new));

    public ReplaceBedsLootModifier(Optional<Holder<LootItemCondition>> condition, int priority) {
        super(condition, priority);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (int i = 0; i < generatedLoot.size(); i++) {
            ItemStack stack = generatedLoot.get(i);
            if (stack.getItem() instanceof BlockItem blockItem
                    && blockItem.getBlock() instanceof BedBlock bed
                    && !(bed instanceof MessyBedBlock)) {
                generatedLoot.set(i, stack.transmuteCopy(ModItems.byColor(bed.getColor()).get()));
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return ModLootModifiers.REPLACE_BEDS.get();
    }
}
