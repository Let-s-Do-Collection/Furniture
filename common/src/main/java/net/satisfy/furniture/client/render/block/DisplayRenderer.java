package net.satisfy.furniture.client.render.block;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.block.entity.DisplayBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DisplayRenderer implements BlockEntityRenderer<DisplayBlockEntity> {
    private static final float BASE_HEIGHT = 0.55F;
    private static final float BOB_AMPLITUDE = 0.03F;
    private static final float ROTATION_SPEED = 1.5F;

    public DisplayRenderer() {
    }

    @Override
    public void render(DisplayBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        ItemStack itemStack = blockEntity.getDisplayedItem();
        Level level = blockEntity.getLevel();
        if (itemStack.isEmpty() || level == null) {
            return;
        }
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model = itemRenderer.getModel(itemStack, level, null, 0);
        BlockPos pos = blockEntity.getBlockPos();
        float time = level.getGameTime() + partialTick + (pos.asLong() & 0xFF) * 7.0F;
        float bob = PlatformHelper.isDisplayBobbingEnabled() ? (float) Math.sin(time / 12.0F) * BOB_AMPLITUDE : 0.0F;
        float groundScale = model.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();

        poseStack.pushPose();
        poseStack.translate(0.5F, BASE_HEIGHT + bob + 0.25F * groundScale, 0.5F);
        if (PlatformHelper.isDisplayRotationEnabled()) {
            poseStack.mulPose(Axis.YP.rotationDegrees(time * ROTATION_SPEED % 360.0F));
        }
        poseStack.scale(0.75F, 0.75F, 0.75F);
        itemRenderer.render(itemStack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, combinedLight, combinedOverlay, model);
        poseStack.popPose();
    }
}
