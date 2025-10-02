package com.berksire.furniture.core.registry;

import com.berksire.furniture.Furniture;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class CanvasRegistry {

    public static final ResourceKey<PaintingVariant> LONELY_DAISY = create("lonely_daisy");
    public static final ResourceKey<PaintingVariant> SUNFLOWER = create("sunflower");
    public static final ResourceKey<PaintingVariant> FORAGING_WOODPECKER = create("foraging_woodpecker");
    public static final ResourceKey<PaintingVariant> SLEEPING_FOX = create("sleeping_fox");
    public static final ResourceKey<PaintingVariant> HONEY_FALL = create("honey_fall");
    public static final ResourceKey<PaintingVariant> RABBIT = create("rabbit");
    public static final ResourceKey<PaintingVariant> LAVENDER_FIELDS = create("lavender_fields");
    public static final ResourceKey<PaintingVariant> ROSES = create("roses");
    public static final ResourceKey<PaintingVariant> ANTS = create("ants");
    public static final ResourceKey<PaintingVariant> BUTTERFLY = create("butterfly");
    public static final ResourceKey<PaintingVariant> WATERFALL = create("waterfall");
    public static final ResourceKey<PaintingVariant> SITTING_BEAR = create("sitting_bear");
    public static final ResourceKey<PaintingVariant> STRONG = create("strong");
    public static final ResourceKey<PaintingVariant> SAKURA_GROVE = create("sakura_grove");
    public static final ResourceKey<PaintingVariant> TULIP_FIELDS = create("tulip_fields");
    public static final ResourceKey<PaintingVariant> HOPPER = create("hopper");

    private static ResourceKey<PaintingVariant> create(String string) {
        return ResourceKey.create(Registries.PAINTING_VARIANT, Furniture.identifier(string));
    }
}
