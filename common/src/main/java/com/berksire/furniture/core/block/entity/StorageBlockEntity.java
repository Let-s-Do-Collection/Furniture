package com.berksire.furniture.core.block.entity;

import com.berksire.furniture.core.registry.EntityTypeRegistry;
import com.berksire.furniture.core.util.FurnitureUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class StorageBlockEntity extends BlockEntity {
    private int size;
    private NonNullList<ItemStack> inventory;

    public StorageBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.STORAGE_BLOCK_ENTITY.get(), pos, state);
        this.size = 2;
        this.inventory = NonNullList.withSize(this.size, ItemStack.EMPTY);
    }

    public StorageBlockEntity(BlockPos pos, BlockState state, int size) {
        super(EntityTypeRegistry.STORAGE_BLOCK_ENTITY.get(), pos, state);
        this.size = size;
        this.inventory = NonNullList.withSize(this.size, ItemStack.EMPTY);
    }

    public ItemStack removeStack(int slot) {
        if (slot >= 0 && slot < inventory.size()) {
            ItemStack stack = inventory.set(slot, ItemStack.EMPTY);
            setChanged();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public void setStack(int slot, ItemStack stack) {
        if (slot >= 0 && slot < inventory.size()) {
            inventory.set(slot, stack);
            setChanged();
        }
    }

    @Override
    public void setChanged() {
        if (level != null && !level.isClientSide()) {
            Packet<ClientGamePacketListener> updatePacket = getUpdatePacket();
            for (ServerPlayer player : FurnitureUtil.tracking((ServerLevel) level, getBlockPos())) {
                if (updatePacket != null) {
                    player.connection.send(updatePacket);
                }
            }
        }
        super.setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        this.size = compoundTag.getInt("size");
        this.inventory = NonNullList.withSize(this.size, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, this.inventory, provider);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        ContainerHelper.saveAllItems(nbt, this.inventory, provider);
        nbt.putInt("size", this.size);
        super.saveAdditional(nbt, provider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }
}
