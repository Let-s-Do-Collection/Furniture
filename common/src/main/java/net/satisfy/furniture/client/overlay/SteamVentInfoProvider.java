package net.satisfy.furniture.client.overlay;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.block.SteamVentBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SteamVentInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (!PlatformHelper.showSteamVentInfo()) {
            return List.of();
        }
        if (!(state.getBlock() instanceof SteamVentBlock)) {
            return List.of();
        }
        boolean forced = state.getValue(SteamVentBlock.PARTICLES_ENABLED);
        boolean extracting = SteamVentBlock.findSteamSource(level, pos) != null;
        Component status;
        if (extracting) {
            status = Component.translatable("hud.furniture.steam_vent.extracting").withStyle(ChatFormatting.GOLD);
        } else if (forced) {
            status = Component.translatable("hud.furniture.steam_vent.forced").withStyle(ChatFormatting.GOLD);
        } else {
            status = Component.translatable("hud.furniture.steam_vent.idle").withStyle(ChatFormatting.GRAY);
        }
        List<InfoSection> sections = new ArrayList<>();
        sections.add(SteamVentBlock.findChimney(level, pos) != null
                ? InfoSection.lines(status, List.of(Component.translatable("hud.furniture.steam_vent.chimney").withStyle(ChatFormatting.GRAY)))
                : InfoSection.title(status));
        sections.add(InfoSection.title(Component.translatable("hud.furniture.steam_vent.hint").withStyle(ChatFormatting.GRAY)));
        return sections;
    }
}
