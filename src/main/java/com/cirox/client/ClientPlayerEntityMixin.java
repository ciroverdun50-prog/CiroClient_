package com.cirox.client.mixin;

import com.cirox.client.CiroxClientMod;
import com.cirox.client.module.PlayerESPModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void onIsGlowing(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof PlayerEntity && entity != net.minecraft.client.MinecraftClient.getInstance().player) {
            PlayerESPModule esp = CiroxClientMod.getModuleManager().getModule(PlayerESPModule.class);
            if (esp != null && esp.isEnabled()) {
                cir.setReturnValue(true);
            }
        }
    }
}
