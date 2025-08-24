package com.berksire.furniture.core.block.entity;

import com.berksire.furniture.core.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DisplayBlockEntity extends BlockEntity implements Clearable {
    private ItemStack displayedItem = ItemStack.EMPTY;

    public DisplayBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(EntityTypeRegistry.DISPLAY_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public ItemStack getDisplayedItem() {
        return this.displayedItem;
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        if (compoundTag.contains("DisplayedItem", 10)) {
            this.displayedItem = ItemStack.parse(provider, compoundTag.getCompound("DisplayedItem")).orElse(ItemStack.EMPTY);
        } else {
            this.displayedItem = ItemStack.EMPTY;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        if (!this.displayedItem.isEmpty()) {
            compoundTag.put("DisplayedItem", this.displayedItem.save(provider));
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        if (!this.displayedItem.isEmpty()) {
            compoundTag.put("DisplayedItem", this.displayedItem.save(provider));
        }
        return compoundTag;
    }

    public boolean setDisplayedItem(ItemStack stack) {
        if (!this.displayedItem.isEmpty()) return false;

        this.displayedItem = stack;
        this.markUpdated();
        return true;
    }

    public void removeDisplayedItem(int count) {
        if (!this.displayedItem.isEmpty()) {
            this.displayedItem.shrink(count);
            if (this.displayedItem.isEmpty()) {
                this.displayedItem = ItemStack.EMPTY;
            }
            this.markUpdated();
        }
    }

    public void dropContents() {
        if (!this.displayedItem.isEmpty()) {
            assert this.level != null;
            ItemEntity itemEntity = new ItemEntity(this.level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), this.displayedItem);
            this.level.addFreshEntity(itemEntity);
            this.displayedItem = ItemStack.EMPTY;
        }
        this.markUpdated();
    }

    private void markUpdated() {
        this.setChanged();
        Objects.requireNonNull(this.getLevel()).sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public void clearContent() {
        this.displayedItem = ItemStack.EMPTY;
    }
}