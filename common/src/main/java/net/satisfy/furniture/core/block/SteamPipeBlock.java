package net.satisfy.furniture.core.block;

import net.satisfy.furniture.platform.PlatformHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.satisfy.foundation.registry.FoundationParticles;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SteamPipeBlock extends Block {
    public static final MapCodec<SteamPipeBlock> CODEC = simpleCodec(SteamPipeBlock::new);
    private static final VoxelShape CORE = Block.box(4, 4, 4, 12, 12, 12);
    private static final VoxelShape VERTICAL = Block.box(4, 0, 4, 12, 16, 12);
    private static final Map<Direction, VoxelShape> ARMS = new EnumMap<>(Direction.class);
    private static final Map<Direction, VoxelShape> FLANGES = new EnumMap<>(Direction.class);
    public static final Map<Direction, EnumProperty<End>> PROPERTY_BY_DIRECTION = new EnumMap<>(Direction.class);
    public static final EnumProperty<End> NORTH = EnumProperty.create("north", End.class);
    public static final EnumProperty<End> EAST = EnumProperty.create("east", End.class);
    public static final EnumProperty<End> SOUTH = EnumProperty.create("south", End.class);
    public static final EnumProperty<End> WEST = EnumProperty.create("west", End.class);
    public static final EnumProperty<End> UP = EnumProperty.create("up", End.class);
    public static final EnumProperty<End> DOWN = EnumProperty.create("down", End.class);
    private final Map<BlockState, VoxelShape> shapes;
    private static final Map<Long, Long> PRESSURE_CACHE = new HashMap<>();
    private static final int PRESSURE_CACHE_TICKS = 40;
    private static final int STEAM_COLOR = 0xFFE6E6E6;
    private static final int OIL_COLOR = 0xFF1A1714;

    static {
        ARMS.put(Direction.UP, Block.box(4, 12, 4, 12, 16, 12));
        ARMS.put(Direction.DOWN, Block.box(4, 0, 4, 12, 4, 12));
        ARMS.put(Direction.NORTH, Block.box(4, 4, 0, 12, 12, 4));
        ARMS.put(Direction.SOUTH, Block.box(4, 4, 12, 12, 12, 16));
        ARMS.put(Direction.WEST, Block.box(0, 4, 4, 4, 12, 12));
        ARMS.put(Direction.EAST, Block.box(12, 4, 4, 16, 12, 12));
        FLANGES.put(Direction.UP, Block.box(3, 14, 3, 13, 16, 13));
        FLANGES.put(Direction.DOWN, Block.box(3, 0, 3, 13, 2, 13));
        FLANGES.put(Direction.NORTH, Block.box(3, 3, 0, 13, 13, 2));
        FLANGES.put(Direction.SOUTH, Block.box(3, 3, 14, 13, 13, 16));
        FLANGES.put(Direction.WEST, Block.box(0, 3, 3, 2, 13, 13));
        FLANGES.put(Direction.EAST, Block.box(14, 3, 3, 16, 13, 13));
        PROPERTY_BY_DIRECTION.put(Direction.NORTH, NORTH);
        PROPERTY_BY_DIRECTION.put(Direction.EAST, EAST);
        PROPERTY_BY_DIRECTION.put(Direction.SOUTH, SOUTH);
        PROPERTY_BY_DIRECTION.put(Direction.WEST, WEST);
        PROPERTY_BY_DIRECTION.put(Direction.UP, UP);
        PROPERTY_BY_DIRECTION.put(Direction.DOWN, DOWN);
    }

    public SteamPipeBlock(Properties properties) {
        super(properties);
        BlockState state = this.stateDefinition.any();
        for (Direction direction : Direction.values()) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), End.NONE);
        }
        this.registerDefaultState(state);
        this.shapes = this.getShapeForEachState(SteamPipeBlock::buildShape);
    }

    @Override
    protected @NotNull MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        for (Direction direction : Direction.values()) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), End.of(connectsTo(context.getLevel(), context.getClickedPos(), direction)));
        }
        return withFlanges(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return withFlanges(state.setValue(PROPERTY_BY_DIRECTION.get(direction), End.of(connectsTo(level, pos, direction))), level, pos);
    }

    private static BlockState withFlanges(BlockState state, BlockGetter level, BlockPos pos) {
        boolean joint = straightAxis(state) == null;
        for (Direction direction : Direction.values()) {
            if (!connected(state, direction)) {
                continue;
            }
            boolean flange = false;
            BlockState neighbor = level.getBlockState(pos.relative(direction));
            if (!(neighbor.getBlock() instanceof SteamPipeBlock)) {
                flange = true;
            } else if (joint) {
                flange = straightAxis(neighbor) != null || direction.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            }
            state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), flange ? End.FLANGE : End.PIPE);
        }
        return state;
    }

    public static boolean connectsTo(BlockGetter level, BlockPos pos, Direction direction) {
        BlockState neighbor = level.getBlockState(pos.relative(direction));
        if (neighbor.getBlock() instanceof SteamPipeBlock) {
            return true;
        }
        if (neighbor.getBlock() instanceof SteamVentBlock) {
            return SteamVentBlock.pipeSide(neighbor) == direction.getOpposite();
        }
        return neighbor.getBlock() instanceof ChimneyBlock && direction == Direction.UP;
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.get(state);
    }

    private static VoxelShape buildShape(BlockState state) {
        Direction.Axis axis = straightAxis(state);
        VoxelShape shape = CORE;
        if (axis != null) {
            shape = switch (axis) {
                case X -> Block.box(0, 4, 4, 16, 12, 12);
                case Y -> VERTICAL;
                case Z -> Block.box(4, 4, 0, 12, 12, 16);
            };
        }
        for (Direction direction : Direction.values()) {
            if (connected(state, direction)) {
                shape = Shapes.or(shape, ARMS.get(direction));
            }
            if (state.getValue(PROPERTY_BY_DIRECTION.get(direction)) == End.FLANGE) {
                shape = Shapes.or(shape, FLANGES.get(direction));
            }
        }
        return shape;
    }

    private static Direction.Axis straightAxis(BlockState state) {
        Direction first = null;
        int count = 0;
        for (Direction direction : Direction.values()) {
            if (connected(state, direction)) {
                if (first == null) {
                    first = direction;
                } else if (direction != first.getOpposite()) {
                    return null;
                }
                count++;
            }
        }
        if (count == 0) {
            return Direction.Axis.Y;
        }
        return first.getAxis();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!PlatformHelper.areSteamPipeParticlesEnabled() || !isPressurized(level, pos)) {
            return;
        }
        Direction open = openEnd(state);
        if (open != null) {
            leak(level, pos, open, random);
        }
        if (random.nextInt(6) == 0) {
            drip(level, pos, state, random);
        }
        if (random.nextInt(400) == 0) {
            burst(level, pos, state, random);
        }
    }

    private static void leak(Level level, BlockPos pos, Direction open, RandomSource random) {
        double x = pos.getX() + 0.5 + open.getStepX() * 0.5;
        double y = pos.getY() + 0.5 + open.getStepY() * 0.5;
        double z = pos.getZ() + 0.5 + open.getStepZ() * 0.5;
        for (int i = 0; i < 2; i++) {
            double speed = 0.05 + random.nextDouble() * 0.05;
            level.addParticle(ColorParticleOption.create(FoundationParticles.COLORED_STEAM.get(), STEAM_COLOR),
                    x + (random.nextDouble() - 0.5) * 0.2, y + (random.nextDouble() - 0.5) * 0.2, z + (random.nextDouble() - 0.5) * 0.2,
                    open.getStepX() * speed, open.getStepY() * speed + 0.01, open.getStepZ() * speed);
        }
        if (PlatformHelper.areSteamPipeSoundsEnabled() && random.nextInt(40) == 0) {
            level.playLocalSound(pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.08F, 1.6F + random.nextFloat() * 0.3F, false);
        }
    }

    private static void drip(Level level, BlockPos pos, BlockState state, RandomSource random) {
        if (connected(state, Direction.DOWN) || !level.getBlockState(pos.below()).isAir()) {
            return;
        }
        Direction.Axis axis = straightAxis(state);
        if (axis == Direction.Axis.Y) {
            return;
        }
        level.addParticle(ColorParticleOption.create(FoundationParticles.COLORED_DRIP.get(), OIL_COLOR),
                pos.getX() + 0.3 + random.nextDouble() * 0.4, pos.getY() + 0.24, pos.getZ() + 0.3 + random.nextDouble() * 0.4,
                0.0, 0.0, 0.0);
    }

    private static void burst(Level level, BlockPos pos, BlockState state, RandomSource random) {
        Direction.Axis axis = straightAxis(state);
        Direction side = Direction.getRandom(random);
        if (axis != null && side.getAxis() == axis || connected(state, side)) {
            return;
        }
        double x = pos.getX() + 0.5 + side.getStepX() * 0.3;
        double y = pos.getY() + 0.5 + side.getStepY() * 0.3;
        double z = pos.getZ() + 0.5 + side.getStepZ() * 0.3;
        for (int i = 0; i < 12; i++) {
            double speed = 0.12 + random.nextDouble() * 0.1;
            level.addParticle(ColorParticleOption.create(FoundationParticles.COLORED_STEAM.get(), STEAM_COLOR),
                    x, y, z,
                    side.getStepX() * speed + (random.nextDouble() - 0.5) * 0.04,
                    side.getStepY() * speed + (random.nextDouble() - 0.5) * 0.04,
                    side.getStepZ() * speed + (random.nextDouble() - 0.5) * 0.04);
        }
        if (PlatformHelper.areSteamPipeSoundsEnabled()) {
            level.playLocalSound(pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.35F, 1.4F + random.nextFloat() * 0.4F, false);
        }
    }

    @Nullable
    private static Direction openEnd(BlockState state) {
        Direction connected = null;
        for (Direction direction : Direction.values()) {
            if (connected(state, direction)) {
                if (connected != null) {
                    return null;
                }
                connected = direction;
            }
        }
        return connected == null ? null : connected.getOpposite();
    }

    private static boolean isPressurized(Level level, BlockPos pos) {
        long now = level.getGameTime();
        Long until = PRESSURE_CACHE.get(pos.asLong());
        if (until != null && Math.abs(until) > now) {
            return until > 0;
        }
        if (PRESSURE_CACHE.size() > 4096) {
            PRESSURE_CACHE.clear();
        }
        Set<BlockPos> network = new HashSet<>();
        boolean pressurized = scanNetwork(level, pos, network);
        long expiry = now + PRESSURE_CACHE_TICKS;
        for (BlockPos member : network) {
            PRESSURE_CACHE.put(member.asLong(), pressurized ? expiry : -expiry);
        }
        return pressurized;
    }

    private static boolean scanNetwork(Level level, BlockPos start, Set<BlockPos> visited) {
        Deque<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);
        boolean pressurized = false;
        while (!queue.isEmpty() && visited.size() < PlatformHelper.getSteamMaxPipeLength()) {
            BlockPos current = queue.poll();
            BlockState state = level.getBlockState(current);
            if (!(state.getBlock() instanceof SteamPipeBlock)) {
                continue;
            }
            for (Direction direction : Direction.values()) {
                if (!connected(state, direction)) {
                    continue;
                }
                BlockPos next = current.relative(direction);
                BlockState neighbor = level.getBlockState(next);
                if (neighbor.getBlock() instanceof SteamVentBlock) {
                    if (neighbor.getValue(SteamVentBlock.PARTICLES_ENABLED) || SteamVentBlock.findSteamSource(level, next) != null) {
                        pressurized = true;
                    }
                } else if (neighbor.getBlock() instanceof SteamPipeBlock && visited.add(next)) {
                    queue.add(next);
                }
            }
        }
        return pressurized;
    }

    public static boolean connected(BlockState state, Direction direction) {
        return state.getValue(PROPERTY_BY_DIRECTION.get(direction)) != End.NONE;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    public enum End implements StringRepresentable {
        NONE("none"),
        PIPE("pipe"),
        FLANGE("flange");

        private final String name;

        End(String name) {
            this.name = name;
        }

        public static End of(boolean connected) {
            return connected ? PIPE : NONE;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}
