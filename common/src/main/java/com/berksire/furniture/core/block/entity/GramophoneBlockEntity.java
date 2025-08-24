package com.berksire.furniture.core.block.entity;

import com.berksire.furniture.core.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class GramophoneBlockEntity extends BlockEntity implements Clearable {
    private ItemStack recordItem = ItemStack.EMPTY;
    private long tickCount;
    private long recordStartedTick;
    private boolean isPlaying;
    private boolean repeat;
    private final JukeboxSongPlayer jukeboxSongPlayer;

    public GramophoneBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(EntityTypeRegistry.GRAMOPHONE_BLOCK_ENTITY.get(), blockPos, blockState);
        this.jukeboxSongPlayer = new JukeboxSongPlayer(this::onSongChanged, this.getBlockPos());
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        if (compoundTag.contains("RecordItem")) {
            this.recordItem = ItemStack.parse(provider, compoundTag.getCompound("RecordItem")).orElse(ItemStack.EMPTY);
        }
        this.isPlaying = compoundTag.getBoolean("IsPlaying");
        this.recordStartedTick = compoundTag.getLong("RecordStartTick");
        this.tickCount = compoundTag.getLong("TickCount");
        this.repeat = compoundTag.getBoolean("Repeat");
        if (compoundTag.contains("ticks_since_song_started", 4)) {
            JukeboxSong.fromStack(provider, this.recordItem).ifPresent((holder) -> this.jukeboxSongPlayer.setSongWithoutPlaying(holder, compoundTag.getLong("ticks_since_song_started")));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        if (!this.recordItem.isEmpty()) {
            compoundTag.put("RecordItem", this.recordItem.save(provider));
        }
        compoundTag.putBoolean("IsPlaying", this.isPlaying);
        compoundTag.putLong("RecordStartTick", this.recordStartedTick);
        compoundTag.putLong("TickCount", this.tickCount);
        compoundTag.putBoolean("Repeat", this.repeat);
        if (this.jukeboxSongPlayer.getSong() != null) {
            compoundTag.putLong("ticks_since_song_started", this.jukeboxSongPlayer.getTicksSinceSongStarted());
        }
    }

    public boolean isRecordPlaying() {
        return !this.recordItem.isEmpty() && this.isPlaying && this.getBlockState().getValue(JukeboxBlock.HAS_RECORD);
    }

    public static void playRecordTick(Level level, BlockState state, GramophoneBlockEntity be) {
        be.tick(level, state);
    }

    private void startPlaying() {
        this.recordStartedTick = this.tickCount;
        this.isPlaying = true;
        if (this.level != null) {
            JukeboxSong.fromStack(this.level.registryAccess(), this.recordItem).ifPresent(songHolder -> this.jukeboxSongPlayer.play(this.level, songHolder));
            this.level.gameEvent(GameEvent.JUKEBOX_PLAY, this.worldPosition, GameEvent.Context.of(this.getBlockState()));
            this.level.levelEvent(null, 1010, this.worldPosition, Item.getId(this.recordItem.getItem()));
            this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
            this.setChanged();
        }
    }

    public void tick(Level level, BlockState state) {
        this.tickCount++;
        this.jukeboxSongPlayer.tick(level, state);
        net.minecraft.world.item.JukeboxSong song = this.jukeboxSongPlayer.getSong();
        if (this.isPlaying && song != null) {
            long elapsed = this.jukeboxSongPlayer.getTicksSinceSongStarted();
            long length = song.lengthInTicks();
            if (elapsed >= length) {
                if (this.repeat) {
                    net.minecraft.world.item.JukeboxSong.fromStack(level.registryAccess(), this.recordItem).ifPresent(h -> {
                        this.jukeboxSongPlayer.setSongWithoutPlaying(h, 0L);
                        level.levelEvent(null, 1010, this.worldPosition, net.minecraft.world.item.Item.getId(this.recordItem.getItem()));
                    });
                } else {
                    this.stopPlaying();
                }
            }
        }
    }

    public void popOutRecord() {
        if (this.level != null && !this.level.isClientSide) {
            this.stopPlaying();
            BlockPos blockPos = this.getBlockPos();
            ItemStack itemStack = this.recordItem;
            if (!itemStack.isEmpty()) {
                this.recordItem = ItemStack.EMPTY;
                ItemStack singleDisk = itemStack.copy();
                singleDisk.setCount(1);
                Vec3 vec3 = Vec3.atLowerCornerWithOffset(blockPos, 0.5, 1.01, 0.5).offsetRandom(this.level.random, 0.7F);
                ItemEntity itemEntity = new ItemEntity(this.level, vec3.x(), vec3.y(), vec3.z(), singleDisk);
                itemEntity.setDefaultPickUpDelay();
                this.level.addFreshEntity(itemEntity);
                this.setChanged();
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
                if (this.level.getBlockState(this.worldPosition).is(Blocks.AIR)) {
                    return;
                }
                BlockState state = this.level.getBlockState(this.worldPosition);
                this.level.setBlock(this.worldPosition, state.setValue(JukeboxBlock.HAS_RECORD, false), 3);
                this.level.updateNeighborsAt(this.worldPosition, state.getBlock());
            }
        }
    }

    public void onSongChanged() {
        assert this.level != null;
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.setChanged();
    }

    @Override
    public void setRemoved() {
        this.stopPlayingOnRemove();
        super.setRemoved();
    }

    public void setRecord(ItemStack record) {
        if (!record.isEmpty()) {
            this.recordItem = record.copy();
            this.recordItem.setCount(1);
            this.setHasRecordBlockState(true);
            this.startPlaying();
            this.setChanged();
        }
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
        this.setChanged();
    }

    public boolean isRepeat() {
        return this.repeat;
    }

    private void setHasRecordBlockState(boolean hasRecord) {
        if (this.level != null && !this.level.getBlockState(this.worldPosition).is(Blocks.AIR)) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.setBlock(this.worldPosition, state.setValue(JukeboxBlock.HAS_RECORD, hasRecord), 3);
            this.level.updateNeighborsAt(this.worldPosition, state.getBlock());
        }
    }

    public void stopPlaying() {
        if (this.isPlaying) {
            this.isPlaying = false;
            if (this.level != null) {
                this.jukeboxSongPlayer.stop(this.level, this.getBlockState());
                this.level.gameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.worldPosition, GameEvent.Context.of(this.getBlockState()));
                this.level.levelEvent(1011, this.worldPosition, 0);
                this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
                this.setChanged();
            }
        }
    }

    public void stopPlayingOnRemove() {
        if (this.isPlaying) {
            this.stopPlaying();
        }
    }

    @Override
    public void clearContent() {
        this.recordItem = ItemStack.EMPTY;
        this.setHasRecordBlockState(false);
        this.stopPlaying();
    }
}
