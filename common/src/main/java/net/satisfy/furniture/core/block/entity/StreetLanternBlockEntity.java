package net.satisfy.furniture.core.block.entity;

import net.satisfy.furniture.core.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class StreetLanternBlockEntity extends BlockEntity implements Clearable {
    private final NonNullList<ItemStack> flowers = NonNullList.withSize(2, ItemStack.EMPTY);

    public StreetLanternBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(EntityTypeRegistry.STREET_LANTERN_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public ItemStack getFlower(int slot) {
        return this.flowers.get(slot);
    }

    public void setFlower(int slot, ItemStack stack) {
        this.flowers.set(slot, stack);
        this.markUpdated();
    }

    public ItemStack removeFlower(int slot) {
        ItemStack stack = this.flowers.get(slot);
        this.flowers.set(slot, ItemStack.EMPTY);
        this.markUpdated();
        return stack;
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        this.flowers.clear();
        ContainerHelper.loadAllItems(compoundTag, this.flowers, provider);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        ContainerHelper.saveAllItems(compoundTag, this.flowers, true, provider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        ContainerHelper.saveAllItems(compoundTag, this.flowers, true, provider);
        return compoundTag;
    }

    private void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public void clearContent() {
        this.flowers.clear();
    }
}
