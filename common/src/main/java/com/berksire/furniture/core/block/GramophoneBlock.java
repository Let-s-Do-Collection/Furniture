package com.berksire.furniture.core.block;

import com.berksire.furniture.core.block.entity.GramophoneBlockEntity;
import com.berksire.furniture.core.registry.EntityTypeRegistry;
import com.berksire.furniture.core.util.FurnitureUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GramophoneBlock extends BaseEntityBlock {
    public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty REPEAT = BooleanProperty.create("repeat");

    private static final VoxelShape SHAPE_LOWER = makeLowerShape();
    private static final VoxelShape SHAPE_UPPER = makeUpperShape();

    public static final Map<Direction, VoxelShape> SHAPE_LOWER_MAP = net.minecraft.Util.make(new HashMap<>(), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
            map.put(direction, FurnitureUtil.rotateShape(Direction.NORTH, direction, SHAPE_LOWER));
        }
    });

    public static final Map<Direction, VoxelShape> SHAPE_UPPER_MAP = net.minecraft.Util.make(new HashMap<>(), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
            map.put(direction, FurnitureUtil.rotateShape(Direction.NORTH, direction, SHAPE_UPPER));
        }
    });

    public GramophoneBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_RECORD, false).setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER).setValue(REPEAT, false));
    }

    public static final MapCodec<GramophoneBlock> CODEC = simpleCodec(GramophoneBlock::new);

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    private static VoxelShape makeLowerShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.125, 0.75, 0.3125, 0.875, 1, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.875, 0.375, 0.75, 1, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.5, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.5, 0.8125, 0.5625, 1, 0.9375), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeUpperShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.3125, 0.875, 0.5, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0, 0.375, 0.75, 0.375, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0, 0.6875, 0.625, 0.25, 0.9375), BooleanOp.OR);
        return shape;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        BlockPos abovePos = blockPos.above();
        level.setBlock(abovePos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER).setValue(FACING, blockState.getValue(FACING)).setValue(HAS_RECORD, blockState.getValue(HAS_RECORD)).setValue(REPEAT, blockState.getValue(REPEAT)), 3);
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState newState, boolean isMoving) {
        if (!blockState.is(newState.getBlock())) {
            DoubleBlockHalf half = blockState.getValue(HALF);
            BlockPos basePos = half == DoubleBlockHalf.UPPER ? blockPos.below() : blockPos;
            BlockEntity baseBe = level.getBlockEntity(basePos);
            if (baseBe instanceof GramophoneBlockEntity gram) {
                gram.stopPlayingOnRemove();
                level.levelEvent(1011, basePos, 0);
                gram.popOutRecord();
                level.removeBlockEntity(basePos);
            }
            BlockPos otherPos = half == DoubleBlockHalf.LOWER ? blockPos.above() : blockPos.below();
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.is(this) && otherState.getValue(HALF) != half) {
                level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(2001, otherPos, Block.getId(otherState));
                level.removeBlockEntity(otherPos);
            }
        }
        super.onRemove(blockState, level, blockPos, newState, isMoving);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return super.canSurvive(state, world, pos);
        } else {
            BlockState belowState = world.getBlockState(pos.below());
            return belowState.is(this) && belowState.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        return pos.getY() < world.getMaxBuildHeight() - 1 && world.getBlockState(pos.above()).canBeReplaced(context) ? this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(HALF, DoubleBlockHalf.LOWER) : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_RECORD, FACING, HALF, REPEAT);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? SHAPE_UPPER_MAP.get(state.getValue(FACING)) : SHAPE_LOWER_MAP.get(state.getValue(FACING));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, EntityTypeRegistry.GRAMOPHONE_BLOCK_ENTITY.get(), (level1, pos, state, be) -> GramophoneBlockEntity.playRecordTick(level1, state, be));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GramophoneBlockEntity(blockPos, blockState);
    }

    @Override
    public boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        BlockEntity blockEntity = blockGetter.getBlockEntity(blockPos);
        if (blockEntity instanceof GramophoneBlockEntity discPlayerBlockEntity) {
            if (discPlayerBlockEntity.isRecordPlaying()) {
                return 15;
            }
        }
        return 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        BlockEntity var5 = level.getBlockEntity(blockPos);
        if (var5 instanceof JukeboxBlockEntity jukeboxBlockEntity) {
            return jukeboxBlockEntity.getComparatorOutput();
        } else {
            return 0;
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockPos basePos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        BlockState baseState = level.getBlockState(basePos);
        if (!baseState.is(this)) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {
            if (baseState.getValue(HAS_RECORD)) {
                BlockEntity be = level.getBlockEntity(basePos);
                if (be instanceof GramophoneBlockEntity gram) {
                    if (!level.isClientSide) {
                        gram.popOutRecord();
                        BlockPos otherPos = basePos.above();
                        BlockState otherState = level.getBlockState(otherPos);
                        level.setBlock(basePos, baseState.setValue(HAS_RECORD, false), 3);
                        if (otherState.is(this)) level.setBlock(otherPos, otherState.setValue(HAS_RECORD, false), 3);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            return InteractionResult.PASS;
        }

        BlockEntity be = level.getBlockEntity(basePos);
        if (be instanceof GramophoneBlockEntity gram) {
            if (!level.isClientSide) {
                boolean next = !gram.isRepeat();
                gram.setRepeat(next);
                player.displayClientMessage(Component.translatable("tooltip.furniture.repeat_mode", Component.translatable(next ? "tooltip.furniture.on" : "tooltip.furniture.off")), true);
                BlockPos otherPos = basePos.above();
                BlockState otherState = level.getBlockState(otherPos);
                level.setBlock(basePos, baseState.setValue(REPEAT, next), 3);
                if (otherState.is(this)) level.setBlock(otherPos, otherState.setValue(REPEAT, next), 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockPos basePos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        BlockState baseState = level.getBlockState(basePos);
        if (!baseState.is(this)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (baseState.getValue(HAS_RECORD)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        JukeboxPlayable playable = stack.get(net.minecraft.core.component.DataComponents.JUKEBOX_PLAYABLE);
        if (playable == null) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(basePos);
            if (be instanceof GramophoneBlockEntity gram) {
                ItemStack inserted = stack.copy();
                inserted.setCount(1);
                gram.setRecord(inserted);
                stack.shrink(1);
                BlockPos otherPos = basePos.above();
                BlockState otherState = level.getBlockState(otherPos);
                level.setBlock(basePos, baseState.setValue(HAS_RECORD, true), 3);
                if (otherState.is(this)) level.setBlock(otherPos, otherState.setValue(HAS_RECORD, true), 3);
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return false;
    }
}