package com.berksire.furniture.core.block;

import com.berksire.furniture.core.block.entity.GrandfatherClockBlockEntity;
import com.berksire.furniture.core.registry.SoundRegistry;
import com.berksire.furniture.core.util.GeneralUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public class GrandfatherClockBlock extends FacingBlock implements EntityBlock {

    public enum Part implements StringRepresentable {
        BOTTOM("bottom"),
        MIDDLE("middle"),
        TOP("top");

        private final String serializedName;

        Part(String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.serializedName;
        }
    }

    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    private static final Supplier<VoxelShape> BOTTOM_SHAPE_SUPPLIER = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.25, 0.875, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.4375, 0.1875, 0.9375, 0.5625, 1), BooleanOp.OR);
        return shape;
    };

    private static final Supplier<VoxelShape> MIDDLE_SHAPE_SUPPLIER = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.25, 0.875, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.625, 0.1875, 0.9375, 0.75, 1), BooleanOp.OR);
        return shape;
    };

    private static final Supplier<VoxelShape> TOP_SHAPE_SUPPLIER = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.25, 0.875, 0.375, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.375, 0.25, 0.875, 0.6875, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.0625, 0.1875, 0.5625, 0.1875, 0.25), BooleanOp.OR);
        return shape;
    };

    private static final Map<Direction, VoxelShape> SHAPE_BOTTOM = Util.make(new HashMap<>(), map -> {
        VoxelShape baseShape = BOTTOM_SHAPE_SUPPLIER.get();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, baseShape));
        }
    });

    private static final Map<Direction, VoxelShape> SHAPE_MIDDLE = Util.make(new HashMap<>(), map -> {
        VoxelShape baseShape = MIDDLE_SHAPE_SUPPLIER.get();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, baseShape));
        }
    });

    private static final Map<Direction, VoxelShape> SHAPE_TOP = Util.make(new HashMap<>(), map -> {
        VoxelShape baseShape = TOP_SHAPE_SUPPLIER.get();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, baseShape));
        }
    });

    public GrandfatherClockBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, Part.BOTTOM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();

        if (clickedPos.getY() > level.getMaxBuildHeight() - 3) {
            return null;
        }

        BlockPos middlePos = clickedPos.above();
        BlockPos topPos = clickedPos.above(2);

        if (!level.getBlockState(middlePos).canBeReplaced(context) || !level.getBlockState(topPos).canBeReplaced(context)) {
            return null;
        }

        Direction facingDirection = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facingDirection).setValue(PART, Part.BOTTOM);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity placer, ItemStack itemStack) {
        if (level.isClientSide) {
            return;
        }

        if (blockState.getValue(PART) != Part.BOTTOM) {
            return;
        }

        Direction facingDirection = blockState.getValue(FACING);

        BlockPos middlePos = blockPos.above();
        BlockPos topPos = blockPos.above(2);

        level.setBlock(middlePos, blockState.setValue(PART, Part.MIDDLE).setValue(FACING, facingDirection), 3);
        level.setBlock(topPos, blockState.setValue(PART, Part.TOP).setValue(FACING, facingDirection), 3);

        level.scheduleTick(blockPos, this, 20);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            Part part = state.getValue(PART);
            if (part != Part.BOTTOM) {
                BlockPos bottomPos = getBottomPos(state, pos);
                boolean dropItems = !player.isCreative();
                level.destroyBlock(bottomPos, dropItems, player);
                return state;
            }
        }
        super.playerWillDestroy(level, pos, state, player);
        return state;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GrandfatherClockBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facingDirection = state.getValue(FACING);
        Part part = state.getValue(PART);

        if (part == Part.TOP) {
            return SHAPE_TOP.get(facingDirection);
        }
        if (part == Part.MIDDLE) {
            return SHAPE_MIDDLE.get(facingDirection);
        }
        return SHAPE_BOTTOM.get(facingDirection);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(PART) != Part.BOTTOM) {
            return;
        }

        if (level.getDayTime() % 24000 == 0) {
            level.playSound(null, pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else {
            level.playSound(null, pos, SoundRegistry.GRANDFATHERS_CLOCK_TICKING.get(), SoundSource.BLOCKS, 0.15F, 1.0F);
        }

        level.scheduleTick(pos, this, 20);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide && state.getValue(PART) == Part.BOTTOM) {
            level.scheduleTick(pos, this, 20);
        }
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide && state.getBlock() != newState.getBlock()) {
            BlockPos bottomPos = getBottomPos(state, pos);

            removeClockPart(level, bottomPos.above(2));
            removeClockPart(level, bottomPos.above());
            removeClockPart(level, bottomPos);
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    private static BlockPos getBottomPos(BlockState state, BlockPos pos) {
        Part part = state.getValue(PART);
        if (part == Part.MIDDLE) {
            return pos.below();
        }
        if (part == Part.TOP) {
            return pos.below(2);
        }
        return pos;
    }

    private static void removeClockPart(Level level, BlockPos targetPos) {
        BlockState targetState = level.getBlockState(targetPos);
        if (!(targetState.getBlock() instanceof GrandfatherClockBlock)) {
            return;
        }

        level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 35);
        level.levelEvent(2001, targetPos, Block.getId(targetState));
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            long time = level.getDayTime();
            int hours = (int) ((time / 1000 + 6) % 24);
            int minutes = (int) (60 * (time % 1000) / 1000);
            player.displayClientMessage(Component.translatable("tooltip.furniture.clock", String.format(Locale.ROOT, "%02d:%02d", hours, minutes)), true);
        }
        return InteractionResult.SUCCESS;
    }
}