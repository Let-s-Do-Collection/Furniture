package net.satisfy.furniture.core.block.entity;

import net.satisfy.furniture.core.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GrandfatherClockBlockEntity extends BlockEntity {
    private int strikesLeft;
    private long lastStrikeDay = -1L;

    public GrandfatherClockBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.GRANDFATHER_CLOCK_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean startMidnightStrike(long day) {
        if (lastStrikeDay == day) {
            return false;
        }
        lastStrikeDay = day;
        strikesLeft = 12;
        setChanged();
        return true;
    }

    public int getStrikesLeft() {
        return strikesLeft;
    }

    public void strike() {
        strikesLeft = Math.max(0, strikesLeft - 1);
        setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        strikesLeft = tag.getInt("StrikesLeft");
        lastStrikeDay = tag.contains("LastStrikeDay") ? tag.getLong("LastStrikeDay") : -1L;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("StrikesLeft", strikesLeft);
        tag.putLong("LastStrikeDay", lastStrikeDay);
    }
}
