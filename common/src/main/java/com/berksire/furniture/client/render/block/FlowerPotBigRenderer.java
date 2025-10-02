package com.berksire.furniture.client.render.block;

import com.berksire.furniture.client.util.ClientUtil;
import com.berksire.furniture.core.block.entity.StorageBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;


public class FlowerPotBigRenderer implements StorageTypeRenderer {

    @Override
    public void render(StorageBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, NonNullList<ItemStack> itemStacks) {
        if (!itemStacks.isEmpty()) {
            ItemStack itemStack = itemStacks.get(0);
            if (itemStack.getItem() instanceof BlockItem blockItem) {
                BlockState state = blockItem.getBlock().defaultBlockState();
                matrices.translate(-0.5f, 0.4f, -0.5f);
                ClientUtil.renderBlock(state, matrices, vertexConsumers, entity);
                state = blockItem.getBlock().defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
                matrices.translate(0f, 1f, 0f);
                ClientUtil.renderBlock(state, matrices, vertexConsumers, entity);
            }
        }
    }
}
