package net.satisfy.furniture.core.block.entity;

import net.satisfy.furniture.core.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MirrorBlockEntity extends BlockEntity {
    public MirrorBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.MIRROR_BLOCK_ENTITY.get(), pos, state);
    }
}
