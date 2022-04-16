package com.djayfresh.messybeds.client.renderer.blockentity;

import com.djayfresh.messybeds.block.MessyBedBlock;
import com.djayfresh.messybeds.block.entity.MessyBedEntity;
import com.djayfresh.messybeds.block.entity.MessyEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
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
    private final ModelPart headRoot;
    private final ModelPart footRoot;

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

    public void render(MessyBedEntity p_112205_, float p_112206_, PoseStack p_112207_, MultiBufferSource p_112208_,
            int p_112209_, int p_112210_) {
        Material material = Sheets.BED_TEXTURES[p_112205_.getColor().getId()];
        Level level = p_112205_.getLevel();
        if (level != null) {
            BlockState blockState = p_112205_.getBlockState();
            DoubleBlockCombiner.NeighborCombineResult<? extends MessyBedEntity> neighborCombineResult = DoubleBlockCombiner
                    .combineWithNeigbour(MessyEntities.BED.get(), MessyBedBlock::getBlockType,
                            MessyBedBlock::getConnectedDirection, ChestBlock.FACING, blockState, level,
                            p_112205_.getBlockPos(), (p_112202_, p_112203_) -> {
                                return false;
                            });
            int i = neighborCombineResult.<Int2IntFunction>apply(new BrightnessCombiner<>()).get(p_112209_);
            this.renderPiece(p_112207_, p_112208_,
                    blockState.getValue(MessyBedBlock.PART) == BedPart.HEAD ? this.headRoot : this.footRoot,
                    blockState.getValue(MessyBedBlock.FACING), material, i, p_112210_, false);
        } else {
            this.renderPiece(p_112207_, p_112208_, this.headRoot, Direction.SOUTH, material, p_112209_, p_112210_,
                    false);
            this.renderPiece(p_112207_, p_112208_, this.footRoot, Direction.SOUTH, material, p_112209_, p_112210_,
                    true);
        }

    }

    private void renderPiece(PoseStack p_173542_, MultiBufferSource p_173543_, ModelPart p_173544_, Direction p_173545_,
            Material p_173546_, int p_173547_, int p_173548_, boolean p_173549_) {
        p_173542_.pushPose();
        p_173542_.translate(0.0D, 0.5625D, p_173549_ ? -1.0D : 0.0D);
        p_173542_.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        p_173542_.translate(0.5D, 0.5D, 0.5D);
        p_173542_.mulPose(Vector3f.ZP.rotationDegrees(180.0F + p_173545_.toYRot()));
        p_173542_.translate(-0.5D, -0.5D, -0.5D);
        VertexConsumer vertexConsumer = p_173546_.buffer(p_173543_, RenderType::entitySolid);
        p_173544_.render(p_173542_, vertexConsumer, p_173547_, p_173548_);
        p_173542_.popPose();
    }

    public static void register() {
        BlockEntityRenderers.register(MessyEntities.BED.get(), MessyBedRenderer::new);
    }
}