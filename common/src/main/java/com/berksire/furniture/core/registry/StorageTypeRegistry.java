package com.berksire.furniture.core.registry;

import com.berksire.furniture.Furniture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Set;


public class StorageTypeRegistry {
    public static final ResourceLocation FLOWER_POT_BIG = Furniture.identifier("flower_pot_big");
    public static final ResourceLocation FLOWER_BOX = Furniture.identifier("flower_box");

    public static Set<Block> registerBlocks(Set<Block> blocks) {
        blocks.add(ObjectRegistry.FLOWER_POT_BIG.get());
        blocks.add(ObjectRegistry.FLOWER_BOX.get());

        return blocks;
    }
}
