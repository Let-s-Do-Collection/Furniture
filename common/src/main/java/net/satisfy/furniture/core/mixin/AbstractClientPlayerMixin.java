package net.satisfy.furniture.core.mixin;

import net.satisfy.furniture.client.handler.TelescopeViewHandler;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {
    @Inject(method = "getFieldOfViewModifier", at = @At("HEAD"), cancellable = true)
    private void furniture$telescopeZoom(CallbackInfoReturnable<Float> cir) {
        if ((Object) this instanceof LocalPlayer && TelescopeViewHandler.isActive()) {
            cir.setReturnValue(1.0F);
        }
    }
}
