package com.berksire.furniture.core.item;

import com.berksire.furniture.core.entity.CanvasEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CanvasItem extends HangingEntityItem {
    private final ResourceKey<PaintingVariant> defaultVariant;
    private final TagKey<PaintingVariant> variants;

    public CanvasItem(Properties settings, ResourceKey<PaintingVariant> defaultVariant, TagKey<PaintingVariant> variants) {
        super(EntityType.PAINTING, settings);
        this.defaultVariant = defaultVariant;
        this.variants = variants;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        if (direction.getAxis().isVertical()) {
            return InteractionResult.FAIL;
        }

        BlockPos placePos = clickedPos.relative(direction);
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();

        if (player != null && !mayPlace(player, direction, stack, placePos)) {
            return InteractionResult.FAIL;
        }

        Optional<CanvasEntity> optional = CanvasEntity.createCanvas(level, placePos, direction, this.variants, this.defaultVariant);
        if (optional.isEmpty()) {
            return InteractionResult.FAIL;
        }

        CanvasEntity canvasEntity = optional.get();

        CustomData customData = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
        EntityType.updateCustomEntityTag(level, player, canvasEntity, customData);

        canvasEntity.fixPosition();
        if (!canvasEntity.survives()) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide) {
            canvasEntity.playPlacementSound();
            level.gameEvent(player, GameEvent.ENTITY_PLACE, canvasEntity.blockPosition());
            level.addFreshEntity(canvasEntity);
            stack.shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}