package com.berksire.furniture;

import com.berksire.furniture.core.registry.*;
import com.google.common.reflect.Reflection;

public class Furniture {
    public static final String MODID = "furniture";

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
