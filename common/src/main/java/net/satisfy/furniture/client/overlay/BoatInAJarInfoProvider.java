package net.satisfy.furniture.client.overlay;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.block.BoatInAJarBlock;
import net.satisfy.furniture.core.block.entity.BoatInAJarBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import net.satisfy.foundation.tooltip.TooltipBorder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BoatInAJarInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (!PlatformHelper.showBoatInAJarInfo()) {
            return List.of();
        }
        if (!(state.getBlock() instanceof BoatInAJarBlock)) {
            return List.of();
        }
        if (state.getValue(BoatInAJarBlock.BROKEN)) {
            return List.of(
                    InfoSection.title(Component.translatable("hud.furniture.boat_in_a_jar.broken").withStyle(ChatFormatting.RED)),
                    InfoSection.icons(Component.translatable("hud.furniture.boat_in_a_jar.repair_title"), List.of(new ItemStack(Items.STICK)), InfoSection.GRID_COLUMNS),
                    InfoSection.title(Component.translatable("hud.furniture.boat_in_a_jar.repair").withStyle(ChatFormatting.GRAY)));
        }
        InfoSection hint = InfoSection.title(Component.translatable("hud.furniture.boat_in_a_jar.hint").withStyle(ChatFormatting.GRAY));
        if (isRough(level, pos)) {
            return List.of(InfoSection.title(Component.translatable("hud.furniture.boat_in_a_jar.careful").withStyle(ChatFormatting.GOLD)), hint);
        }
        return List.of(hint);
    }

    @Override
    public void beforeBackground(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof BoatInAJarBlock)) {
            return;
        }
        if (state.getValue(BoatInAJarBlock.BROKEN)) {
            TooltipBorder.mark(0xF08C2414, 0xF0401008);
        } else if (isRough(level, pos)) {
            TooltipBorder.mark(0xF0FFD700, 0xF0B8860B);
        }
    }

    private static boolean isRough(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof BoatInAJarBlockEntity be && be.getEnergy() >= BoatInAJarBlockEntity.warnEnergy();
    }
}
