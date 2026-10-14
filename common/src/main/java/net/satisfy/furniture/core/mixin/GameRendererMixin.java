package net.satisfy.furniture.core.mixin;

import net.satisfy.furniture.client.handler.TelescopeViewHandler;
import net.satisfy.furniture.client.render.block.MirrorRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void furniture$telescopeZoom(Camera camera, float partialTick, boolean useFovSetting, CallbackInfoReturnable<Double> cir) {
        if (useFovSetting && TelescopeViewHandler.isActive()) {
            cir.setReturnValue(cir.getReturnValue() * TelescopeViewHandler.getZoom(partialTick));
        }
    }

    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void furniture$hideHandInTelescope(CallbackInfo ci) {
        if (TelescopeViewHandler.isActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"))
    private void furniture$renderMirrors(DeltaTracker deltaTracker, CallbackInfo ci) {
        MirrorRenderer.renderReflections();
    }
}
