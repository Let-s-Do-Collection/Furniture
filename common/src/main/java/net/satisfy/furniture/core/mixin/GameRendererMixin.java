package net.satisfy.furniture.core.mixin;

import net.satisfy.furniture.client.handler.TelescopeViewHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void furniture$telescopeZoom(Camera camera, float partialTick, boolean useFovSetting, CallbackInfoReturnable<Double> cir) {
        if (useFovSetting && TelescopeViewHandler.isActive()) {
            cir.setReturnValue(cir.getReturnValue() * TelescopeViewHandler.getZoom(partialTick));
        }
    }
}
