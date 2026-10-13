package net.satisfy.furniture.core.block.entity;

import net.satisfy.furniture.core.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ClockBlockEntity extends BlockEntity {
    public ClockBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.CLOCK_BLOCK_ENTITY.get(), pos, state);
    }
}
