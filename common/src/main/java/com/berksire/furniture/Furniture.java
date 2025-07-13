package com.berksire.furniture;

import com.berksire.furniture.registry.*;
import com.google.common.reflect.Reflection;

public class Furniture {
    public static final String MODID = "furniture";

    public static void init() {
        Reflection.initialize(
                ObjectRegistry.class,
                EntityTypeRegistry.class,
                // TODO fixme
                /*CanvasRegistry.class,*/
                TabRegistry.class,
                SoundRegistry.class
        );
    }

    public static void commonSetup() {
        Reflection.initialize(
                FlammableBlockRegistry.class
        );
    }
}
