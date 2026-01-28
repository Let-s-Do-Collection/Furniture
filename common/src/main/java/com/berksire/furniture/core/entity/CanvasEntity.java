package com.berksire.furniture.core.entity;

import com.berksire.furniture.core.registry.EntityTypeRegistry;
import com.berksire.furniture.core.registry.ObjectRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
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

    public static Optional<CanvasEntity> createCanvas(Level level, BlockPos blockPos, Direction direction, TagKey<PaintingVariant> variants, ResourceKey<PaintingVariant> defaultVariant) {
        CanvasEntity canvasEntity = new CanvasEntity(EntityTypeRegistry.CANVAS.get(), level);
        canvasEntity.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        canvasEntity.setDirection(direction);

        List<Holder<PaintingVariant>> possible = new ArrayList<>();
        level.registryAccess().registryOrThrow(Registries.PAINTING_VARIANT).getTagOrEmpty(variants).forEach(possible::add);

        if (possible.isEmpty()) {
            Optional<Holder.Reference<PaintingVariant>> fallback = level.registryAccess()
                    .registryOrThrow(Registries.PAINTING_VARIANT)
                    .getHolder(defaultVariant);
            if (fallback.isEmpty()) {
                return Optional.empty();
            }
            canvasEntity.setVariant(fallback.get());
            canvasEntity.fixPosition();
            return canvasEntity.survives() ? Optional.of(canvasEntity) : Optional.empty();
        }

        possible.removeIf(holder -> {
            canvasEntity.setVariant(holder);
            canvasEntity.fixPosition();
            return !canvasEntity.survives();
        });

        if (possible.isEmpty()) {
            Optional<Holder.Reference<PaintingVariant>> fallback = level.registryAccess()
                    .registryOrThrow(Registries.PAINTING_VARIANT)
                    .getHolder(defaultVariant);
            if (fallback.isEmpty()) {
                return Optional.empty();
            }
            canvasEntity.setVariant(fallback.get());
            canvasEntity.fixPosition();
            return canvasEntity.survives() ? Optional.of(canvasEntity) : Optional.empty();
        }

        Optional<Holder<PaintingVariant>> picked = Util.getRandomSafe(possible, canvasEntity.random);
        if (picked.isEmpty()) {
            return Optional.empty();
        }

        canvasEntity.setVariant(picked.get());
        canvasEntity.setDirection(direction);
        canvasEntity.fixPosition();
        return canvasEntity.survives() ? Optional.of(canvasEntity) : Optional.empty();
    }

    public void fixPosition() {
        this.recalculateBoundingBox();
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