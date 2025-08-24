package com.berksire.furniture.core.block.entity;

import com.berksire.furniture.core.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FishTankBlockEntity extends BlockEntity implements BlockEntityTicker<FishTankBlockEntity> {
    private boolean hasCod;
    private boolean hasPufferfish;
    private boolean hasSalmon;

    public AnimationState idleAnimationState;
    private int idleAnimationTimeout = 0;

    public FishTankBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.FISH_TANK_BLOCK_ENTITY.get(), pos, state);
        idleAnimationState = new AnimationState();
    }

    public boolean hasCod() {
        return hasCod;
    }

    public void setHasCod(boolean hasCod) {
        this.hasCod = hasCod;
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public boolean hasPufferfish() {
        return hasPufferfish;
    }

    public void setHasPufferfish(boolean hasPufferfish) {
        this.hasPufferfish = hasPufferfish;
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public boolean hasSalmon() {
        return hasSalmon;
    }

    public void setHasSalmon(boolean hasSalmon) {
        this.hasSalmon = hasSalmon;
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        hasCod = tag.getBoolean("HasCod");
        hasPufferfish = tag.getBoolean("HasPufferfish");
        hasSalmon = tag.getBoolean("HasSalmon");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("HasCod", hasCod);
        tag.putBoolean("HasPufferfish", hasPufferfish);
        tag.putBoolean("HasSalmon", hasSalmon);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state, FishTankBlockEntity be) {
        if (level.isClientSide) updateAnimations();
    }

    public void updateAnimations() {
        if (idleAnimationTimeout <= 0) {
            idleAnimationTimeout = 123;
            idleAnimationState.start(0);
        } else {
            idleAnimationTimeout--;
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }
}
