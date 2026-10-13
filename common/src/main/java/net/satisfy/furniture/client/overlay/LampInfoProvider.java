package net.satisfy.furniture.client.overlay;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.block.LampBlock;
import net.satisfy.furniture.core.block.LampWallBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.block.VerticalConnectingType;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LampInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (!PlatformHelper.showLampInfo()) {
            return List.of();
        }
        boolean switchable = state.getBlock() instanceof LampWallBlock
                || state.getBlock() instanceof LampBlock && (state.getValue(LampBlock.TYPE) == VerticalConnectingType.NONE || state.getValue(LampBlock.TYPE) == VerticalConnectingType.TOP);
        if (!switchable) {
            return List.of();
        }
        boolean lit = state.getValue(AbstractCandleBlock.LIT);
        return List.of(
                InfoSection.title(lit ? Component.translatable("hud.furniture.lamp.status_on").withStyle(ChatFormatting.GOLD) : Component.translatable("hud.furniture.lamp.status_off").withStyle(ChatFormatting.GRAY)),
                InfoSection.title(Component.translatable(lit ? "hud.furniture.lamp.hint_off" : "hud.furniture.lamp.hint_on").withStyle(ChatFormatting.GRAY)));
    }
}
