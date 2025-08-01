package com.berksire.furniture.mixin;

import com.berksire.furniture.registry.ObjectRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CartographyTableBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CartographyTableMenu.class)
public class CartographyTableMenuMixin {
    @Shadow @Final private ContainerLevelAccess access;

    @Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
    public void furniture$stillValid(Player player, CallbackInfoReturnable<Boolean> cir) {
        boolean isValid = this.access.evaluate((world, pos) -> {
            Block block = world.getBlockState(pos).getBlock();
            return world.getBlockState(pos).is(ObjectRegistry.EXPLORERS_BOX.get()) || block instanceof CartographyTableBlock;
        }, true);
        cir.setReturnValue(isValid);
    }
}
