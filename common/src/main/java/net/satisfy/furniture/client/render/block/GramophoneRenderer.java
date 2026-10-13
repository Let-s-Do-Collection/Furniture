package net.satisfy.furniture.client.render.block;

import net.satisfy.furniture.Furniture;
import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.block.GramophoneBlock;
import net.satisfy.furniture.core.block.entity.GramophoneBlockEntity;
import net.satisfy.furniture.client.model.GramophoneModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.joml.Quaternionf;

import java.util.Objects;

public class GramophoneRenderer implements BlockEntityRenderer<GramophoneBlockEntity> {
    private static final ResourceLocation TEXTURE = Furniture.identifier("textures/entity/gramophone.png");
    private static final float HORN_BELL_X = 0.0F;
    private static final float HORN_BELL_Y = -18.0F;
    private static final float HORN_BELL_Z = -3.0F;
    private final GramophoneModel<?> model;

    public GramophoneRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new GramophoneModel<>(context.bakeLayer(GramophoneModel.LAYER_LOCATION));
    }

    @Override
    public void render(GramophoneBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        BlockState blockState = blockEntity.getBlockState();
        if (blockState.getValue(GramophoneBlock.HALF) == DoubleBlockHalf.UPPER) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        float rotation = switch (direction) {
            case NORTH -> 0;
            case SOUTH -> 180;
            default -> direction.toYRot();
        };
        poseStack.mulPose(new Quaternionf().rotateYXZ((float) Math.toRadians(rotation), 0.0F, 0.0F));
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        ModelPart base = model.gramophone.getChild("base");
        model.disc.visible = false;
        base.render(poseStack, bufferSource.getBuffer(model.renderType(TEXTURE)), combinedLight, OverlayTexture.NO_OVERLAY);
        model.disc.visible = true;
        poseStack.pushPose();
        if (blockState.getValue(GramophoneBlock.HAS_RECORD) && PlatformHelper.isGramophoneHornPulseEnabled()) {
            float time = (Objects.requireNonNull(blockEntity.getLevel()).getGameTime() % 24000) + partialTicks;
            float pulse = 1.0F + (float) Math.sin(time * 0.6F) * 0.04F;
            poseStack.translate(HORN_BELL_X / 16.0F, HORN_BELL_Y / 16.0F, HORN_BELL_Z / 16.0F);
            poseStack.scale(pulse, pulse, 1.0F);
            poseStack.translate(-HORN_BELL_X / 16.0F, -HORN_BELL_Y / 16.0F, -HORN_BELL_Z / 16.0F);
        }
        model.gramophone.getChild("horn").render(poseStack, bufferSource.getBuffer(model.renderType(TEXTURE)), combinedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        if (blockState.getValue(GramophoneBlock.HAS_RECORD)) {
            float totalGameTime = (Objects.requireNonNull(blockEntity.getLevel()).getGameTime() % 360) + partialTicks;
            float discRotation = totalGameTime * 5 % 360;
            float verticalMovement = (float) Math.sin(totalGameTime * 0.1) * 0.002f;

            poseStack.pushPose();
            poseStack.translate(0.5, 0 + verticalMovement, -0.5);
            poseStack.translate(model.disc.x / 16.0F, 0.0, model.disc.z / 16.0F);
            poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(discRotation)));
            poseStack.translate(-model.disc.x / 16.0F, 0.0, -model.disc.z / 16.0F);
            model.disc.render(poseStack, bufferSource.getBuffer(model.renderType(TEXTURE)), combinedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

}
