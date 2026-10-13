package net.satisfy.furniture.client.render.block;

import net.satisfy.furniture.client.render.BakedPartModels;
import net.satisfy.furniture.core.block.CashRegisterBlock;
import net.satisfy.furniture.core.block.entity.CashRegisterBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class CashRegisterRenderer implements BlockEntityRenderer<CashRegisterBlockEntity> {
    private static final float ITEM_SIZE = 0.2F;
    private static final float TRAVEL = 6.0F / 16.0F;
    private static final float SPEED = 1.0F / (CashRegisterBlockEntity.CLOSE_ANIMATION_TICKS / 20.0F);

    @Override
    public void render(CashRegisterBlockEntity be, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffers, int light, int overlay) {
        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof CashRegisterBlock) || be.getLevel() == null) {
            be.renderProgress = 0.0F;
            be.lastRenderNanos = 0L;
            return;
        }
        long now = System.nanoTime();
        float dt = be.lastRenderNanos == 0L ? 0.0F : Math.min((now - be.lastRenderNanos) / 1.0E9F, 0.1F);
        be.lastRenderNanos = now;
        float target = be.isOpen() ? 1.0F : 0.0F;
        float step = SPEED * dt;
        be.renderProgress = be.renderProgress < target ? Math.min(target, be.renderProgress + step) : Math.max(target, be.renderProgress - step);
        float eased = 1.0F - (1.0F - be.renderProgress) * (1.0F - be.renderProgress);

        Direction facing = state.getValue(CashRegisterBlock.FACING);
        int frontLight = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(facing));

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
        poseStack.translate(-0.5F, 0.0F, -0.5F);
        poseStack.translate(0.0F, 0.0F, -TRAVEL * eased);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffers.getBuffer(Sheets.cutoutBlockSheet()), state, BakedPartModels.get(BakedPartModels.CASH_REGISTER_DRAWER), 1.0F, 1.0F, 1.0F, frontLight, overlay);

        for (int slot = 0; slot < CashRegisterBlockEntity.SLOTS; slot++) {
            ItemStack stack = be.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            float x = (12.0F - (slot + 0.5F) * 8.0F / CashRegisterBlockEntity.SLOTS) / 16.0F;
            int copies = stack.getCount() >= 32 ? 3 : stack.getCount() >= 8 ? 2 : 1;
            for (int i = 0; i < copies; i++) {
                poseStack.pushPose();
                poseStack.translate(x, (1.25F + i * 0.5F) / 16.0F, (5.5F + i * 0.75F) / 16.0F);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(15.0F + i * 25.0F));
                poseStack.scale(ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, frontLight, overlay, poseStack, buffers, be.getLevel(), (int) be.getBlockPos().asLong() + slot * 7 + i);
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }
}
