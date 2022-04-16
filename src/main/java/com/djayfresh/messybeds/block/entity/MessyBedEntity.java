package com.djayfresh.messybeds.block.entity;

import com.djayfresh.messybeds.block.MessyBedBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MessyBedEntity extends BlockEntity {
   private DyeColor color;

   public MessyBedEntity(BlockPos blockPos, BlockState blockState) {
      super(MessyEntities.BED.get(), blockPos, blockState);
      this.color = ((MessyBedBlock)blockState.getBlock()).getColor();
   }

   public MessyBedEntity(BlockPos p_155118_, BlockState p_155119_, DyeColor p_155120_) {
      super(MessyEntities.BED.get(), p_155118_, p_155119_);
      this.color = p_155120_;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public DyeColor getColor() {
      return this.color;
   }

   public void setColor(DyeColor p_58730_) {
      this.color = p_58730_;
   }
}