package net.satisfy.furniture.client.render.block;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.client.render.BakedPartModels;
import net.satisfy.furniture.core.block.BoatInAJarBlock;
import net.satisfy.furniture.core.block.entity.BoatInAJarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class BoatInAJarRenderer implements BlockEntityRenderer<BoatInAJarBlockEntity> {
    private static final float PX = 1.0F / 16.0F;
    private static final float FOLLOW_SPEED = 10.0F;
    private static final float BASE_SPEED = 2.2F;

    @Override
    public void render(BoatInAJarBlockEntity be, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffers, int light, int overlay) {
        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof BoatInAJarBlock)) {
            return;
        }
        long now = System.nanoTime();
        float dt = be.lastRenderNanos == 0L ? 0.0F : Math.min((now - be.lastRenderNanos) / 1.0E9F, 0.1F);
        be.lastRenderNanos = now;
        float target = Mth.clamp(be.getEnergy() / BoatInAJarBlockEntity.maxEnergy(), 0.0F, 1.0F);
        if (PlatformHelper.isBoatInAJarThunderRockingEnabled() && be.getLevel() != null) {
            target = Math.max(target, be.getLevel().getThunderLevel(partialTick) * 0.55F);
        }
        be.renderEnergy += (target - be.renderEnergy) * (1.0F - (float) Math.exp(-FOLLOW_SPEED * dt));
        float amp = be.renderEnergy;
        be.renderTime = (be.renderTime + dt * BASE_SPEED * PlatformHelper.getBoatInAJarAnimationSpeed() / 100.0F * (1.0F + amp * 6.0F)) % (Mth.TWO_PI * 100.0F);
        float t = be.renderTime;
        boolean broken = state.getValue(BoatInAJarBlock.BROKEN);

        ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.getValue(BoatInAJarBlock.FACING).toYRot()));
        poseStack.translate(-0.5F, 0.0F, -0.5F);

        float jarTilt = amp * 14.0F * Mth.sin(t) + amp * amp * 4.0F * Mth.sin(t * 3.7F);
        float jarRoll = amp * 18.0F * Mth.sin(t * 1.3F + 1.0F) + amp * amp * 5.0F * Mth.sin(t * 4.3F + 2.0F);
        poseStack.translate(amp * 1.5F * PX * Mth.sin(t * 5.1F), amp * 2.0F * PX * Math.abs(Mth.sin(t * 2.6F)), amp * 1.0F * PX * Mth.sin(t * 6.3F + 1.0F));
        pivot(poseStack, 8.0F, 4.0F, 8.0F, jarTilt, jarRoll);
        draw(renderer, poseStack, buffers.getBuffer(Sheets.cutoutBlockSheet()), state, BakedPartModels.BOAT_IN_A_JAR_JAR, light, overlay);
        draw(renderer, poseStack, buffers.getBuffer(Sheets.translucentCullBlockSheet()), state, BakedPartModels.BOAT_IN_A_JAR_WATER, light, overlay);

        float boatAmp = broken ? amp * 0.5F : amp;
        poseStack.pushPose();
        poseStack.translate(boatAmp * 1.5F * PX * Mth.sin(t * 0.7F), (0.2F + boatAmp * 0.6F) * PX * Mth.sin(t * 2.0F), 0.0F);
        float pitch = (0.8F + boatAmp * 6.0F) * Mth.sin(t * 1.7F + 0.4F);
        float roll = (1.5F + boatAmp * 10.0F) * Mth.sin(t * 2.1F + 0.5F);
        pivot(poseStack, 7.5F, 5.0F, 8.0F, pitch, roll);
        draw(renderer, poseStack, buffers.getBuffer(Sheets.cutoutBlockSheet()), state, broken ? BakedPartModels.BOAT_IN_A_JAR_BOAT_BROKEN : BakedPartModels.BOAT_IN_A_JAR_BOAT, light, overlay);
        poseStack.popPose();

        poseStack.popPose();
    }

    private static void pivot(PoseStack poseStack, float x, float y, float z, float zDegrees, float xDegrees) {
        poseStack.translate(x * PX, y * PX, z * PX);
        poseStack.mulPose(Axis.ZP.rotationDegrees(zDegrees));
        poseStack.mulPose(Axis.XP.rotationDegrees(xDegrees));
        poseStack.translate(-x * PX, -y * PX, -z * PX);
    }

    private static void draw(ModelBlockRenderer renderer, PoseStack poseStack, VertexConsumer consumer, BlockState state, ResourceLocation id, int light, int overlay) {
        BakedModel model = BakedPartModels.get(id);
        renderer.renderModel(poseStack.last(), consumer, state, model, 1.0F, 1.0F, 1.0F, light, overlay);
    }
}
