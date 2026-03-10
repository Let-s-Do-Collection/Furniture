package com.berksire.furniture.client.render.block;

import com.berksire.furniture.Furniture;
import com.berksire.furniture.client.model.GrandfatherClockModel;
import com.berksire.furniture.core.block.GrandfatherClockBlock;
import com.berksire.furniture.core.block.entity.GrandfatherClockBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Objects;

public class GrandfatherClockRenderer implements BlockEntityRenderer<GrandfatherClockBlockEntity> {

    private static final String GRANDFATHER_CLOCK_SUFFIX = "_grandfather_clock";
    private static final ResourceLocation FALLBACK_TEXTURE = Furniture.identifier("textures/entity/oak_grandfather_clock.png");

    private final GrandfatherClockModel<Entity> model;

    public GrandfatherClockRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(GrandfatherClockModel.LAYER_LOCATION);
        this.model = new GrandfatherClockModel<>(root);
    }

    @Override
    public void render(GrandfatherClockBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        BlockState blockState = blockEntity.getBlockState();

        if (!blockState.hasProperty(GrandfatherClockBlock.PART) || blockState.getValue(GrandfatherClockBlock.PART) != GrandfatherClockBlock.Part.BOTTOM) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

        Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        poseStack.mulPose(Axis.YP.rotationDegrees(direction.toYRot()));

        this.model.pendulum.setPos(0.0F, 10.0F, 5.0F);
        this.model.minutes.setPos(0.0F, -24.0F, 0.0F);
        this.model.hours.setPos(0.0F, -24.0F, 0.05F);
        this.model.inner.setPos(0.0F, 34.0F, 4.0F);

        long totalTimeMillis = System.currentTimeMillis();
        float smoothTime = (totalTimeMillis % 60000L) / 50.0F;
        this.model.pendulum.zRot = (float) Math.sin(smoothTime * Math.PI / 30.0D) * 0.15F;

        long dayTime = Objects.requireNonNull(blockEntity.getLevel()).getDayTime() % 24000L;
        int totalMinutes = (int) ((dayTime * 60L) / 1000L);
        int minutes = totalMinutes % 60;
        int hours12 = (totalMinutes / 60) % 12;

        this.model.minutes.zRot = (float) (minutes * Math.PI / 30.0D);
        this.model.hours.zRot = (float) ((hours12 + (minutes / 60.0F)) * Math.PI / 6.0D);

        ResourceLocation texture = resolveTexture(blockState);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(texture));
        this.model.renderToBuffer(poseStack, vertexConsumer, combinedLight, combinedOverlay, -1);

        poseStack.popPose();
    }
    
    @Override
    public boolean shouldRenderOffScreen(GrandfatherClockBlockEntity blockEntity) {
        return true;
    }

    private static ResourceLocation resolveTexture(BlockState blockState) {
        ResourceLocation blockKey = BuiltInRegistries.BLOCK.getKey(blockState.getBlock());

        String blockPath = blockKey.getPath();
        if (!blockPath.endsWith(GRANDFATHER_CLOCK_SUFFIX)) {
            return FALLBACK_TEXTURE;
        }

        String woodTypeKey = blockPath.substring(0, blockPath.length() - GRANDFATHER_CLOCK_SUFFIX.length());
        if (woodTypeKey.isEmpty()) {
            return FALLBACK_TEXTURE;
        }

        return Furniture.identifier("textures/entity/" + woodTypeKey + "_grandfather_clock.png");
    }
}