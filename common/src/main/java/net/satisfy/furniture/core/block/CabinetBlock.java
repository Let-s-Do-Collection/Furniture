package net.satisfy.furniture.core.block;

import net.satisfy.furniture.core.registry.EntityTypeRegistry;
import net.satisfy.furniture.core.util.GeneralUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public class CabinetBlock extends net.satisfy.foundation.block.CabinetBlock {
    private final Supplier<SoundEvent> openSound;
    private final Supplier<SoundEvent> closeSound;

    public CabinetBlock(Properties settings, Supplier<SoundEvent> openSound, Supplier<SoundEvent> closeSound) {
        super(settings, () -> EntityTypeRegistry.CABINET_BLOCK_ENTITY.get());
        this.openSound = openSound;
        this.closeSound = closeSound;
    }

    @Override
    public void playSound(Level level, BlockPos pos, boolean open) {
        level.playSound(null, pos, open ? openSound.get() : closeSound.get(), SoundSource.BLOCKS, 1.0F, 1.1F);
    }

    private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.125, 1, 0.875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.875, 0, 1, 1, 1), BooleanOp.OR);
        return shape;
    };

    private static final Supplier<VoxelShape> openVoxelShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.125, 1, 0.875, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.875, 0, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0.4375, -0.125, 0.9375, 0.8125, 0.125), BooleanOp.OR);
        return shape;
    };

    public static final Map<Direction, VoxelShape> SHAPE = GeneralUtil.horizontalShapes(voxelShapeSupplier.get());

    public static final Map<Direction, VoxelShape> OPEN_SHAPE = GeneralUtil.horizontalShapes(openVoxelShapeSupplier.get());

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(OPEN) ? OPEN_SHAPE.get(state.getValue(FACING)) : SHAPE.get(state.getValue(FACING));
    }
}
