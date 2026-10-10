package net.satisfy.furniture.core.block.entity;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BoatInAJarBlockEntity extends BlockEntity {
    public static final int SHAKE_EVENT = 0;
    public static final int STORM_EVENT = 1;
    public static final int STORM_DURATION = 120;
    public static final float SHAKE_IMPULSE = 1.0F;
    private static final float DECAY = 0.96F;

    private float energy;
    private int stormTicks;

    public float renderEnergy;
    public float renderTime;
    public long lastRenderNanos;

    public BoatInAJarBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.BOAT_IN_A_JAR_BLOCK_ENTITY.get(), pos, state);
    }

    public float getEnergy() {
        return energy;
    }

    public static float breakEnergy() {
        return PlatformHelper.getBoatInAJarBreakThreshold();
    }

    public static float warnEnergy() {
        return breakEnergy() * 0.7F;
    }

    public static float maxEnergy() {
        return breakEnergy() + 1.0F;
    }

    public float shake() {
        energy = Math.min(maxEnergy(), energy * DECAY + SHAKE_IMPULSE);
        return energy;
    }

    public boolean isStorming() {
        return stormTicks > 0;
    }

    public void startStorm() {
        stormTicks = STORM_DURATION;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BoatInAJarBlockEntity be) {
        be.energy = be.energy < 0.01F ? 0.0F : be.energy * DECAY;
        if (be.stormTicks <= 0) {
            return;
        }
        be.stormTicks--;
        be.energy = Math.max(be.energy, warnEnergy() * (0.6F + level.random.nextFloat() * 0.4F));
        if (!level.isClientSide) {
            return;
        }
        RandomSource random = level.random;
        double x = pos.getX() + 0.2 + random.nextDouble() * 0.6;
        double z = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
        level.addParticle(ParticleTypes.FALLING_WATER, x, pos.getY() + 0.75, z, 0.0, 0.0, 0.0);
        if (random.nextInt(4) == 0) {
            level.addParticle(ParticleTypes.SPLASH, x, pos.getY() + 0.35, z, 0.0, 0.0, 0.0);
        }
        if (random.nextInt(30) == 0) {
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 0.0, -0.1, 0.0);
            level.playLocalSound(pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.BLOCKS, 0.08F, 1.8F, false);
        }
    }

    @Override
    public boolean triggerEvent(int id, int param) {
        if (id == SHAKE_EVENT) {
            energy = param / 100.0F;
            return true;
        }
        if (id == STORM_EVENT) {
            startStorm();
            return true;
        }
        return super.triggerEvent(id, param);
    }
}
