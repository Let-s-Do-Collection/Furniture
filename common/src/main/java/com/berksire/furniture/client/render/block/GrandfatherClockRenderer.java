package com.berksire.furniture.client.render.block;

import com.berksire.furniture.Furniture;
import com.berksire.furniture.core.block.GrandfatherClockBlock;
import com.berksire.furniture.core.block.entity.GrandfatherClockBlockEntity;
import com.berksire.furniture.client.model.GrandfatherClockModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class GrandfatherClockRenderer implements BlockEntityRenderer<GrandfatherClockBlockEntity> {
    private static final Map<GrandfatherClockBlock.WoodType, ResourceLocation> TEXTURES = new EnumMap<>(GrandfatherClockBlock.WoodType.class);
    static {
        TEXTURES.put(GrandfatherClockBlock.WoodType.OAK, Furniture.identifier( "textures/entity/oak_grandfatherclock.png"));
        TEXTURES.put(GrandfatherClockBlock.WoodType.SPRUCE, Furniture.identifier( "textures/entity/spruce_grandfatherclock.png"));
        TEXTURES.put(GrandfatherClockBlock.WoodType.BIRCH, Furniture.identifier( "textures/entity/birch_grandfatherclock.png"));
        TEXTURES.put(GrandfatherClockBlock.WoodType.JUNGLE, Furniture.identifier( "textures/entity/jungle_grandfatherclock.png"));
        TEXTURES.put(GrandfatherClockBlock.WoodType.ACACIA, Furniture.identifier( "textures/entity/acacia_grandfatherclock.png"));
        TEXTURES.put(GrandfatherClockBlock.WoodType.DARK_OAK, Furniture.identifier(  "textures/entity/dark_oak_grandfatherclock.png"));
        TEXTURES.put(GrandfatherClockBlock.WoodType.MANGROVE, Furniture.identifier( "textures/entity/mangrove_grandfatherclock.png"));
        TEXTURES.put(GrandfatherClockBlock.WoodType.CHERRY, Furniture.identifier(  "textures/entity/cherry_grandfatherclock.png"));
    }

    private final GrandfatherClockModel<Entity> model;

    public GrandfatherClockRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(GrandfatherClockModel.LAYER_LOCATION);

        this.model = new GrandfatherClockModel<>(root);
    }

    @Override
    public void render(GrandfatherClockBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        BlockState blockstate = blockEntity.getBlockState();
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        Direction direction = blockstate.getValue(BlockStateProperties.HORIZONTAL_FACING);
        float rotation = direction.getOpposite().toYRot();
        poseStack.mulPose(new Quaternionf().rotateYXZ((float) Math.toRadians(rotation), 0.0F, 0.0F));

        long totalTime = System.currentTimeMillis();
        float smoothTime = (totalTime % 60000) / 50.0f;
        this.model.pendulum.zRot = (float) Math.sin(smoothTime * Math.PI / 30) * 0.15F;

        long gameTime = Objects.requireNonNull(blockEntity.getLevel()).getDayTime() % 24000;
        int hours = (int) ((gameTime / 1000 + 6) % 24);
        int minutes = (int) ((gameTime % 1000) * 60 / 1000);

        this.model.minutes.zRot = (float) (minutes * Math.PI / 30);
        this.model.hours.zRot = (float) (hours * Math.PI / 6);

        GrandfatherClockBlock.WoodType woodType = ((GrandfatherClockBlock) blockstate.getBlock()).getWoodType();
        ResourceLocation texture = TEXTURES.get(woodType);
        if (texture == null) {
            texture = TEXTURES.get(GrandfatherClockBlock.WoodType.OAK);
        }
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(texture));

        model.renderToBuffer(poseStack, vertexConsumer, combinedLight, combinedOverlay);
        poseStack.popPose();
    }
}

