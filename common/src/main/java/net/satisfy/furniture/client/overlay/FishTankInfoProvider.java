package net.satisfy.furniture.client.overlay;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.block.FishTankBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FishTankInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (!PlatformHelper.showFishTankInfo()) {
            return List.of();
        }
        if (!(state.getBlock() instanceof FishTankBlock)) {
            return List.of();
        }
        List<ItemStack> missing = new ArrayList<>();
        if (!state.getValue(FishTankBlock.HAS_COD)) missing.add(new ItemStack(Items.COD_BUCKET));
        if (!state.getValue(FishTankBlock.HAS_SALMON)) missing.add(new ItemStack(Items.SALMON_BUCKET));
        if (!state.getValue(FishTankBlock.HAS_PUFFERFISH)) missing.add(new ItemStack(Items.PUFFERFISH_BUCKET));
        if (missing.isEmpty()) {
            return List.of();
        }
        return List.of(
                InfoSection.icons(Component.translatable("hud.furniture.fish_tank.add_title"), missing, InfoSection.GRID_COLUMNS),
                InfoSection.title(Component.translatable("hud.furniture.fish_tank.hint").withStyle(ChatFormatting.GRAY)));
    }
}
