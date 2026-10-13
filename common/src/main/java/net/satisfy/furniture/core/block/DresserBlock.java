package net.satisfy.furniture.core.block;

import net.satisfy.furniture.core.registry.EntityTypeRegistry;
import net.satisfy.furniture.core.util.GeneralUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.block.LineConnectingType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class DresserBlock extends net.satisfy.foundation.block.DresserBlock {
    private static final Map<LineConnectingType, Supplier<VoxelShape>> SHAPES_SUPPLIERS = Map.of(
            LineConnectingType.NONE, DresserBlock::makeSingleShape,
            LineConnectingType.MIDDLE, DresserBlock::makeMiddleShape,
            LineConnectingType.RIGHT, DresserBlock::makeRightShape,
            LineConnectingType.LEFT, DresserBlock::makeLeftShape);
    private static final Map<Direction, Map<LineConnectingType, VoxelShape>> SHAPES = new EnumMap<>(Direction.class);

    public DresserBlock(Properties settings, Supplier<SoundEvent> openSound, Supplier<SoundEvent> closeSound) {
        super(settings, () -> EntityTypeRegistry.DRESSER_BLOCK_ENTITY.get(), openSound, closeSound);
    }

    @Override
    protected boolean connectsTo(BlockState neighbour) {
        return neighbour.getBlock() instanceof DeskBlock;
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING)).get(state.getValue(TYPE));
    }

    private static VoxelShape makeSingleShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.25, 0.1875, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.0625, 0.9375, 0.1875, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.75, 0.25, 0.1875, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.75, 0.9375, 0.1875, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.8125, 0, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.1875, 0.0625, 0.9375, 0.8125, 0.9375), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeRightShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.8125, 0, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.0625, 0.9375, 0.8125, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.0625, 0.9375, 0.1875, 0.25), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.75, 0, 0.75, 0.9375, 0.1875, 0.9375), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeLeftShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.8125, 0, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.1875, 0.0625, 1, 0.8125, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.75, 0.25, 0.1875, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.25, 0.1875, 0.25), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeMiddleShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.1875, 0.0625, 1, 0.8125, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.8125, 0, 1, 1, 1), BooleanOp.OR);
        return shape;
    }

    static {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            Map<LineConnectingType, VoxelShape> rotated = new EnumMap<>(LineConnectingType.class);
            SHAPES_SUPPLIERS.forEach((type, shape) -> rotated.put(type, GeneralUtil.rotateShape(Direction.NORTH, direction, shape.get())));
            SHAPES.put(direction, rotated);
        }
    }
}
