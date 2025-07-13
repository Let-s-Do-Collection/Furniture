package com.berksire.furniture.mixin;

import com.berksire.furniture.registry.CanvasRegistry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.decoration.PaintingVariants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PaintingVariants.class)
public abstract class PaintingVariantsMixin {

    @Shadow
    private static void register(BootstrapContext<PaintingVariant> bootstrapContext, ResourceKey<PaintingVariant> resourceKey, int i, int j) {
    }

    @Inject(method = "bootstrap", at = @At("RETURN"))
    private static void furniture$registerPainting(BootstrapContext<PaintingVariant> bootstrapContext, CallbackInfo ci) {
        register(bootstrapContext, CanvasRegistry.LONELY_DAISY, 16, 16);
        register(bootstrapContext, CanvasRegistry.SUNFLOWER, 16, 32);
        register(bootstrapContext, CanvasRegistry.FORAGING_WOODPECKER, 32, 64);
        register(bootstrapContext, CanvasRegistry.SLEEPING_FOX, 6, 48);
        register(bootstrapContext, CanvasRegistry.HONEY_FALL, 16, 64);
        register(bootstrapContext, CanvasRegistry.RABBIT, 32, 16);
        register(bootstrapContext, CanvasRegistry.LAVENDER_FIELDS, 32, 32);
        register(bootstrapContext, CanvasRegistry.ROSES, 32, 48);
        register(bootstrapContext, CanvasRegistry.ANTS, 48, 16);
        register(bootstrapContext, CanvasRegistry.BUTTERFLY, 48, 32);
        register(bootstrapContext, CanvasRegistry.WATERFALL, 48, 64);
        register(bootstrapContext, CanvasRegistry.SITTING_BEAR, 48, 48);
        register(bootstrapContext, CanvasRegistry.STRONG, 64, 16);
        register(bootstrapContext, CanvasRegistry.SAKURA_GROVE, 64, 32);
        register(bootstrapContext, CanvasRegistry.TULIP_FIELDS, 64, 48);
        register(bootstrapContext, CanvasRegistry.HOPPER, 64, 64);
    }
}
