package net.satisfy.furniture.core.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.furniture.core.block.PouffeBlock;
import net.satisfy.furniture.core.block.SofaBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapWithCondition(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"))
    private <T extends ParticleOptions> boolean furniture$skipCushionLandingParticles(ServerLevel level, T particle, double x, double y, double z, int count, double dx, double dy, double dz, double speed, double fallY, boolean onGround, BlockState state, BlockPos pos) {
        return !(state.getBlock() instanceof SofaBlock || state.getBlock() instanceof PouffeBlock);
    }
}
