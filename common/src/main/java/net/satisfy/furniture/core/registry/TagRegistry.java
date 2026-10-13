package net.satisfy.furniture.core.registry;

import net.minecraft.world.level.block.Block;
import net.satisfy.furniture.Furniture;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Item;

public final class TagRegistry {
    public static final TagKey<PaintingVariant> PAINTINGS = paintingTag();
    public static final TagKey<Item> TRASH_BAG_BLACKLIST = itemTag();
    public static final TagKey<Item> CASH_REGISTER_VALUABLES = TagKey.create(Registries.ITEM, Furniture.identifier("cash_register_valuables"));
    public static final TagKey<Block> STEAM_SOURCES = TagKey.create(Registries.BLOCK, Furniture.identifier("steam_sources"));

    private static TagKey<PaintingVariant> paintingTag() {
        return TagKey.create(Registries.PAINTING_VARIANT, Furniture.identifier("paintings"));
    }

    private static TagKey<Item> itemTag() {
        return TagKey.create(Registries.ITEM, Furniture.identifier("trash_bag_blacklist"));
    }
}
