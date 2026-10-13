package net.satisfy.furniture.core.mixin;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.satisfy.furniture.client.handler.TelescopeViewHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {
    @Inject(method = "tick", at = @At("TAIL"))
    private void furniture$lockMovementInTelescope(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        if (TelescopeViewHandler.isActive()) {
            this.up = false;
            this.down = false;
            this.left = false;
            this.right = false;
            this.jumping = false;
            this.forwardImpulse = 0.0F;
            this.leftImpulse = 0.0F;
        }
    }
}
