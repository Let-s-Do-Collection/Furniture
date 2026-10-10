package net.satisfy.furniture.core.network;

import net.minecraft.util.Mth;
import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.Furniture;
import net.satisfy.furniture.core.block.TelescopeBlock;
import net.satisfy.furniture.core.block.entity.TelescopeBlockEntity;
import net.minecraft.network.codec.ByteBufCodecs;
import net.satisfy.furniture.core.registry.TriggerRegistry;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ServerLevelData;
import net.satisfy.foundation.overlay.BlockInfoSync;
import org.jetbrains.annotations.NotNull;

public final class TelescopeNetworking {
    public static final String CLEAR_TIME = "furniture_clear_time";
    public static final String RAIN_TIME = "furniture_rain_time";
    public static final String THUNDER_TIME = "furniture_thunder_time";

    private TelescopeNetworking() {
    }

    public record ShootingStarPacket(BlockPos pos) implements CustomPacketPayload {
        public static final Type<ShootingStarPacket> TYPE = new Type<>(Furniture.identifier("shooting_star"));
        public static final StreamCodec<RegistryFriendlyByteBuf, ShootingStarPacket> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, ShootingStarPacket::pos, ShootingStarPacket::new);

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record UsePacket(BlockPos pos, boolean using) implements CustomPacketPayload {
        public static final Type<UsePacket> TYPE = new Type<>(Furniture.identifier("telescope_use"));
        public static final StreamCodec<RegistryFriendlyByteBuf, UsePacket> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, UsePacket::pos, ByteBufCodecs.BOOL, UsePacket::using, UsePacket::new);

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), UsePacket.TYPE, UsePacket.CODEC, (packet, context) -> context.queue(() -> {
            if (!(context.getPlayer() instanceof ServerPlayer player) || player.blockPosition().distSqr(packet.pos()) > maxDistanceSqr()) {
                return;
            }
            TelescopeBlockEntity be = TelescopeBlock.getOrCreateBlockEntity(player.level(), packet.pos());
            if (be == null) {
                return;
            }
            if (packet.using()) {
                be.setUser(player.getUUID());
                if (player.level().isNight()) {
                    TriggerRegistry.STARGAZER.get().trigger(player);
                }
            } else if (player.getUUID().equals(be.getUser())) {
                be.setUser(null);
            }
        }));
        NetworkManager.registerReceiver(NetworkManager.c2s(), ShootingStarPacket.TYPE, ShootingStarPacket.CODEC, (packet, context) -> context.queue(() -> {
            if (context.getPlayer() instanceof ServerPlayer player
                    && player.blockPosition().distSqr(packet.pos()) <= maxDistanceSqr()
                    && player.level().getBlockState(packet.pos()).getBlock() instanceof TelescopeBlock
                    && player.level().isNight()) {
                TriggerRegistry.SHOOTING_STAR.get().trigger(player);
            }
        }));
        BlockInfoSync.registerHandler(TelescopeNetworking::writeWeather);
    }

    private static void writeWeather(ServerLevel level, BlockPos pos, ServerPlayer player, CompoundTag tag) {
        if (!(level.getBlockState(pos).getBlock() instanceof TelescopeBlock) || !(level.getLevelData() instanceof ServerLevelData data)) {
            return;
        }
        tag.putInt(CLEAR_TIME, data.getClearWeatherTime());
        tag.putInt(RAIN_TIME, data.getRainTime());
        tag.putInt(THUNDER_TIME, data.getThunderTime());
    }

    private static double maxDistanceSqr() {
        return Mth.square(PlatformHelper.getTelescopeMaxDistance() + 5.0);
    }
}
