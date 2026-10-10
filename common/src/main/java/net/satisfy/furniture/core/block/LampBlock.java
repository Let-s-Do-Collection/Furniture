package net.satisfy.furniture.core.block;

import net.satisfy.foundation.overlay.BlockNotice;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.foundation.block.VerticalConnectingType;
import net.satisfy.furniture.core.util.GeneralUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LampBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<VerticalConnectingType> TYPE = GeneralUtil.VERTICAL_CONNECTING_TYPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final Map<VerticalConnectingType, Supplier<VoxelShape>> SHAPES_SUPPLIERS = new HashMap<>();
    private static final Map<VerticalConnectingType, VoxelShape> SHAPES = new HashMap<>();
    private final DyeColor color;

    static {
        SHAPES_SUPPLIERS.put(VerticalConnectingType.NONE, LampBlock::makeSingleShape);
        SHAPES_SUPPLIERS.put(VerticalConnectingType.MIDDLE, LampBlock::makeMiddleShape);
        SHAPES_SUPPLIERS.put(VerticalConnectingType.TOP, LampBlock::makeTopShape);
        SHAPES_SUPPLIERS.put(VerticalConnectingType.BOTTOM, LampBlock::makeBottomShape);

        for (Map.Entry<VerticalConnectingType, Supplier<VoxelShape>> entry : SHAPES_SUPPLIERS.entrySet()) {
            SHAPES.put(entry.getKey(), entry.getValue().get());
        }
    }

    public LampBlock(BlockBehaviour.Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(TYPE, VerticalConnectingType.NONE)
                .setValue(AbstractCandleBlock.LIT, false)
                .setValue(WATERLOGGED, false));
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE, WATERLOGGED, AbstractCandleBlock.LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level world = context.getLevel();
        BlockPos belowPos = clickedPos.below();
        BlockPos abovePos = clickedPos.above();

        BlockState belowState = world.getBlockState(belowPos);
        BlockState aboveState = world.getBlockState(abovePos);

        BlockState blockState = this.defaultBlockState();
        blockState = blockState.setValue(TYPE, getType(aboveState, belowState));
        blockState = blockState.setValue(WATERLOGGED, world.getFluidState(clickedPos).getType() == Fluids.WATER);
        return blockState;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClientSide) return;

        VerticalConnectingType type = getType(world.getBlockState(pos.above()), world.getBlockState(pos.below()));
        if (state.getValue(TYPE) != type) {
            world.setBlock(pos, state.setValue(TYPE, type), 3);
        }
    }

    public VerticalConnectingType getType(BlockState above, BlockState below) {
        boolean shapeAboveSame = above.getBlock() instanceof LampBlock;
        boolean shapeBelowSame = below.getBlock() instanceof LampBlock;

        if (shapeAboveSame && shapeBelowSame) {
            return VerticalConnectingType.MIDDLE;
        } else if (shapeAboveSame) {
            return VerticalConnectingType.BOTTOM;
        } else if (shapeBelowSame) {
            return VerticalConnectingType.TOP;
        } else {
            return VerticalConnectingType.NONE;
        }
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(TYPE));
    }

    private static VoxelShape makeTopShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.4375, 0, 0.4375, 0.5625, 1, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.5, 0.125, 0.875, 1, 0.875), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeBottomShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.75, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.75, 0.4375, 0.5625, 1, 0.5625), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeSingleShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.375, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.375, 0.4375, 0.5625, 1, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.5, 0.125, 0.875, 1, 0.875), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeMiddleShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.4375, 0, 0.4375, 0.5625, 1, 0.5625), BooleanOp.OR);
        return shape;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        if (player.isShiftKeyDown() && (state.getValue(TYPE) == VerticalConnectingType.NONE || state.getValue(TYPE) == VerticalConnectingType.TOP)) {
            boolean lit = !state.getValue(AbstractCandleBlock.LIT);
            world.setBlock(pos, state.setValue(AbstractCandleBlock.LIT, lit), 3);
            world.playSound(null, pos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (player instanceof ServerPlayer serverPlayer) {
                BlockNotice.send(serverPlayer, pos, Component.translatable(lit ? "hud.furniture.lamp.on" : "hud.furniture.lamp.off"));
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, world, pos, player, blockHitResult);
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return false;
    }
}
