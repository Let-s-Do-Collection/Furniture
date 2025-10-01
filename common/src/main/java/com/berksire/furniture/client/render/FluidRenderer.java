package com.berksire.furniture.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.fluid.FluidStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("deprecation, unused")
public class FluidRenderer {

    private static final RenderType FLUID = RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);

    public static VertexConsumer getFluidBuilder(MultiBufferSource buffer) {
        return buffer.getBuffer(FLUID);
    }

    public static void renderFluidBox(FluidStack fluidStack, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax, MultiBufferSource buffer, PoseStack ms, int light, boolean renderBottom, Level level, BlockPos blockPos) {
        VertexConsumer builder = getFluidBuilder(buffer);
        BlockState state = Blocks.WATER.defaultBlockState();
        TextureAtlasSprite fluidTexture = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModel(state)
                .getParticleIcon();

        int color = 0x2A4DA0FF;
        int blockLightIn = (light >> 4) & 0xF;
        light = (light & 0xF00000) | (blockLightIn << 4);

        ms.pushPose();

        for (int pass = 0; pass < 2; pass++) {
            for (Direction side : Direction.values()) {
                if (side == Direction.DOWN && !renderBottom) continue;

                boolean positive = side.getAxisDirection() == Direction.AxisDirection.POSITIVE;
                if (side.getAxis().isHorizontal()) {
                    if (side.getAxis() == Direction.Axis.X) {
                        renderStillTiledFace(side, zMin, yMin, zMax, yMax, positive ? xMax : xMin, builder, ms, light, color, fluidTexture);
                    } else {
                        renderStillTiledFace(side, xMin, yMin, xMax, yMax, positive ? zMax : zMin, builder, ms, light, color, fluidTexture);
                    }
                } else {
                    renderStillTiledFace(side, xMin, zMin, xMax, zMax, positive ? yMax : yMin, builder, ms, light, color, fluidTexture);
                }
            }
        }

        ms.popPose();
    }

    public static void renderStillTiledFace(Direction dir, float left, float down, float right, float up, float depth, VertexConsumer builder, PoseStack ms, int light, int color, TextureAtlasSprite texture) {
        renderTiledFace(dir, left, down, right, up, depth, builder, ms, light, color, texture, 1);
    }

    public static void renderTiledFace(Direction dir, float left, float down, float right, float up, float depth, VertexConsumer builder, PoseStack ms, int light, int color, TextureAtlasSprite texture, float textureScale) {
        boolean positive = dir.getAxisDirection() == Direction.AxisDirection.POSITIVE;
        boolean horizontal = dir.getAxis().isHorizontal();
        boolean x = dir.getAxis() == Direction.Axis.X;

        float u1 = texture.getU0();
        float u2 = texture.getU1();
        float v1 = texture.getV0();
        float v2 = texture.getV1();

        if (horizontal) {
            if (x) {
                putVertex(builder, ms, depth, up, positive ? right : left, color, u1, v1, dir, light);
                putVertex(builder, ms, depth, down, positive ? right : left, color, u1, v2, dir, light);
                putVertex(builder, ms, depth, down, positive ? left : right, color, u2, v2, dir, light);
                putVertex(builder, ms, depth, up, positive ? left : right, color, u2, v1, dir, light);
            } else {
                putVertex(builder, ms, positive ? left : right, up, depth, color, u1, v1, dir, light);
                putVertex(builder, ms, positive ? left : right, down, depth, color, u1, v2, dir, light);
                putVertex(builder, ms, positive ? right : left, down, depth, color, u2, v2, dir, light);
                putVertex(builder, ms, positive ? right : left, up, depth, color, u2, v1, dir, light);
            }
        } else {
            putVertex(builder, ms, left, depth, positive ? down : up, color, u1, v1, dir, light);
            putVertex(builder, ms, left, depth, positive ? up : down, color, u1, v2, dir, light);
            putVertex(builder, ms, right, depth, positive ? up : down, color, u2, v2, dir, light);
            putVertex(builder, ms, right, depth, positive ? down : up, color, u2, v1, dir, light);
        }
    }

    private static void putVertex(VertexConsumer builder, PoseStack ms, float x, float y, float z, int color, float u, float v, Direction face, int light) {
        Vec3i normal = face.getNormal();
        PoseStack.Pose pose = ms.last();
        int a = color >> 24 & 0xff;
        int r = color >> 16 & 0xff;
        int g = color >> 8 & 0xff;
        int b = color & 0xff;

        r = (int)(r * 0.7f);
        g = (int)(g * 0.7f);
        b = (int)(b * 0.9f);

        builder.addVertex(pose.pose(), x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(normal.getX(), normal.getY(), normal.getZ());
    }
}
