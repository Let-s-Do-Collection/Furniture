package net.satisfy.furniture.client.render.block;

import net.minecraft.world.inventory.InventoryMenu;
import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.client.util.ShaderPackHelper;
import net.satisfy.furniture.core.block.MirrorBlock;
import net.satisfy.furniture.core.block.entity.MirrorBlockEntity;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.satisfy.foundation.block.VerticalConnectingType;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.satisfy.furniture.Furniture;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class MirrorRenderer implements BlockEntityRenderer<MirrorBlockEntity> {
    private static final int GRID = 8;

    private static final Region NEAR = new Region(false, 0, 24, 12, 6, 12, 20, 4);

    private static final Region FAR = new Region(true, 24, 48, 24, 8, 24, 100, 3);

    private static final float BLUR_RADIUS = 2.5f;
    private static final float[][] BLUR_TAPS = {{0, 0}, {1, 0.5f}, {-1, -0.5f}, {-0.5f, 1}, {0.5f, -1}, {1.5f, -1.5f}, {-1.5f, 1.5f}, {1.5f, 1.5f}, {-1.5f, -1.5f}};
    private static final float TINT_R = 0.92f, TINT_G = 0.88f, TINT_B = 0.78f;
    private static final float GLASS_DEPTH = 15f / 16f - 0.002f;
    private static final float GLASS_MIN_X = 3f / 16f;
    private static final float GLASS_MAX_X = 13f / 16f;

    private static final int LAYER_SOLID = 0, LAYER_CUTOUT = 1, LAYER_TRANSLUCENT = 2, LAYERS = 3;

    private static final int[] VIEWPORT = new int[4];
    private static final PoseStack REFLECTION_POSE = new PoseStack();
    private static final Vector4f CLIP = new Vector4f();
    private static final Matrix4f CLIP_MATRIX = new Matrix4f();
    private static final float[] CORNER = new float[3];
    private static final float[] GLASS_VERTS = new float[GRID * GRID * 4 * 5];
    private static final Matrix4f FRAME_VIEW = new Matrix4f();
    private static final Matrix4f FRAME_CLIP = new Matrix4f();
    private static Vec3 FRAME_CAM = Vec3.ZERO;
    private static int TEXTURE_COUNTER;
    private static final Map<Long, Reflection> REFLECTIONS = new HashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<Object> FAILED = new HashSet<>();
    private static ClientLevel lastLevel;

    public MirrorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MirrorBlockEntity be, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || be.getLevel() == null || !PlatformHelper.areMirrorReflectionsEnabled() || ShaderPackHelper.isRenderingShadowPass()) return;
        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof MirrorBlock)) return;

        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        Direction facing = state.getValue(MirrorBlock.FACING);
        Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());
        Vec3 center = Vec3.atCenterOf(be.getBlockPos());
        Vec3 planePoint = center.subtract(normal.scale(0.5 - 1.0 / 16.0));

        if (cam.subtract(planePoint).dot(normal) <= 0.01) return;
        double cameraRange = PlatformHelper.getMirrorRenderDistance();
        if (cam.distanceToSqr(center) > cameraRange * cameraRange) return;

        double planeCoord = facing.getAxis().choose(planePoint.x, planePoint.y, planePoint.z);
        long key = ((long) facing.ordinal() << 40) ^ Math.round(planeCoord * 16.0);

        Reflection reflection = REFLECTIONS.get(key);
        if (reflection == null) {
            int maxPlanes = PlatformHelper.getMaxMirrors();
            if (REFLECTIONS.size() >= maxPlanes) cleanup();
            if (REFLECTIONS.size() >= maxPlanes) return;
            reflection = new Reflection();
            REFLECTIONS.put(key, reflection);
        }
        reflection.lastUsed = System.nanoTime();
        reflection.facing = facing;
        reflection.planePoint = planePoint;
        reflection.normal = normal;
        reflection.requested = true;

        if (reflection.ready && reflection.texture != null) {
            drawGlass(poseStack, bufferSource, reflection.texture, be.getBlockPos(), facing, state.getValue(MirrorBlock.TYPE));
        }
    }

    public static void renderReflections() {
        Minecraft mc = Minecraft.getInstance();
        boolean enabled = PlatformHelper.areMirrorReflectionsEnabled();
        if (mc.level != lastLevel || (!enabled && !REFLECTIONS.isEmpty())) {
            clearAll();
            lastLevel = mc.level;
        }
        if (mc.level == null || !enabled) return;
        cleanup();
        if (REFLECTIONS.isEmpty()) return;
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cam = camera.getPosition();
        float partialTick = camera.getPartialTickTime();

        FRAME_CAM = cam;
        FRAME_VIEW.rotation(camera.rotation().conjugate(new Quaternionf()));
        FRAME_CLIP.set(RenderSystem.getProjectionMatrix()).mul(FRAME_VIEW);

        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.identity().mul(FRAME_VIEW);
        RenderSystem.applyModelViewMatrix();
        FogRenderer.setupNoFog();
        Lighting.setupLevel();

        try {
            for (Reflection reflection : REFLECTIONS.values()) {
                if (!reflection.requested) continue;
                reflection.requested = false;
                if (cam.subtract(reflection.planePoint).dot(reflection.normal) <= 0.01) continue;
                try {
                    if (reflection.update(partialTick, cam, camera.getXRot(), camera.getYRot(), mc.level.getGameTime())) {
                        renderReflection(mc, reflection, reflection.facing, reflection.planePoint, reflection.normal, cam, partialTick);
                    }
                    reflection.ready = true;
                } catch (Exception e) {
                    logFailure("mirror facing " + reflection.facing + " at " + reflection.planePoint, e);
                }
            }

        } finally {
            modelView.popMatrix();
            RenderSystem.applyModelViewMatrix();
        }
    }

    private static Matrix4f mirrorMatrix(Direction.Axis axis, Vec3 planeRel) {
        return new Matrix4f()
                .translate((float) planeRel.x, (float) planeRel.y, (float) planeRel.z)
                .scale(axis == Direction.Axis.X ? -1 : 1, 1, axis == Direction.Axis.Z ? -1 : 1)
                .translate((float) -planeRel.x, (float) -planeRel.y, (float) -planeRel.z);
    }

    private static void renderReflection(Minecraft mc, Reflection reflection, Direction facing, Vec3 planePoint, Vec3 normal, Vec3 cam, float partialTick) {
        RenderTarget main = mc.getMainRenderTarget();
        int width = main.width, height = main.height;
        if (reflection.target == null) {
            reflection.target = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            reflection.target.setFilterMode(GL11.GL_LINEAR);
            reflection.texture = ResourceLocation.fromNamespaceAndPath(Furniture.MOD_ID, "mirror/" + TEXTURE_COUNTER++);
            mc.getTextureManager().register(reflection.texture, new TargetTexture(reflection));
        } else if (reflection.target.width != width || reflection.target.height != height) {
            reflection.target.resize(width, height, Minecraft.ON_OSX);
            reflection.target.setFilterMode(GL11.GL_LINEAR);
        }

        ClientLevel level = mc.level;
        long time = level.getGameTime();
        if (reflection.anchor == null) reflection.anchor = BlockPos.containing(planePoint);
        BlockPos origin = reflection.anchor;
        reflection.near.step(mc, level, origin, facing, planePoint, normal, time);
        reflection.far.step(mc, level, origin, facing, planePoint, normal, time);

        Vec3 sky = level.getSkyColor(cam, partialTick);
        reflection.target.setClearColor((float) sky.x, (float) sky.y, (float) sky.z, 1f);

        int previousFbo = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        int[] viewport = VIEWPORT;
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

        try {
            reflection.target.clear(Minecraft.ON_OSX);
            reflection.target.bindWrite(true);
            drawScene(mc, reflection, facing, planePoint, normal, cam, partialTick, width, height);
        } finally {
            GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFbo);
            RenderSystem.viewport(viewport[0], viewport[1], viewport[2], viewport[3]);
        }
    }

    private static void drawScene(Minecraft mc, Reflection reflection, Direction facing, Vec3 planePoint, Vec3 normal, Vec3 cam, float partialTick, int width, int height) {
        ClientLevel level = mc.level;

        Matrix4f mirror = mirrorMatrix(facing.getAxis(), planePoint.subtract(cam));

        reflection.far.draw(mc, LAYER_SOLID, mirror, cam, GameRenderer.getRendertypeSolidShader(), true, false);
        reflection.near.draw(mc, LAYER_SOLID, mirror, cam, GameRenderer.getRendertypeCutoutMippedShader(), true, false);
        reflection.near.draw(mc, LAYER_CUTOUT, mirror, cam, GameRenderer.getRendertypeCutoutShader(), false, false);

        AABB area = new AABB(planePoint, planePoint).inflate(PlatformHelper.getMirrorReflectionRange());
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        boolean shadows = mc.options.entityShadows().get();
        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        buffers.endBatch();
        dispatcher.setRenderShadow(false);
        GL11.glFrontFace(GL11.GL_CW);
        try {
            for (Entity entity : level.getEntities((Entity) null, area, e -> !e.isInvisible() && !e.isSpectator())) {
                double x = Mth.lerp(partialTick, entity.xOld, entity.getX());
                double y = Mth.lerp(partialTick, entity.yOld, entity.getY());
                double z = Mth.lerp(partialTick, entity.zOld, entity.getZ());
                if ((x - planePoint.x) * normal.x + (y - planePoint.y) * normal.y + (z - planePoint.z) * normal.z <= 0) continue;

                PoseStack pose = REFLECTION_POSE;
                pose.pushPose();
                pose.mulPose(mirror);
                float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
                try {
                    dispatcher.render(entity, x - cam.x, y - cam.y, z - cam.z, yaw, partialTick, pose, buffers, dispatcher.getPackedLightCoords(entity, partialTick));
                } catch (Exception e) {
                    logFailure(entity.getType(), e);
                } finally {
                    pose.popPose();
                }
            }
            BlockEntityRenderDispatcher blockEntityDispatcher = mc.getBlockEntityRenderDispatcher();
            for (BlockPos pos : reflection.near.blockEntities) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity == null || blockEntity instanceof MirrorBlockEntity) continue;
                PoseStack pose = REFLECTION_POSE;
                pose.pushPose();
                pose.mulPose(mirror);
                pose.translate(pos.getX() - cam.x, pos.getY() - cam.y, pos.getZ() - cam.z);
                try {
                    blockEntityDispatcher.render(blockEntity, partialTick, pose, buffers);
                } catch (Exception e) {
                    logFailure(blockEntity.getType(), e);
                } finally {
                    pose.popPose();
                }
            }
            buffers.endBatch();
        } finally {
            GL11.glFrontFace(GL11.GL_CCW);
            dispatcher.setRenderShadow(shadows);
        }

        reflection.near.draw(mc, LAYER_TRANSLUCENT, mirror, cam, GameRenderer.getRendertypeTranslucentShader(), true, true);

        RenderSystem.colorMask(false, false, false, true);
        RenderSystem.clearColor(0f, 0f, 0f, 1f);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, Minecraft.ON_OSX);
        RenderSystem.colorMask(true, true, true, true);

        if (PlatformHelper.isMirrorBlurEnabled()) blur(reflection, width, height);
        else if (reflection.blurred != null) {
            reflection.blurred.destroyBuffers();
            reflection.blurred = null;
        }
    }

    private static void blur(Reflection reflection, int width, int height) {
        if (reflection.blurred == null) {
            reflection.blurred = new TextureTarget(width, height, false, Minecraft.ON_OSX);
            reflection.blurred.setFilterMode(GL11.GL_LINEAR);
        } else if (reflection.blurred.width != width || reflection.blurred.height != height) {
            reflection.blurred.resize(width, height, Minecraft.ON_OSX);
            reflection.blurred.setFilterMode(GL11.GL_LINEAR);
        }
        reflection.blurred.setClearColor(0f, 0f, 0f, 1f);
        reflection.blurred.clear(Minecraft.ON_OSX);
        reflection.blurred.bindWrite(true);

        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(new Matrix4f(), VertexSorting.ORTHOGRAPHIC_Z);
        Matrix4fStack modelView = RenderSystem.getModelViewStack();
        modelView.pushMatrix();
        modelView.identity();
        RenderSystem.applyModelViewMatrix();
        try {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, reflection.target.getColorTextureId());

            float du = BLUR_RADIUS / width, dv = BLUR_RADIUS / height;
            for (int n = 0; n < BLUR_TAPS.length; n++) {
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f / (n + 1));
                float ou = BLUR_TAPS[n][0] * du, ov = BLUR_TAPS[n][1] * dv;
                BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                builder.addVertex(-1f, -1f, 0f).setUv(ou, ov);
                builder.addVertex(1f, -1f, 0f).setUv(1f + ou, ov);
                builder.addVertex(1f, 1f, 0f).setUv(1f + ou, 1f + ov);
                builder.addVertex(-1f, 1f, 0f).setUv(ou, 1f + ov);
                BufferUploader.drawWithShader(builder.buildOrThrow());
            }

        } finally {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            modelView.popMatrix();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.restoreProjectionMatrix();
        }
    }

    private static int layerOf(RenderType type) {
        if (type == RenderType.cutout()) return LAYER_CUTOUT;
        if (type == RenderType.translucent() || type == RenderType.tripwire()) return LAYER_TRANSLUCENT;
        return LAYER_SOLID;
    }

    private static void renderFluid(BlockRenderDispatcher blockRenderer, ClientLevel level, BlockPos pos, BlockState state, FluidState fluid, BlockPos origin, VertexConsumer consumer) {
        float ox = (pos.getX() & ~15) - origin.getX();
        float oy = (pos.getY() & ~15) - origin.getY();
        float oz = (pos.getZ() & ~15) - origin.getZ();
        try {
            blockRenderer.renderLiquid(pos, level, new OffsetConsumer(consumer, ox, oy, oz), state, fluid);
        } catch (Exception e) {
            logFailure(fluid.getType(), e);
        }
    }

    private static void drawGlass(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation texture, BlockPos blockPos, Direction facing, VerticalConnectingType type) {
        float minY = switch (type) {
            case NONE, BOTTOM -> 3f / 16f;
            default -> -0.001f;
        };
        float maxY = switch (type) {
            case NONE, TOP -> 13f / 16f;
            default -> 1.001f;
        };

        Vec3 rel = Vec3.atLowerCornerOf(blockPos).subtract(FRAME_CAM);
        Matrix4f clip = CLIP_MATRIX.set(FRAME_CLIP).translate((float) rel.x, (float) rel.y, (float) rel.z);

        int count = 0;
        for (int i = 0; i < GRID; i++) {
            for (int j = 0; j < GRID; j++) {
                float x0 = Mth.lerp(i / (float) GRID, GLASS_MIN_X, GLASS_MAX_X);
                float x1 = Mth.lerp((i + 1) / (float) GRID, GLASS_MIN_X, GLASS_MAX_X);
                float y0 = Mth.lerp(j / (float) GRID, minY, maxY);
                float y1 = Mth.lerp((j + 1) / (float) GRID, minY, maxY);
                int start = count;
                boolean visible = true;
                for (int k = 0; k < 4; k++) {
                    rotate(facing, k == 1 || k == 2 ? x1 : x0, k >= 2 ? y1 : y0, GLASS_DEPTH, CORNER);
                    Vector4f c = clip.transform(CLIP.set(CORNER[0], CORNER[1], CORNER[2], 1f));
                    if (c.w <= 0.01f) {
                        visible = false;
                        break;
                    }
                    GLASS_VERTS[count++] = CORNER[0];
                    GLASS_VERTS[count++] = CORNER[1];
                    GLASS_VERTS[count++] = CORNER[2];
                    GLASS_VERTS[count++] = (c.x / c.w + 1f) * 0.5f;
                    GLASS_VERTS[count++] = (c.y / c.w + 1f) * 0.5f;
                }
                if (!visible) count = start;
            }
        }
        if (count == 0) return;

        PoseStack.Pose pose = poseStack.last();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
        float nx = facing.getStepX(), ny = facing.getStepY(), nz = facing.getStepZ();
        for (int v = 0; v < count; v += 5) {
            consumer.addVertex(pose, GLASS_VERTS[v], GLASS_VERTS[v + 1], GLASS_VERTS[v + 2])
                    .setColor(TINT_R, TINT_G, TINT_B, 1f)
                    .setUv(GLASS_VERTS[v + 3], GLASS_VERTS[v + 4])
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(LightTexture.FULL_BRIGHT)
                    .setNormal(pose, nx, ny, nz);
        }
    }

    private static void rotate(Direction facing, float x, float y, float z, float[] out) {
        switch (facing) {
            case SOUTH -> { out[0] = 1 - x; out[1] = y; out[2] = 1 - z; }
            case EAST -> { out[0] = 1 - z; out[1] = y; out[2] = x; }
            case WEST -> { out[0] = z; out[1] = y; out[2] = 1 - x; }
            default -> { out[0] = x; out[1] = y; out[2] = z; }
        }
    }

    private static class TargetTexture extends AbstractTexture {
        private final Reflection reflection;

        TargetTexture(Reflection reflection) {
            this.reflection = reflection;
        }

        @Override
        public int getId() {
            RenderTarget output = reflection.blurred != null ? reflection.blurred : reflection.target;
            return output.getColorTextureId();
        }

        @Override
        public void setFilter(boolean blur, boolean mipmap) {
        }

        @Override
        public void releaseId() {
        }

        @Override
        public void load(@NotNull ResourceManager resourceManager) {
        }
    }

    private static void logFailure(Object type, Exception e) {
        if (FAILED.add(type)) LOGGER.warn("Mirror reflection skipped {}", type, e);
    }

    private static void clearAll() {
        for (Reflection reflection : REFLECTIONS.values()) reflection.destroy();
        REFLECTIONS.clear();
        FAILED.clear();
    }

    private static void cleanup() {
        long now = System.nanoTime();
        Iterator<Reflection> it = REFLECTIONS.values().iterator();
        while (it.hasNext()) {
            Reflection r = it.next();
            if (now - r.lastUsed > 1_000_000_000L) {
                r.destroy();
                it.remove();
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull MirrorBlockEntity blockEntity) {
        return false;
    }

    private record Region(boolean coarse, int depthStart, int depthEnd, int side, int down, int up, int rebuildTicks, int slicesPerFrame) {
    }

    private static class RegionMesh {
        final Region region;
        final VertexBuffer[] buffers = new VertexBuffer[LAYERS];
        final boolean[] has = new boolean[LAYERS];
        final ByteBufferBuilder[] bytes = new ByteBufferBuilder[LAYERS];
        BufferBuilder[] builders;
        BlockPos origin;
        BlockPos buildOrigin;
        List<BlockPos> blockEntities = List.of();
        List<BlockPos> pendingBlockEntities;
        int slice;
        long lastBuild = Long.MIN_VALUE;

        RegionMesh(Region region) {
            this.region = region;
        }

        void step(Minecraft mc, ClientLevel level, BlockPos origin, Direction facing, Vec3 planePoint, Vec3 normal, long time) {
            if (builders != null && !origin.equals(buildOrigin)) abort();
            if (builders == null) {
                boolean due = this.origin == null || !origin.equals(this.origin) || time - lastBuild >= region.rebuildTicks() || time < lastBuild;
                if (!due) return;
                builders = new BufferBuilder[LAYERS];
                for (int i = 0; i < LAYERS; i++) {
                    if (bytes[i] == null) bytes[i] = new ByteBufferBuilder(1 << 18);
                    builders[i] = new BufferBuilder(bytes[i], VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
                }
                buildOrigin = origin.immutable();
                slice = region.depthStart();
                pendingBlockEntities = new ArrayList<>();
                lastBuild = time;
            }

            BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
            RandomSource random = RandomSource.create();
            Direction side = facing.getClockWise();
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            PoseStack poseStack = new PoseStack();

            for (int n = 0; n < region.slicesPerFrame() && slice < region.depthEnd(); n++, slice++) {
                for (int s = -region.side(); s <= region.side(); s++) {
                    for (int y = -region.down(); y <= region.up(); y++) {
                        pos.set(origin).move(facing, slice).move(side, s).move(Direction.UP, y);
                        if (Vec3.atCenterOf(pos).subtract(planePoint).dot(normal) <= 0) continue;
                        BlockState state = level.getBlockState(pos);
                        if (state.getBlock() instanceof MirrorBlock) continue;

                        FluidState fluid = state.getFluidState();
                        if (!fluid.isEmpty()) {
                            int layer = region.coarse() ? LAYER_SOLID : layerOf(ItemBlockRenderTypes.getRenderLayer(fluid));
                            renderFluid(blockRenderer, level, pos, state, fluid, origin, builders[layer]);
                        }

                        if (!region.coarse() && state.hasBlockEntity()) pendingBlockEntities.add(pos.immutable());
                        if (state.getRenderShape() != RenderShape.MODEL) continue;
                        int layer;
                        if (region.coarse()) {
                            if (!state.isSolidRender(level, pos) && !state.is(BlockTags.LEAVES)) continue;
                            layer = LAYER_SOLID;
                        } else {
                            layer = layerOf(ItemBlockRenderTypes.getChunkRenderType(state));
                        }

                        poseStack.pushPose();
                        poseStack.translate(pos.getX() - origin.getX(), pos.getY() - origin.getY(), pos.getZ() - origin.getZ());
                        try {
                            blockRenderer.renderBatched(state, pos, level, poseStack, builders[layer], true, random);
                        } catch (Exception e) {
                            logFailure(state.getBlock(), e);
                        } finally {
                            poseStack.popPose();
                        }
                    }
                }
            }

            if (slice >= region.depthEnd()) {
                BufferBuilder[] finished = builders;
                builders = null;
                int i = 0;
                try {
                    for (; i < LAYERS; i++) {
                        MeshData mesh = finished[i].build();
                        has[i] = mesh != null;
                        if (mesh == null) continue;
                        if (buffers[i] == null) buffers[i] = new VertexBuffer(VertexBuffer.Usage.STATIC);
                        buffers[i].bind();
                        buffers[i].upload(mesh);
                        VertexBuffer.unbind();
                    }
                } catch (RuntimeException e) {
                    VertexBuffer.unbind();
                    for (int j = i + 1; j < LAYERS; j++) {
                        MeshData rest = finished[j].build();
                        if (rest != null) rest.close();
                    }
                    Arrays.fill(has, false);
                    this.origin = null;
                    throw e;
                }
                this.origin = buildOrigin;
                blockEntities = pendingBlockEntities;
            }
        }

        void draw(Minecraft mc, int layer, Matrix4f mirror, Vec3 cam, ShaderInstance shader, boolean mipmap, boolean blend) {
            if (origin == null || !has[layer] || buffers[layer] == null || shader == null) return;
            Vec3 rel = Vec3.atLowerCornerOf(origin).subtract(cam);
            Matrix4f modelView = new Matrix4f(RenderSystem.getModelViewMatrix()).mul(mirror)
                    .translate((float) rel.x, (float) rel.y, (float) rel.z);

            AbstractTexture atlas = mc.getTextureManager().getTexture(InventoryMenu.BLOCK_ATLAS);
            atlas.setFilter(false, mipmap);
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            GL11.glFrontFace(GL11.GL_CW);
            if (blend) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
            } else {
                RenderSystem.disableBlend();
            }
            mc.gameRenderer.lightTexture().turnOnLightLayer();
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

            if (shader.CHUNK_OFFSET != null) shader.CHUNK_OFFSET.set(0f, 0f, 0f);
            try {
                buffers[layer].bind();
                buffers[layer].drawWithShader(modelView, RenderSystem.getProjectionMatrix(), shader);
            } finally {
                VertexBuffer.unbind();
                GL11.glFrontFace(GL11.GL_CCW);
                mc.gameRenderer.lightTexture().turnOffLightLayer();
                RenderSystem.disableBlend();
                atlas.setFilter(false, true);
            }
        }

        void abort() {
            if (builders == null) return;
            for (BufferBuilder b : builders) {
                MeshData mesh = b.build();
                if (mesh != null) mesh.close();
            }
            builders = null;
        }

        void destroy() {
            abort();
            for (VertexBuffer b : buffers) if (b != null) b.close();
            for (ByteBufferBuilder b : bytes) if (b != null) b.close();
        }
    }

    private record OffsetConsumer(VertexConsumer delegate, float ox, float oy, float oz) implements VertexConsumer {
        @Override
        public @NotNull VertexConsumer addVertex(float x, float y, float z) {
            delegate.addVertex(x + ox, y + oy, z + oz);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setColor(int r, int g, int b, int a) {
            delegate.setColor(r, g, b, a);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv(float u, float v) {
            delegate.setUv(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv1(int u, int v) {
            delegate.setUv1(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setUv2(int u, int v) {
            delegate.setUv2(u, v);
            return this;
        }

        @Override
        public @NotNull VertexConsumer setNormal(float x, float y, float z) {
            delegate.setNormal(x, y, z);
            return this;
        }
    }

    private static class Reflection {
        final RegionMesh near = new RegionMesh(NEAR);
        final RegionMesh far = new RegionMesh(FAR);
        RenderTarget target;
        RenderTarget blurred;
        ResourceLocation texture;
        Direction facing;
        Vec3 planePoint;
        Vec3 normal;
        boolean requested;
        boolean ready;
        BlockPos anchor;
        long lastUsed;
        float partialTick = Float.NaN;
        double camX, camY, camZ;
        float xRot, yRot;
        long gameTime;

        boolean update(float partialTick, Vec3 cam, float xRot, float yRot, long gameTime) {
            if (partialTick == this.partialTick && cam.x == camX && cam.y == camY && cam.z == camZ && xRot == this.xRot && yRot == this.yRot && gameTime == this.gameTime) {
                return false;
            }
            this.partialTick = partialTick;
            this.camX = cam.x;
            this.camY = cam.y;
            this.camZ = cam.z;
            this.xRot = xRot;
            this.yRot = yRot;
            this.gameTime = gameTime;
            return true;
        }

        void destroy() {
            if (texture != null) Minecraft.getInstance().getTextureManager().release(texture);
            if (target != null) target.destroyBuffers();
            if (blurred != null) blurred.destroyBuffers();
            near.destroy();
            far.destroy();
        }
    }
}
