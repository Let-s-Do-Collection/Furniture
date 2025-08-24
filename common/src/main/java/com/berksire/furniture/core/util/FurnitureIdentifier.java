package com.berksire.furniture.core.util;

import com.berksire.furniture.Furniture;
import net.minecraft.resources.ResourceLocation;

public class FurnitureIdentifier {

    public static ResourceLocation parseIdentifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(Furniture.MODID, path);
    }
}
