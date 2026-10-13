package net.satisfy.furniture.core.block.entity;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.block.CashRegisterBlock;
import net.satisfy.furniture.core.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CashRegisterBlockEntity extends BlockEntity {
    public static final int SLOTS = 3;
    public static final int CLOSE_ANIMATION_TICKS = 6;

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private boolean open;
    private int idleTicks;
    private int closingTicks;

    public float renderProgress;
    public long lastRenderNanos;

    public CashRegisterBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.CASH_REGISTER_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean isOpen() {
        return open;
    }

    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        touch();
        sync();
    }

    public void setOpen(boolean open) {
        if (level == null || level.isClientSide || this.open == open) {
            return;
        }
        this.open = open;
        idleTicks = 0;
        if (open) {
            closingTicks = 0;
            setOpenState(true);
        } else {
            closingTicks = CLOSE_ANIMATION_TICKS;
        }
        sync();
    }

    public void touch() {
        idleTicks = 0;
    }

    public int getAnalogSignal() {
        int total = 0;
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                total += stack.getCount() * 64 / stack.getMaxStackSize();
            }
        }
        return total == 0 ? 0 : 1 + total * 14 / (SLOTS * 64);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CashRegisterBlockEntity be) {
        if (be.open) {
            if (!state.getValue(CashRegisterBlock.POWERED) && ++be.idleTicks >= PlatformHelper.getCashRegisterAutoCloseSeconds() * 20) {
                CashRegisterBlock.close(level, pos, be);
            }
        } else if (be.closingTicks > 0 && --be.closingTicks == 0) {
            be.setOpenState(false);
        } else if (be.closingTicks == 0 && state.getValue(CashRegisterBlock.OPEN)) {
            be.setOpenState(false);
        }
    }

    private void setOpenState(boolean value) {
        if (level == null) {
            return;
        }
        BlockState state = level.getBlockState(worldPosition);
        if (state.getBlock() instanceof CashRegisterBlock && state.getValue(CashRegisterBlock.OPEN) != value) {
            level.setBlock(worldPosition, state.setValue(CashRegisterBlock.OPEN, value), Block.UPDATE_ALL);
        }
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
            level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        ContainerHelper.saveAllItems(tag, items, true, provider);
        tag.putBoolean("Open", open);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        items.clear();
        ContainerHelper.loadAllItems(tag, items, provider);
        open = tag.getBoolean("Open");
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }
}
