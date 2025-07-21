package com.berksire.furniture.block.entity;

import com.berksire.furniture.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

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
    }

    public boolean hasPufferfish() {
        return hasPufferfish;
    }

    public void setHasPufferfish(boolean hasPufferfish) {
        this.hasPufferfish = hasPufferfish;
        setChanged();
    }

    public boolean hasSalmon() {
        return hasSalmon;
    }

    public void setHasSalmon(boolean hasSalmon) {
        this.hasSalmon = hasSalmon;
        setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        hasCod = compoundTag.getBoolean("HasCod");
        hasPufferfish = compoundTag.getBoolean("HasPufferfish");
        hasSalmon = compoundTag.getBoolean("HasSalmon");
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        compoundTag.putBoolean("HasCod", hasCod);
        compoundTag.putBoolean("HasPufferfish", hasPufferfish);
        compoundTag.putBoolean("HasSalmon", hasSalmon);
    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState blockState, FishTankBlockEntity blockEntity) {
        if (level.isClientSide()) {
            this.updateAnimations();
        }
    }

    public void updateAnimations() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 123;
            this.idleAnimationState.start(0);
        } else {
            this.idleAnimationTimeout--;
        }
    }
}
