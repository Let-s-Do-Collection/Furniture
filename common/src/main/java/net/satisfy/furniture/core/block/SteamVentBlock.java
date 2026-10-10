package net.satisfy.furniture.core.block;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.registry.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.overlay.BlockNotice;
import net.satisfy.foundation.registry.FoundationParticles;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SteamVentBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty INVERTED = BooleanProperty.create("inverted");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty PARTICLES_ENABLED = BooleanProperty.create("particles_enabled");
    private static final int STEAM_COLOR = 0xFFE6E6E6;
    private static final VoxelShape SHAPE = Shapes.or(Shapes.box(0, 0, 0, 1, 0.375, 1), Shapes.box(0.25, 0.375, 0.25, 0.75, 1, 0.75));
    private static final VoxelShape INVERTED_SHAPE = Shapes.or(Shapes.box(0, 0.625, 0, 1, 1, 1), Shapes.box(0.25, 0, 0.25, 0.75, 0.625, 0.75));

    public SteamVentBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(INVERTED, false)
                .setValue(WATERLOGGED, false)
                .setValue(PARTICLES_ENABLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(INVERTED, WATERLOGGED, PARTICLES_ENABLED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        boolean inverted = face == Direction.UP || face != Direction.DOWN && context.getClickLocation().y - pos.getY() < 0.5;
        return this.defaultBlockState()
                .setValue(INVERTED, inverted)
                .setValue(WATERLOGGED, context.getLevel().getFluidState(pos).getType() == Fluids.WATER);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(INVERTED) ? INVERTED_SHAPE : SHAPE;
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public static Direction pipeSide(BlockState state) {
        return state.getValue(INVERTED) ? Direction.DOWN : Direction.UP;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (!level.isClientSide) {
            boolean forced = !blockState.getValue(PARTICLES_ENABLED);
            level.setBlock(blockPos, blockState.setValue(PARTICLES_ENABLED, forced), 3);
            if (player instanceof ServerPlayer serverPlayer) {
                BlockNotice.send(serverPlayer, blockPos, Component.translatable(forced ? "hud.furniture.steam_vent.forced_on" : "hud.furniture.steam_vent.auto"));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    public static BlockPos findSteamSource(BlockGetter level, BlockPos vent) {
        Direction opening = pipeSide(level.getBlockState(vent)).getOpposite();
        for (int i = 1; i <= PlatformHelper.getSteamVentSourceRange(); i++) {
            BlockPos pos = vent.relative(opening, i);
            BlockState state = level.getBlockState(pos);
            if (state.is(TagRegistry.STEAM_SOURCES)) {
                return !state.hasProperty(BlockStateProperties.LIT) || state.getValue(BlockStateProperties.LIT) ? pos : null;
            }
            if (state.isCollisionShapeFullBlock(level, pos)) {
                return null;
            }
        }
        return null;
    }

    @Nullable
    public static BlockPos findChimney(BlockGetter level, BlockPos vent) {
        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(vent);
        visited.add(vent);
        while (!queue.isEmpty() && visited.size() < PlatformHelper.getSteamMaxPipeLength()) {
            BlockPos current = queue.poll();
            BlockState state = level.getBlockState(current);
            List<Direction> exits = new ArrayList<>();
            if (state.getBlock() instanceof SteamVentBlock) {
                exits.add(pipeSide(state));
            } else if (state.getBlock() instanceof SteamPipeBlock) {
                for (Direction direction : Direction.values()) {
                    if (SteamPipeBlock.connected(state, direction)) {
                        exits.add(direction);
                    }
                }
            }
            for (Direction direction : exits) {
                BlockPos next = current.relative(direction);
                if (!visited.add(next)) {
                    continue;
                }
                Block block = level.getBlockState(next).getBlock();
                if (block instanceof ChimneyBlock) {
                    return next;
                }
                if (block instanceof SteamPipeBlock) {
                    queue.add(next);
                }
            }
        }
        return null;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (!PlatformHelper.areSteamVentParticlesEnabled()) {
            return;
        }
        BlockPos source = findSteamSource(world, pos);
        if (source == null && !state.getValue(PARTICLES_ENABLED)) {
            return;
        }
        int direction = state.getValue(INVERTED) ? 1 : -1;
        int distance = source == null ? 2 : Math.abs(pos.getY() - source.getY());
        for (int i = 0; i < 2; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 0.8;
            double offsetZ = (random.nextDouble() - 0.5) * 0.8;
            double travel = 1.0 + random.nextDouble() * Math.max(0, distance - 1);
            double y = pos.getY() + 0.5 + direction * travel;
            world.addParticle(ColorParticleOption.create(FoundationParticles.COLORED_STEAM.get(), STEAM_COLOR),
                    pos.getX() + 0.5 + offsetX, y, pos.getZ() + 0.5 + offsetZ,
                    -offsetX * 0.06, -direction * (0.05 + random.nextDouble() * 0.03), -offsetZ * 0.06);
        }
        if (random.nextInt(3) == 0) {
            BlockPos chimney = findChimney(world, pos);
            if (chimney != null) {
                world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, chimney.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3, chimney.getY() + 1.0, chimney.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3, 0.0, 0.07, 0.0);
            }
        }
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return false;
    }
}
