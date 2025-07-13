package com.berksire.furniture.fabric.client.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.berksire.furniture.registry.ObjectRegistry;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

// TODO Waiting for updated
/// Optional mixin to support colored lights when using Iris. Selects the most similar
/// vanilla blocks, so it is up to the shader to decide what color intensity
@Mixin(value = ChunkBuilderMeshingTask.class, priority = 1500)
public class IrisColoredLightsMixin {

    /*
    @TargetHandler(
            mixin = "net.irisshaders.iris.compat.sodium.mixin.block_id.MixinChunkRenderRebuildTask",
            name = "iris$setLocalPos"
    )
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(
            value = "INVOKE",
            target = "Lnet/irisshaders/iris/compat/sodium/impl/block_context/ChunkBuildBuffersExt;iris$setMaterialId(Lnet/minecraft/world/level/block/state/BlockState;SB)V"
    ))
    private void setLocalPos(ChunkBuildBuffersExt instance, BlockState blockState, short someOtherVar, byte emission, Operation<Void> operation) {
        furniture$shamBlockId(instance, blockState, someOtherVar, emission, operation);
    }

    @TargetHandler(
            mixin = "net.irisshaders.iris.compat.sodium.mixin.block_id.MixinChunkRenderRebuildTask",
            name = "iris$wrapGetBlockLayer"
    )
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(
            value = "INVOKE",
            target = "Lnet/irisshaders/iris/compat/sodium/impl/block_context/ChunkBuildBuffersExt;iris$setMaterialId(Lnet/minecraft/world/level/block/state/BlockState;SB)V"
    ))
    private void onRenderModel(ChunkBuildBuffersExt instance, BlockState blockState, short someOtherVar, byte emission, Operation<Void> operation) {
        furniture$shamBlockId(instance, blockState, someOtherVar, emission, operation);
    }

    @Unique
    private void furniture$shamBlockId(ChunkBuildBuffersExt instance, BlockState blockState, short someOtherVar, byte emission, Operation<Void> operation) {
        BlockState original = blockState;
        if (blockState.is(ObjectRegistry.STREET_LANTERN.get()) || blockState.is(ObjectRegistry.STREET_WALL_LANTERN.get())) {
            original = Blocks.OCHRE_FROGLIGHT.defaultBlockState();
        } else if (blockState.is(ObjectRegistry.PLATED_STREET_LANTERN.get()) || blockState.is(ObjectRegistry.PLATED_STREET_WALL_LANTERN.get())) {
            original = Blocks.PEARLESCENT_FROGLIGHT.defaultBlockState();
        }
        operation.call(instance, original, someOtherVar, emission);
    }*/
}
