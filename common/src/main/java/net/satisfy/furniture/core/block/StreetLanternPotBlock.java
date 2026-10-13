package net.satisfy.furniture.core.block;

import net.satisfy.furniture.core.util.GeneralUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class StreetLanternPotBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<StreetLanternPotBlock> CODEC = simpleCodec(StreetLanternPotBlock::new);
    public static final BooleanProperty LEFT = BooleanProperty.create("left");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final Map<Direction, VoxelShape> LEFT_SHAPE = GeneralUtil.horizontalShapes(box(7.5, 0, 5, 13.5, 6, 11));

    private static final Map<Direction, VoxelShape> RIGHT_SHAPE = GeneralUtil.horizontalShapes(box(2.5, 0, 5, 8.5, 6, 11));

    public StreetLanternPotBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LEFT, true)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LEFT, WATERLOGGED);
    }

    public static int slot(BlockState state) {
        return state.getValue(LEFT) ? 0 : 1;
    }

    public static BlockPos lanternPos(BlockState state, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        return pos.above().relative(state.getValue(LEFT) ? facing.getClockWise() : facing.getCounterClockWise());
    }

    private static boolean hasLantern(BlockState state, LevelReader level, BlockPos pos) {
        BlockState lantern = level.getBlockState(lanternPos(state, pos));
        return lantern.getBlock() instanceof IronStreetLanternBlock
                && lantern.getValue(IronStreetLanternBlock.POTS)
                && lantern.getValue(IronStreetLanternBlock.FACING) == state.getValue(FACING);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return (state.getValue(LEFT) ? LEFT_SHAPE : RIGHT_SHAPE).get(state.getValue(FACING));
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!hasLantern(state, level, pos)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return IronStreetLanternBlock.usePot(level, lanternPos(state, pos), slot(state), stack, player);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return hasLantern(state, level, pos);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        if (!hasLantern(state, level, pos)) {
            return state.getFluidState().createLegacyBlock();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!newState.is(this) && hasLantern(state, level, pos)) {
            BlockPos lantern = lanternPos(state, pos);
            level.setBlock(lantern, level.getBlockState(lantern).setValue(IronStreetLanternBlock.POTS, false), 3);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(Items.FLOWER_POT);
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
