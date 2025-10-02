package com.berksire.furniture.core.block;

import com.berksire.furniture.core.block.entity.StorageBlockEntity;
import com.berksire.furniture.core.util.FurnitureUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class StorageBlock extends FacingBlock implements EntityBlock {
    public static final SoundEvent DEFAULT_SOUND = SoundEvents.WOOD_PLACE;

    public StorageBlock(Properties props) {
        super(props);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof StorageBlockEntity shelf)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        Optional<Tuple<Float, Float>> coords = FurnitureUtil.getRelativeHitCoordinatesForBlockFace(hit, state.getValue(FACING), unAllowedDirections());
        if (coords.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        int slot = getSection(coords.get().getA(), coords.get().getB());
        if (slot == Integer.MIN_VALUE) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (!shelf.getInventory().get(slot).isEmpty()) {
            remove(level, pos, player, shelf, slot);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        } else if (!stack.isEmpty() && canInsertStack(stack)) {
            add(level, pos, player, shelf, stack, slot);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    private void add(Level level, BlockPos pos, Player player, StorageBlockEntity shelf, ItemStack stack, int slot) {
        if (level.isClientSide) return;
        SoundEvent sound = getAddSound(level, pos, player, slot);
        shelf.setStack(slot, stack.split(1));
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (player.isCreative()) stack.grow(1);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    private void remove(Level level, BlockPos pos, Player player, StorageBlockEntity shelf, int slot) {
        if (level.isClientSide) return;
        ItemStack removed = shelf.removeStack(slot);
        SoundEvent sound = getRemoveSound(level, pos, player, slot);
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!player.getInventory().add(removed)) player.drop(removed, false);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    public SoundEvent getRemoveSound(Level level, BlockPos pos, Player player, int slot) {
        return DEFAULT_SOUND;
    }

    public SoundEvent getAddSound(Level level, BlockPos pos, Player player, int slot) {
        return DEFAULT_SOUND;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof StorageBlockEntity shelf) {
                if (level instanceof ServerLevel) Containers.dropContents(level, pos, shelf.getInventory());
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StorageBlockEntity(pos, state, size());
    }

    public abstract int size();

    public abstract ResourceLocation type();

    public abstract Direction[] unAllowedDirections();

    public abstract boolean canInsertStack(ItemStack stack);

    public abstract int getSection(Float x, Float y);
}
