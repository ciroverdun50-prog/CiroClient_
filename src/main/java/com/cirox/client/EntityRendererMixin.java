package com.cirox.client;

import com.cirox.client.module.PlayerESPModule;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void onHasOutline(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof PlayerEntity && entity != net.minecraft.client.MinecraftClient.getInstance().player) {
            PlayerESPModule esp = CiroxClientMod.getModuleManager() != null ? CiroxClientMod.getModuleManager().getModule(PlayerESPModule.class) : null;
            if (esp != null && esp.isEnabled()) {
                cir.setReturnValue(true);
            }
        }
    }
}
