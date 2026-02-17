package com.berksire.furniture.client.render.block;

import com.berksire.furniture.Furniture;
import com.berksire.furniture.client.model.ClockModel;
import com.berksire.furniture.core.block.entity.ClockBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Objects;

public class ClockRenderer implements BlockEntityRenderer<ClockBlockEntity> {
    private final ClockModel<Entity> model;

    public ClockRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(ClockModel.LAYER_LOCATION);
        this.model = new ClockModel<>(root);
    }

    @Override
    public void render(ClockBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        long gameTime = Objects.requireNonNull(blockEntity.getLevel()).getDayTime() % 24000L;
        int hours = (int) ((gameTime / 1000L + 6L) % 24L);
        int minutes = (int) ((gameTime % 1000L) * 60L / 1000L);

        float minutesRotation = (float) (minutes * Math.PI / 30.0D);
        float hoursRotation = (float) (hours * Math.PI / 6.0D);
        this.model.setHandRotations(minutesRotation, hoursRotation);

        BlockState blockState = blockEntity.getBlockState();

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        float rotation = direction.getOpposite().toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        ResourceLocation texture = resolveClockTexture(blockState.getBlock());
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(texture));
        renderModel(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    private void renderModel(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay) {
        this.model.renderToBuffer(poseStack, vertexConsumer, light, overlay, 0xFFFFFF);
    }

    private static ResourceLocation resolveClockTexture(Block block) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);

        String woodType = blockId.getPath();
        if (woodType.endsWith("_clock")) {
            woodType = woodType.substring(0, woodType.length() - "_clock".length());
        }

        ResourceLocation candidate = Furniture.identifier("textures/entity/" + woodType + "_clock.png");
        if (Minecraft.getInstance().getResourceManager().getResource(candidate).isPresent()) {
            return candidate;
        }

        return Furniture.identifier("textures/entity/oak_clock.png");
    }
}