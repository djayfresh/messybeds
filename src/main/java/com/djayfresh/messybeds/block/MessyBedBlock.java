package com.djayfresh.messybeds.block;

import java.util.List;

import com.djayfresh.messybeds.block.entity.MessyBedEntity;
import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public class MessyBedBlock extends BedBlock {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final BooleanProperty MESSY = BooleanProperty.create("messy");
    private DyeColor color;

    public MessyBedBlock(DyeColor dyeColor, Properties properties) {
        super(dyeColor, properties);
        this.color = dyeColor;
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, BedPart.FOOT)
                .setValue(OCCUPIED, Boolean.valueOf(false)).setValue(MESSY, Boolean.valueOf(false)));
    }

    @Override()
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player,
            InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        } else {
            LOGGER.info("Clicked on MessyBedBlock");
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
            } else if (blockState.getValue(MESSY)) {
                LOGGER.info("Clicked on messy bed!");
                
                this.setMessy(level, blockPos, false);

                player.displayClientMessage(new TranslatableComponent("block.messybed.bed.cleaned"), true);

                return InteractionResult.SUCCESS;
            } else if (blockState.getValue(OCCUPIED)) {
                if (!this.kickVillagerOutOfBed(level, blockPos)) {
                    player.displayClientMessage(new TranslatableComponent("block.minecraft.bed.occupied"), true);
                }

                return InteractionResult.SUCCESS;
            } else {
                BlockPos currentBlockPos = blockPos;

                player.startSleepInBed(blockPos).ifLeft((p_49477_) -> {
                    if (p_49477_ != null) {
                        LOGGER.info("Error Sleeping: {}", p_49477_.getMessage());
                        player.displayClientMessage(p_49477_.getMessage(), true);
                    }
                }).ifRight((error) -> {
                    LOGGER.info("Safe sleep: {}", error);

                    this.setMessy(level, currentBlockPos, true);
                });
                return InteractionResult.SUCCESS;
            }
        }
    }

    private void setMessy(Level level, BlockPos blockPos, Boolean value){
        BlockState currentBlockState = level.getBlockState(blockPos);
        level.setBlock(blockPos, currentBlockState.setValue(MESSY, value), 3);
        level.blockUpdated(blockPos, this);

        blockPos = blockPos.relative(currentBlockState.getValue(FACING).getOpposite());
        BlockState nextBlockState = level.getBlockState(blockPos);
        if (nextBlockState.is(this)) {
            level.setBlock(blockPos, nextBlockState.setValue(MESSY, value), 3);
            level.blockUpdated(blockPos, this);
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

    @Override()
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49532_) {
        p_49532_.add(FACING, PART, OCCUPIED, MESSY);
    }

    @Override()
    public MessyBedEntity newBlockEntity(BlockPos p_152175_, BlockState p_152176_) {
        return new MessyBedEntity(p_152175_, p_152176_, this.color);
    }

    @Override()
    public BlockState updateShape(BlockState blockState, Direction direction, BlockState newBlockState,
            LevelAccessor levelAccessor, BlockPos blockPos, BlockPos newBlockPos) {
        if (newBlockState.is(this) && blockState.getValue(MESSY) != newBlockState.getValue(MESSY)) {
            blockState.setValue(MESSY, newBlockState.getValue(MESSY));
        }
        
        return super.updateShape(blockState, direction, newBlockState, levelAccessor, blockPos, newBlockPos);
    }
}
