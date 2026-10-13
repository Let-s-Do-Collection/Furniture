package net.satisfy.furniture.core.block;

import net.satisfy.furniture.core.block.entity.StreetLanternBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.block.VerticalConnectingType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class IronStreetLanternBlock extends StreetLanternBlock implements EntityBlock {
    public static final BooleanProperty POTS = BooleanProperty.create("pots");

    private final Supplier<Block> potBlock;

    public IronStreetLanternBlock(Properties settings, Supplier<Block> potBlock) {
        super(settings);
        this.potBlock = potBlock;
        this.registerDefaultState(this.defaultBlockState().setValue(POTS, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POTS);
    }

    public static BlockPos potPos(BlockPos lanternPos, Direction facing, int slot) {
        return lanternPos.below().relative(slot == 0 ? facing.getCounterClockWise() : facing.getClockWise());
    }

    public static boolean isPlantable(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem
                && (blockItem.getBlock().defaultBlockState().is(BlockTags.SMALL_FLOWERS) || blockItem.getBlock().defaultBlockState().is(BlockTags.SAPLINGS));
    }

    private boolean canHavePots(BlockState state) {
        VerticalConnectingType type = state.getValue(TYPE);
        return state.getValue(BULBS) == 1 && !state.getValue(POTS) && (type == VerticalConnectingType.TOP || type == VerticalConnectingType.NONE);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(Items.FLOWER_POT) || !canHavePots(state)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        Direction facing = state.getValue(FACING);
        for (int slot = 0; slot < 2; slot++) {
            if (!level.getBlockState(potPos(pos, facing, slot)).canBeReplaced()) {
                return ItemInteractionResult.FAIL;
            }
        }
        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(POTS, true), 3);
            for (int slot = 0; slot < 2; slot++) {
                BlockPos helperPos = potPos(pos, facing, slot);
                level.setBlock(helperPos, this.potBlock.get().defaultBlockState()
                        .setValue(StreetLanternPotBlock.FACING, facing)
                        .setValue(StreetLanternPotBlock.LEFT, slot == 0)
                        .setValue(StreetLanternPotBlock.WATERLOGGED, level.getFluidState(helperPos).is(Fluids.WATER)), 3);
            }
            stack.consume(1, player);
            level.playSound(null, pos, SoundEvents.DECORATED_POT_INSERT, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    public static ItemInteractionResult usePot(Level level, BlockPos lanternPos, int slot, ItemStack stack, Player player) {
        if (!(level.getBlockEntity(lanternPos) instanceof StreetLanternBlockEntity blockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        ItemStack current = blockEntity.getFlower(slot);
        if (current.isEmpty()) {
            if (!isPlantable(stack)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (!level.isClientSide) {
                blockEntity.setFlower(slot, stack.copyWithCount(1));
                stack.consume(1, player);
                level.playSound(null, lanternPos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!stack.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!level.isClientSide) {
            ItemStack removed = blockEntity.removeFlower(slot);
            if (!player.addItem(removed)) {
                player.drop(removed, false);
            }
            level.playSound(null, lanternPos, SoundEvents.GRASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    public static void removePots(Level level, BlockPos lanternPos, BlockState lanternState, boolean dropPot) {
        if (level.getBlockEntity(lanternPos) instanceof StreetLanternBlockEntity blockEntity) {
            for (int slot = 0; slot < 2; slot++) {
                ItemStack flower = blockEntity.removeFlower(slot);
                if (!flower.isEmpty()) {
                    BlockPos dropPos = potPos(lanternPos, lanternState.getValue(FACING), slot);
                    Containers.dropItemStack(level, dropPos.getX(), dropPos.getY(), dropPos.getZ(), flower);
                }
            }
        }
        if (dropPot) {
            Containers.dropItemStack(level, lanternPos.getX(), lanternPos.getY(), lanternPos.getZ(), new ItemStack(Items.FLOWER_POT));
        }
        Direction facing = lanternState.getValue(FACING);
        for (int slot = 0; slot < 2; slot++) {
            BlockPos helperPos = potPos(lanternPos, facing, slot);
            BlockState helperState = level.getBlockState(helperPos);
            if (helperState.getBlock() instanceof StreetLanternPotBlock) {
                level.setBlock(helperPos, helperState.getFluidState().createLegacyBlock(), 3);
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getValue(POTS) && (!newState.is(this) || !newState.getValue(POTS))) {
            removePots(level, pos, state, true);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StreetLanternBlockEntity(pos, state);
    }
}
