package com.berksire.furniture.item;

import com.berksire.furniture.client.entity.CanvasEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class CanvasItem extends HangingEntityItem {
    private final Supplier<PaintingVariant> defaultVariant;
    private final TagKey<PaintingVariant> variants;

    public CanvasItem(Properties settings, Supplier<PaintingVariant> defaultVariant, TagKey<PaintingVariant> variants) {
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

        Optional<CanvasEntity> optional = create(level, pos2, direction);
        if (optional.isEmpty()) {
            return InteractionResult.CONSUME;
        }
        CanvasEntity painting = optional.get();

        // TODO fixme
        /*
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            EntityType.updateCustomEntityTag(level, player, painting, tag);
        }*/
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

    public Optional<CanvasEntity> create(Level level, BlockPos pos, Direction direction) {
        // TODO fixme
        /*
       CanvasEntity painting = new CanvasEntity(level, pos, direction, BuiltInRegistries.PAINTING_VARIANT.wrapAsHolder(defaultVariant.get()));
        List<Holder<PaintingVariant>> list = new ArrayList<>();
        BuiltInRegistries.PAINTING_VARIANT.getTagOrEmpty(variants).forEach(list::add);
        if (!list.isEmpty()) {
            list.removeIf((holder) -> {
                painting.setVariant(holder);
                return !painting.survives();
            });
            if (!list.isEmpty()) {
                int max = list.stream().mapToInt(CanvasItem::variantArea).max().orElse(0);
                list.removeIf(holder -> variantArea(holder) < max);
                return Util.getRandomSafe(list, level.getRandom()).map(holder -> {
                    painting.setVariant(holder);
                    return painting;
                });
            }
        }*/
        return Optional.empty();
    }

    private static int variantArea(Holder<PaintingVariant> variant) {
        return variant.value().area();
    }
}
