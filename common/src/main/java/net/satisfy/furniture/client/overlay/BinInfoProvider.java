package net.satisfy.furniture.client.overlay;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.block.BinBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BinInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (!PlatformHelper.showBinInfo()) {
            return List.of();
        }
        if (!(state.getBlock() instanceof BinBlock)) {
            return List.of();
        }
        Component decorative = Component.translatable(state.getValue(BinBlock.DECORATIVE) ? "hud.furniture.bin.decorative_hint_off" : "hud.furniture.bin.decorative_hint_on").withStyle(ChatFormatting.GRAY);
        if (state.getValue(BinBlock.DECORATIVE)) {
            return List.of(InfoSection.title(decorative));
        }
        int stage = state.getValue(BinBlock.FILL_STAGE);
        Component fill = Component.translatable("hud.furniture.bin.fill", stage, BinBlock.MAX_FILL_STAGE)
                .withStyle(stage >= BinBlock.MAX_FILL_STAGE ? ChatFormatting.GOLD : ChatFormatting.GRAY);
        Component hint = Component.translatable(stage >= BinBlock.MAX_FILL_STAGE ? "hud.furniture.bin.full" : "hud.furniture.bin.hint").withStyle(ChatFormatting.GRAY);
        return List.of(InfoSection.title(fill), InfoSection.title(hint), InfoSection.title(decorative));
    }
}
