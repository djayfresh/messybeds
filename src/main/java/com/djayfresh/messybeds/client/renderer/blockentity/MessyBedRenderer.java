package com.djayfresh.messybeds.client.renderer.blockentity;

import com.djayfresh.messybeds.block.MessyBedBlock;
import com.djayfresh.messybeds.block.entity.MessyBedEntity;
import com.djayfresh.messybeds.block.entity.MessyEntities;
import com.djayfresh.messybeds.client.renderer.MessySheets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;

import org.slf4j.Logger;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MessyBedRenderer implements BlockEntityRenderer<MessyBedEntity> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ModelPart headRoot;
    private final ModelPart footRoot;
    // private Boolean messageShown = false;
    private Boolean messageShown2 = false;

    public MessyBedRenderer(BlockEntityRendererProvider.Context p_173540_) {
        this.headRoot = p_173540_.bakeLayer(ModelLayers.BED_HEAD);
        this.footRoot = p_173540_.bakeLayer(ModelLayers.BED_FOOT);
    }

    public static LayerDefinition createHeadLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("main",
                CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), PartPose.ZERO);
        partDefinition.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(50, 6).addBox(0.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F),
                PartPose.rotation(((float) Math.PI / 2F), 0.0F, ((float) Math.PI / 2F)));
        partDefinition.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(50, 18).addBox(-16.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F),
                PartPose.rotation(((float) Math.PI / 2F), 0.0F, (float) Math.PI));
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    public static LayerDefinition createFootLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        partDefinition.addOrReplaceChild("main",
                CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F), PartPose.ZERO);
        partDefinition.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(50, 0).addBox(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F),
                PartPose.rotation(((float) Math.PI / 2F), 0.0F, 0.0F));
        partDefinition.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(50, 12).addBox(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F),
                PartPose.rotation(((float) Math.PI / 2F), 0.0F, ((float) Math.PI * 1.5F)));
        return LayerDefinition.create(meshDefinition, 64, 64);
    }

    public void render(MessyBedEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource,
            int combinedLight, int combinedOverlay) {
        Material material = Sheets.BED_TEXTURES[entity.getColor().getId()];
        Level level = entity.getLevel();
        if (level != null) {
            BlockState blockState = entity.getBlockState();
            if (blockState.getValue(MessyBedBlock.MESSY)){
                material = MessySheets.MESSY_BED_TEXTURES[entity.getColor().getId()];
            }

            long timeOfDay = level.dayTime();
            long rainingOffset = level.isRaining()? 532 : 0;

            // if (level.isNight()){
            //     if (!messageShown) {
            //         messageShown = true;
            //         LOGGER.info("Can Sleep: isNight()");
            //     }
            //     material = MessySheets.TURN_DOWN_BED_TEXTURES[entity.getColor().getId()];
            // }
            // else {
            //     messageShown = false;
            // }

            if (((12542-rainingOffset) <= timeOfDay && timeOfDay <= (23459 + rainingOffset)) || level.isThundering()) {
                if (!messageShown2) {
                    messageShown2 = true;
                    LOGGER.info("Can Sleep: fancy logic");
                }
                material = MessySheets.TURN_DOWN_BED_TEXTURES[entity.getColor().getId()];
            }
            else {
                messageShown2 = false;
            }
            
            DoubleBlockCombiner.NeighborCombineResult<? extends MessyBedEntity> neighborCombineResult = DoubleBlockCombiner
                    .combineWithNeigbour(MessyEntities.BED.get(), MessyBedBlock::getBlockType,
                            MessyBedBlock::getConnectedDirection, ChestBlock.FACING, blockState, level,
                            entity.getBlockPos(), (p_112202_, p_112203_) -> {
                                return false;
                            });
            int i = neighborCombineResult.<Int2IntFunction>apply(new BrightnessCombiner<>()).get(combinedLight);
            this.renderPiece(poseStack, bufferSource,
                    blockState.getValue(MessyBedBlock.PART) == BedPart.HEAD ? this.headRoot : this.footRoot,
                    blockState.getValue(MessyBedBlock.FACING), material, i, combinedOverlay, false);
        } else {
            this.renderPiece(poseStack, bufferSource, this.headRoot, Direction.SOUTH, material, combinedLight, combinedOverlay,
                    false);
            this.renderPiece(poseStack, bufferSource, this.footRoot, Direction.SOUTH, material, combinedLight, combinedOverlay,
                    true);
        }

    }

    private void renderPiece(PoseStack poseStack, MultiBufferSource bufferSource, ModelPart modelPart, Direction direction,
            Material material, int combinedLight, int combinedOverlay, boolean p_173549_) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.5625D, p_173549_ ? -1.0D : 0.0D);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F + direction.toYRot()));
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        VertexConsumer vertexConsumer = material.buffer(bufferSource, RenderType::entitySolid);
        modelPart.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);
        poseStack.popPose();
    }

    public static void register() {
        BlockEntityRenderers.register(MessyEntities.BED.get(), MessyBedRenderer::new);
    }
}