package com.berksire.furniture.core.registry;

import com.berksire.furniture.Furniture;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundRegistry {
    public static final Registrar<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Furniture.MOD_ID, Registries.SOUND_EVENT).getRegistrar();

    public static final RegistrySupplier<SoundEvent> GRANDFATHERS_CLOCK_TICKING = create("grandfathers_clock_ticking");
    public static final RegistrySupplier<SoundEvent> CABINET_OPEN = create("cabinet_open");
    public static final RegistrySupplier<SoundEvent> CABINET_CLOSE = create("cabinet_close");
    public static final RegistrySupplier<SoundEvent> COFFER_OPEN = create("coffer_open");
    public static final RegistrySupplier<SoundEvent> COFFER_CLOSE = create("coffer_close");
    public static final RegistrySupplier<SoundEvent> CPHS_PRIDE = create("cphs_pride");
    public static final RegistrySupplier<SoundEvent> LETSDO_THEME = create("letsdo_theme");

    private static RegistrySupplier<SoundEvent> create(String name) {
        final ResourceLocation id = Furniture.identifier(name);
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
