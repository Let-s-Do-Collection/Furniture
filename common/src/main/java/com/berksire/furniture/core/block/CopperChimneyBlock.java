package com.berksire.furniture.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class CopperChimneyBlock extends ChimneyBlock{
    private static final VoxelShape SHAPE = Shapes.or(box(4, 0, 4, 12, 3, 12), box(5, 3, 5, 11, 16, 11), box(4, 12, 4, 12, 15, 12));

    public CopperChimneyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        return SHAPE;
    }
}
