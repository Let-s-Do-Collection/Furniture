package com.berksire.furniture.client.render;

import com.berksire.furniture.core.block.FishTankBlock;
import com.berksire.furniture.core.block.entity.FishTankBlockEntity;
import com.berksire.furniture.client.model.FishTankModel;
import com.berksire.furniture.core.registry.ObjectRegistry;
import com.berksire.furniture.core.util.FurnitureIdentifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.architectury.fluid.FluidStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.Fluids;
import org.joml.Quaternionf;

public class FishTankRenderer implements BlockEntityRenderer<FishTankBlockEntity> {
    private static final ResourceLocation NORMAL_TEXTURE = FurnitureIdentifier.parseIdentifier("textures/entity/copper_fish_tank.png");
    private static final ResourceLocation IRON_TEXTURE = FurnitureIdentifier.parseIdentifier( "textures/entity/iron_fish_tank.png");
    private final FishTankModel model;

    public FishTankRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new FishTankModel(context.bakeLayer(FishTankModel.LAYER_LOCATION));
    }

    @Override
    public void render(FishTankBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        // Only render once
        boolean isFoot = blockEntity.getBlockState().getValue(FishTankBlock.PART) == BedPart.FOOT;
        if (!isFoot) return;

        poseStack.pushPose();

        poseStack.translate(0.5, 0, 0.5);

        Quaternionf rotationCorrector = Axis.YN.rotationDegrees(
                blockEntity.getBlockState().getValue(FishTankBlock.FACING).toYRot()
        );
        poseStack.mulPose(rotationCorrector);
        poseStack.mulPose(Axis.YN.rotationDegrees(90));

        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        ResourceLocation texture;
        if (blockEntity.getBlockState().getBlock() == ObjectRegistry.IRON_FISH_TANK.get()) {
            texture = IRON_TEXTURE;
        } else {
            texture = NORMAL_TEXTURE;
        }
        VertexConsumer vertexConsumer = buffers.getBuffer(model.renderType(texture));

        Level world = blockEntity.getLevel();
        assert world != null;
        float renderTick = (world.getGameTime() % 24000L) + Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
        this.model.setupAnim(blockEntity, renderTick);

        if (blockEntity.getBlockState().getValue(FishTankBlock.HAS_COD)) {
            this.model.cod_1.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
            this.model.cod_2.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
            this.model.cod_3.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
            this.model.cod_4.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }

        if (blockEntity.getBlockState().getValue(FishTankBlock.HAS_PUFFERFISH)) {
            poseStack.pushPose();
            poseStack.translate(0.5, -0.6, 0);
            this.model.pufferfish_1.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0.9, -0.8, 0.1);
            this.model.pufferfish_2.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        if (blockEntity.getBlockState().getValue(FishTankBlock.HAS_SALMON)) {
            poseStack.pushPose();
            poseStack.translate(0, 0, 0.05);
            this.model.salmon_1.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
            this.model.salmon_2.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }

        this.model.tank.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        this.model.decoration.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        poseStack.pushPose();

        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(rotationCorrector);
        poseStack.translate(-0.5, 0, -0.5);

        FluidRenderer.renderFluidBox(FluidStack.create(Fluids.WATER, 100L), 1f / 16 + 1f / 128, 2f / 16, 1f / 16 + 1f / 128, 15f / 16 - 1f / 128, 13.6f / 16, 31f / 16 - 1f / 128, buffers, poseStack, light, false, blockEntity.getLevel(), blockEntity.getBlockPos());
        poseStack.popPose();
    }
}