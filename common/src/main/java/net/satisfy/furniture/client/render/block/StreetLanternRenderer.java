package net.satisfy.furniture.client.render.block;

import net.satisfy.furniture.core.block.IronStreetLanternBlock;
import net.satisfy.furniture.core.block.entity.StreetLanternBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class StreetLanternRenderer implements BlockEntityRenderer<StreetLanternBlockEntity> {
    private static final float[] POT_X = {-5.5F / 16.0F, 21.5F / 16.0F};
    private static final float SOIL_Y = -11.0F / 16.0F;
    private static final float FLOWER_SCALE = 0.5F;

    @Override
    public void render(StreetLanternBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Level level = blockEntity.getLevel();
        BlockState state = blockEntity.getBlockState();
        if (level == null || !state.hasProperty(IronStreetLanternBlock.POTS) || !state.getValue(IronStreetLanternBlock.POTS)) {
            return;
        }
        Direction facing = state.getValue(IronStreetLanternBlock.FACING);
        for (int slot = 0; slot < 2; slot++) {
            ItemStack flower = blockEntity.getFlower(slot);
            if (!(flower.getItem() instanceof BlockItem blockItem)) continue;
            int light = LevelRenderer.getLightColor(level, IronStreetLanternBlock.potPos(blockEntity.getBlockPos(), facing, slot));

            poseStack.pushPose();
            poseStack.translate(0.5F, 0.0F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-(facing.toYRot() + 180.0F)));
            poseStack.translate(POT_X[slot] - 0.5F, SOIL_Y, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(67.0F));
            poseStack.scale(FLOWER_SCALE, FLOWER_SCALE, FLOWER_SCALE);
            poseStack.translate(-0.5F, 0.0F, -0.5F);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockItem.getBlock().defaultBlockState(), poseStack, bufferSource, light, combinedOverlay);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(StreetLanternBlockEntity blockEntity) {
        return true;
    }
}
