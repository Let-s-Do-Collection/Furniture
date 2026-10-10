package net.satisfy.furniture.client.overlay;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.block.TelescopeBlock;
import net.satisfy.furniture.core.network.TelescopeNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.BlockInfoSync;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TelescopeInfoProvider implements BlockInfoProvider {
    private static final String[] MOON_PHASES = {"full", "waning_gibbous", "last_quarter", "waning_crescent", "new", "waxing_crescent", "first_quarter", "waxing_gibbous"};

    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (!PlatformHelper.showTelescopeInfo()) {
            return List.of();
        }
        if (!(state.getBlock() instanceof TelescopeBlock)) {
            return List.of();
        }
        Component moon = Component.translatable("hud.furniture.telescope.moon", Component.translatable("hud.furniture.telescope.moon." + MOON_PHASES[level.getMoonPhase()]));
        Component forecast = forecast(level, BlockInfoSync.poll(level, pos));
        return List.of(
                forecast != null ? InfoSection.lines(moon, List.of(forecast)) : InfoSection.title(moon),
                InfoSection.title(Component.translatable("hud.furniture.telescope.hint").withStyle(ChatFormatting.GRAY)));
    }

    @Nullable
    private static Component forecast(Level level, CompoundTag data) {
        if (!data.contains(TelescopeNetworking.RAIN_TIME)) {
            return null;
        }
        int clear = data.getInt(TelescopeNetworking.CLEAR_TIME);
        int rain = data.getInt(TelescopeNetworking.RAIN_TIME);
        int thunder = data.getInt(TelescopeNetworking.THUNDER_TIME);
        if (clear > 0) {
            return Component.translatable("hud.furniture.telescope.clear_for", duration(clear)).withStyle(ChatFormatting.GRAY);
        }
        if (level.isThundering()) {
            return Component.translatable("hud.furniture.telescope.storm_ends", duration(Math.min(rain, thunder))).withStyle(ChatFormatting.GOLD);
        }
        if (level.isRaining()) {
            return Component.translatable("hud.furniture.telescope.rain_ends", duration(rain)).withStyle(ChatFormatting.GRAY);
        }
        if (thunder <= rain) {
            return Component.translatable("hud.furniture.telescope.storm_in", duration(rain)).withStyle(ChatFormatting.GOLD);
        }
        return Component.translatable("hud.furniture.telescope.rain_in", duration(rain)).withStyle(ChatFormatting.GRAY);
    }

    private static Component duration(int ticks) {
        int minutes = ticks / 1200;
        return minutes < 1 ? Component.translatable("hud.furniture.telescope.soon") : Component.translatable("hud.furniture.telescope.minutes", minutes);
    }
}
