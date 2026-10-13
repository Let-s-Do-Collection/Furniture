package net.satisfy.furniture.client.render.block;

import net.satisfy.furniture.client.handler.TelescopeViewHandler;
import net.satisfy.furniture.client.render.BakedPartModels;
import net.satisfy.furniture.core.block.TelescopeBlock;
import net.satisfy.furniture.core.block.entity.TelescopeBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class TelescopeRenderer implements BlockEntityRenderer<TelescopeBlockEntity> {
    public static final float REST_PITCH = 22.5F;
    private static final float MAX_YAW = 45.0F;
    private static final float PIVOT_X = 8.0F / 16.0F;
    private static final float PIVOT_Y = 6.0F / 16.0F;
    private static final float PIVOT_Z = 6.0F / 16.0F;

    private static final float FOLLOW_SPEED = 12.0F;
    private static final long INACTIVE_GRACE_NANOS = 250_000_000L;

    @Override
    public void render(TelescopeBlockEntity be, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffers, int light, int overlay) {
        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof TelescopeBlock)) {
            return;
        }
        if (!state.getValue(TelescopeBlock.ACTIVE)) {
            if (be.inactiveSinceNanos == 0L) {
                be.inactiveSinceNanos = System.nanoTime();
            }
            be.lastRenderNanos = 0L;
            be.renderYaw = 0.0F;
            be.renderPitch = 0.0F;
            if (System.nanoTime() - be.inactiveSinceNanos > INACTIVE_GRACE_NANOS) {
                return;
            }
            renderTube(be, state, poseStack, buffers, light, overlay);
            return;
        }
        be.inactiveSinceNanos = 0L;
        Direction facing = state.getValue(TelescopeBlock.FACING);
        float targetYaw = 0.0F;
        float targetPitch = 0.0F;
        boolean snap = false;
        UUID user = be.getUser();
        Player player = user != null && be.getLevel() != null ? be.getLevel().getPlayerByUUID(user) : null;
        if (player != null) {
            targetYaw = Mth.clamp(Mth.wrapDegrees(player.getViewYRot(partialTick) - facing.getOpposite().toYRot()), -MAX_YAW, MAX_YAW);
            targetPitch = -player.getViewXRot(partialTick) - REST_PITCH;
            snap = player == Minecraft.getInstance().player;
        }

        long now = System.nanoTime();
        float dt = be.lastRenderNanos == 0L ? 0.0F : Math.min((now - be.lastRenderNanos) / 1.0E9F, 0.1F);
        be.lastRenderNanos = now;
        if (snap) {
            be.renderYaw = targetYaw;
            be.renderPitch = targetPitch;
            if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && TelescopeViewHandler.isActiveAt(be.getBlockPos().below())) {
                return;
            }
        } else {
            float t = 1.0F - (float) Math.exp(-FOLLOW_SPEED * dt);
            be.renderYaw += (targetYaw - be.renderYaw) * t;
            be.renderPitch += (targetPitch - be.renderPitch) * t;
        }
        renderTube(be, state, poseStack, buffers, light, overlay);
    }

    private static void renderTube(TelescopeBlockEntity be, BlockState state, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        Direction facing = state.getValue(TelescopeBlock.FACING);
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
        poseStack.translate(-0.5F, 0.0F, -0.5F);
        poseStack.translate(PIVOT_X, PIVOT_Y, PIVOT_Z);
        poseStack.mulPose(Axis.YP.rotationDegrees(-be.renderYaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(be.renderPitch));
        poseStack.translate(-PIVOT_X, -PIVOT_Y, -PIVOT_Z);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffers.getBuffer(Sheets.cutoutBlockSheet()), state, BakedPartModels.get(BakedPartModels.TELESCOPE_TUBE), 1.0F, 1.0F, 1.0F, light, overlay);
        poseStack.popPose();
    }
}
