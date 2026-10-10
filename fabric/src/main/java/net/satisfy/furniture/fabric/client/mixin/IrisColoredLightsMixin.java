package net.satisfy.furniture.fabric.client.mixin;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ChunkBuilderMeshingTask.class, priority = 1500)
public class IrisColoredLightsMixin {
}
