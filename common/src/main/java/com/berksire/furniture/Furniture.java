package com.berksire.furniture;

import com.berksire.furniture.core.registry.*;
import com.google.common.reflect.Reflection;
import net.minecraft.resources.ResourceLocation;

public class Furniture {
    public static final String MOD_ID = "furniture";

    public static ResourceLocation identifier(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static void init() {
        Reflection.initialize(
                TagRegistry.class,
                ObjectRegistry.class,
                EntityTypeRegistry.class,
                TabRegistry.class,
                SoundRegistry.class,
                JukeboxSongRegistry.class
        );
    }

    public static void commonSetup() {
        Reflection.initialize(
                FlammableBlockRegistry.class
        );
    }
}
