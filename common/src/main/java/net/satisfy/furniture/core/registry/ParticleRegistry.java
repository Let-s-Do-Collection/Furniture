package net.satisfy.furniture.core.registry;

import net.satisfy.furniture.Furniture;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;

public class ParticleRegistry {
    private static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Furniture.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<SimpleParticleType> GRAMOPHONE_NOTE = PARTICLE_TYPES.register("gramophone_note", () -> new SimpleParticleType(false) {});

    static {
        PARTICLE_TYPES.register();
    }
}
