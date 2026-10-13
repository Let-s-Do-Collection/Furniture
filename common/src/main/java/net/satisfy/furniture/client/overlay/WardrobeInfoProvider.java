package net.satisfy.furniture.client.overlay;

import net.satisfy.furniture.platform.PlatformHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.block.WardrobeBlock;
import net.satisfy.foundation.block.WardrobeBlockEntity;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WardrobeInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (!PlatformHelper.showWardrobeInfo()) {
            return List.of();
        }
        if (!(state.getBlock() instanceof WardrobeBlock)) {
            return List.of();
        }
        BlockPos basePos = state.getValue(WardrobeBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        if (!(level.getBlockEntity(basePos) instanceof WardrobeBlockEntity wardrobe)) {
            return List.of();
        }
        List<InfoSection.Row> rows = new ArrayList<>();
        for (ItemStack stored : wardrobe.getInventory()) {
            if (!stored.isEmpty()) {
                rows.add(InfoSection.Row.item(stored, stored.getHoverName()));
            }
        }
        List<InfoSection> sections = new ArrayList<>();
        if (!rows.isEmpty()) {
            sections.add(InfoSection.rows(Component.translatable("hud.furniture.wardrobe.in_wardrobe"), rows));
        }
        sections.add(InfoSection.title(Component.translatable("hud.furniture.wardrobe.hint").withStyle(ChatFormatting.GRAY)));
        return sections;
    }
}
