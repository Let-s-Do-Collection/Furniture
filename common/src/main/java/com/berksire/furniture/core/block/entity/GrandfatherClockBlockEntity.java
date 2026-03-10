package com.berksire.furniture.core.block.entity;

import com.berksire.furniture.core.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class GrandfatherClockBlockEntity extends BlockEntity {
    public GrandfatherClockBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.GRANDFATHER_CLOCK_BLOCK_ENTITY.get(), pos, state);
    }
}