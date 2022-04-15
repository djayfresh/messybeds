package com.djayfresh.messybeds.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public class MessyBed extends BedBlock {
    public String name;

    public MessyBed(String name, DyeColor dyeColor, Properties properties) {
        super(dyeColor, properties);
        this.name = name;
    }

    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player,
            InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        } else {
            if (blockState.getValue(PART) != BedPart.HEAD) {
                blockPos = blockPos.relative(blockState.getValue(FACING));
                blockState = level.getBlockState(blockPos);
                if (!blockState.is(this)) {
                    return InteractionResult.CONSUME;
                }
            }

            if (!canSetSpawn(level)) {
                level.removeBlock(blockPos, false);
                BlockPos blockpos = blockPos.relative(blockState.getValue(FACING).getOpposite());
                if (level.getBlockState(blockpos).is(this)) {
                    level.removeBlock(blockpos, false);
                }

                level.explode((Entity) null, DamageSource.badRespawnPointExplosion(),
                        (ExplosionDamageCalculator) null, (double) blockPos.getX() + 0.5D,
                        (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D, 5.0F, true,
                        Explosion.BlockInteraction.DESTROY);
                return InteractionResult.SUCCESS;
            } else if (blockState.getValue(OCCUPIED)) {
                if (!this.kickVillagerOutOfBed(level, blockPos)) {
                    player.displayClientMessage(new TranslatableComponent("block.minecraft.bed.occupied"), true);
                }

                return InteractionResult.SUCCESS;
            } else {
                player.startSleepInBed(blockPos).ifLeft((p_49477_) -> {
                    if (p_49477_ != null) {
                        player.displayClientMessage(p_49477_.getMessage(), true);
                    }

                });
                return InteractionResult.SUCCESS;
            }
        }
    }

    private boolean kickVillagerOutOfBed(Level p_49491_, BlockPos p_49492_) {
        List<Villager> list = p_49491_.getEntitiesOfClass(Villager.class, new AABB(p_49492_), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        } else {
            list.get(0).stopSleeping();
            return true;
        }
    }
}
