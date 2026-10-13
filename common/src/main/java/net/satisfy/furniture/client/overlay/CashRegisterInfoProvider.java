package net.satisfy.furniture.client.overlay;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.block.CashRegisterBlock;
import net.satisfy.furniture.core.block.entity.CashRegisterBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CashRegisterInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (!PlatformHelper.showCashRegisterInfo()) {
            return List.of();
        }
        if (!(state.getBlock() instanceof CashRegisterBlock)) {
            return List.of();
        }
        List<ItemStack> contents = new ArrayList<>();
        boolean open = false;
        if (level.getBlockEntity(pos) instanceof CashRegisterBlockEntity be) {
            open = be.isOpen();
            for (ItemStack stack : be.getItems()) {
                if (stack.isEmpty()) {
                    continue;
                }
                contents.stream().filter(existing -> ItemStack.isSameItemSameComponents(existing, stack)).findFirst()
                        .ifPresentOrElse(existing -> existing.grow(stack.getCount()), () -> contents.add(stack.copy()));
            }
        }
        List<InfoSection> sections = new ArrayList<>();
        if (!contents.isEmpty()) {
            sections.add(InfoSection.icons(Component.translatable("hud.furniture.cash_register.in_drawer"), contents, InfoSection.ROW_COLUMNS).withDecorations());
        }
        List<String> hints = open ? List.of("hint_open", "hint_take", "hint_close") : List.of("hint", "hint_drawer");
        for (String hint : hints) {
            sections.add(InfoSection.title(Component.translatable("hud.furniture.cash_register." + hint).withStyle(ChatFormatting.GRAY)));
        }
        return sections;
    }
}
