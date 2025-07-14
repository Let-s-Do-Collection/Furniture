package com.berksire.furniture.client.entity;

import com.berksire.furniture.registry.EntityTypeRegistry;
import com.berksire.furniture.registry.ObjectRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CanvasEntity extends Painting {

    public CanvasEntity(EntityType<? extends Painting> type, Level level) {
        super(type, level);
    }

    public CanvasEntity(Level level, BlockPos blockPos) {
        super(level, blockPos);
    }

    public CanvasEntity(Level level, BlockPos pos, Direction direction, Holder<PaintingVariant> variant) {
        super(EntityTypeRegistry.CANVAS.get(), level);
        this.setPos(pos.getX(), pos.getY(), pos.getZ());
        this.setDirection(direction);
        this.setVariant(variant);
        this.fixPosition();
    }

    public static Optional<CanvasEntity> createCanvas(Level level, BlockPos blockPos, Direction direction) {
        CanvasEntity canvasEntity = new CanvasEntity(level, blockPos);
        List<Holder<PaintingVariant>> list = new ArrayList();
        level.registryAccess().registryOrThrow(Registries.PAINTING_VARIANT).getTagOrEmpty(PaintingVariantTags.PLACEABLE).forEach(list::add);
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            canvasEntity.setDirection(direction);
            list.removeIf((holder) -> {
                canvasEntity.setVariant(holder);
                return !canvasEntity.survives();
            });
            if (list.isEmpty()) {
                return Optional.empty();
            } else {
                int i = list.stream().mapToInt(CanvasEntity::variantArea).max().orElse(0);
                list.removeIf((holder) -> {
                    return variantArea(holder) < i;
                });
                Optional<Holder<PaintingVariant>> optional = Util.getRandomSafe(list, canvasEntity.random);
                if (optional.isEmpty()) {
                    return Optional.empty();
                } else {
                    canvasEntity.setVariant((Holder)optional.get());
                    canvasEntity.setDirection(direction);
                    return Optional.of(canvasEntity);
                }
            }
        }
    }

    private void fixPosition() {
        this.setPos(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public ItemEntity spawnAtLocation(ItemLike item) {
        return super.spawnAtLocation(ObjectRegistry.CANVAS.get());
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ObjectRegistry.CANVAS.get());
    }

    private static int variantArea(Holder<PaintingVariant> variant) {
        return variant.value().area();
    }
}