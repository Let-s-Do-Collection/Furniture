package net.satisfy.furniture.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.satisfy.furniture.core.block.GramophoneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxSongPlayer.class)
public class JukeboxSongPlayerMixin {
    @Inject(method = "spawnMusicParticles", at = @At("HEAD"), cancellable = true)
    private static void furniture$spawnMusicParticles(LevelAccessor level, BlockPos pos, CallbackInfo ci) {
        if (level.getBlockState(pos).getBlock() instanceof GramophoneBlock) {
            ci.cancel();
        }
    }
}
