package net.satisfy.furniture.client.handler;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.client.render.block.TelescopeRenderer;
import net.satisfy.furniture.core.block.TelescopeBlock;
import net.satisfy.furniture.core.network.TelescopeNetworking;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.satisfy.foundation.overlay.BlockInfoOverlay;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class TelescopeViewHandler {
    private static final float DEFAULT_ZOOM = 0.1F;
    private static final float ZOOM_STEP = 0.8F;
    private static final ResourceLocation SCOPE = ResourceLocation.withDefaultNamespace("textures/misc/spyglass_scope.png");
    private static final float MAX_YAW_OFFSET = 45.0F;
    private static final float MIN_PITCH = -85.0F;
    private static final float MAX_PITCH = 15.0F;
    private static final double SKY_DISTANCE = 90.0;

    @Nullable
    private static BlockPos active;
    private static Direction facing = Direction.NORTH;
    private static boolean wasShifting;
    private static float startYaw;
    private static float startPitch;
    private static float targetZoom = DEFAULT_ZOOM;
    private static float zoom = DEFAULT_ZOOM;
    private static float zoomO = DEFAULT_ZOOM;

    private TelescopeViewHandler() {
    }

    public static void init() {
        ClientTickEvent.CLIENT_POST.register(TelescopeViewHandler::tick);
        ClientGuiEvent.RENDER_HUD.register(TelescopeViewHandler::renderScope);
        ClientRawInputEvent.MOUSE_SCROLLED.register(TelescopeViewHandler::onScroll);
    }

    public static boolean isActive() {
        return active != null;
    }

    public static float getZoom(float partialTick) {
        return Mth.lerp(partialTick, zoomO, zoom);
    }

    private static EventResult onScroll(Minecraft minecraft, double amountX, double amountY) {
        if (active == null || minecraft.screen != null || amountY == 0.0) {
            return EventResult.pass();
        }
        float previous = targetZoom;
        targetZoom = Mth.clamp(amountY > 0.0 ? targetZoom * ZOOM_STEP : targetZoom / ZOOM_STEP, minZoom(), maxZoom());
        if (targetZoom != previous && minecraft.player != null) {
            float progress = (maxZoom() - targetZoom) / Math.max(0.0001F, maxZoom() - minZoom());
            minecraft.player.playSound(SoundEvents.SPYGLASS_USE, 0.3F, 0.8F + progress * 0.8F);
        }
        return EventResult.interruptFalse();
    }

    public static void toggle(BlockPos pos, BlockState state) {
        BlockPos base = state.getValue(TelescopeBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        if (base.equals(active)) {
            stop();
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        active = base.immutable();
        facing = state.getValue(TelescopeBlock.FACING).getOpposite();
        wasShifting = player.isShiftKeyDown();
        startYaw = player.getYRot();
        startPitch = player.getXRot();
        targetZoom = Mth.clamp(DEFAULT_ZOOM, minZoom(), maxZoom());
        zoom = 1.0F;
        zoomO = 1.0F;
        player.setYRot(facing.toYRot());
        player.setXRot(-TelescopeRenderer.REST_PITCH);
        player.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 0.8F);
        NetworkManager.sendToServer(new TelescopeNetworking.UsePacket(active, true));
    }

    public static void stop() {
        if (active != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 0.8F);
                player.setYRot(startYaw);
                player.setXRot(startPitch);
                player.yRotO = startYaw;
                player.xRotO = startPitch;
                player.setYHeadRot(startYaw);
                player.yHeadRotO = startYaw;
            }
            if (Minecraft.getInstance().getConnection() != null) {
                NetworkManager.sendToServer(new TelescopeNetworking.UsePacket(active, false));
            }
            active = null;
        }
    }

    private static void tick(Minecraft minecraft) {
        if (active == null) {
            return;
        }
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        if (player == null || level == null || minecraft.screen != null
                || !(level.getBlockState(active).getBlock() instanceof TelescopeBlock)
                || player.position().distanceToSqr(Vec3.atCenterOf(active)) > Mth.square((double) PlatformHelper.getTelescopeMaxDistance())) {
            stop();
            return;
        }
        boolean shifting = player.isShiftKeyDown();
        if (shifting && !wasShifting) {
            stop();
            return;
        }
        wasShifting = shifting;
        zoomO = zoom;
        zoom += (targetZoom - zoom) * 0.5F;
        float yawOffset = Mth.clamp(Mth.wrapDegrees(player.getYRot() - facing.toYRot()), -MAX_YAW_OFFSET, MAX_YAW_OFFSET);
        player.setYRot(facing.toYRot() + yawOffset);
        player.setXRot(Mth.clamp(player.getXRot(), MIN_PITCH, MAX_PITCH));
        if (PlatformHelper.areShootingStarsEnabled() && level.isNight() && !level.isRaining() && player.getXRot() < -10.0F && level.random.nextInt(PlatformHelper.getShootingStarChance()) == 0) {
            spawnShootingStar(level, player);
        }
    }

    private static void spawnShootingStar(ClientLevel level, LocalPlayer player) {
        RandomSource random = level.random;
        Vec3 look = player.getLookAngle();
        Vec3 side = look.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        Vec3 start = player.getEyePosition()
                .add(look.scale(SKY_DISTANCE))
                .add(side.scale((random.nextDouble() - 0.5) * 30.0))
                .add(0.0, 10.0 + random.nextDouble() * 10.0, 0.0);
        Vec3 direction = side.scale(random.nextBoolean() ? 1.0 : -1.0).add(0.0, -0.35, 0.0).normalize();
        for (int i = 0; i < 24; i++) {
            Vec3 point = start.add(direction.scale(i * 0.9));
            Vec3 velocity = direction.scale(1.6);
            level.addParticle(i % 3 == 0 ? ParticleTypes.FIREWORK : ParticleTypes.END_ROD, true, point.x, point.y, point.z, velocity.x, velocity.y, velocity.z);
        }
        BlockInfoOverlay.showNotice(active, Component.translatable("hud.furniture.telescope.shooting_star"));
        NetworkManager.sendToServer(new TelescopeNetworking.ShootingStarPacket(active));
    }

    private static void renderScope(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (active == null || !Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            return;
        }
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        int size = Math.min(width, height);
        int x = (width - size) / 2;
        int y = (height - size) / 2;
        graphics.blit(SCOPE, x, y, -90, 0.0F, 0.0F, size, size, size, size);
        graphics.fill(0, 0, width, y, -90, 0xFF000000);
        graphics.fill(0, y + size, width, height, -90, 0xFF000000);
        graphics.fill(0, y, x, y + size, -90, 0xFF000000);
        graphics.fill(x + size, y, width, y + size, -90, 0xFF000000);
    }

    private static float minZoom() {
        return (float) (1.0 / PlatformHelper.getTelescopeMaxMagnification());
    }

    private static float maxZoom() {
        return Math.max(minZoom(), (float) (1.0 / PlatformHelper.getTelescopeMinMagnification()));
    }
}
