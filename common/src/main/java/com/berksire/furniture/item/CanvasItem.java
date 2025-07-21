package com.berksire.furniture.item;

import com.berksire.furniture.client.entity.CanvasEntity;
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
        BlockPos pos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos pos2 = pos.relative(direction);
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        if (player != null && !mayPlace(player, direction, stack, pos2)) {
            return InteractionResult.FAIL;
        }

        Optional<CanvasEntity> optional = CanvasEntity.createCanvas(level, pos2, direction);
        if (optional.isEmpty()) {
            return InteractionResult.CONSUME;
        }
        CanvasEntity painting = optional.get();

        CustomData customData = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
        if (customData != null) {
            EntityType.updateCustomEntityTag(level, player, painting, customData);
        }
        if (painting.survives()) {
            if (!level.isClientSide) {
                painting.playPlacementSound();
                level.gameEvent(player, GameEvent.ENTITY_PLACE, painting.blockPosition());
                level.addFreshEntity(painting);
            }
            stack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.CONSUME;
    }
}
