package com.cirox.client.mixin;

import com.cirox.client.CiroxClientMod;
import com.cirox.client.module.impl.AimAssistModule;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        AimAssistModule aimAssist = CiroxClientMod.getModuleManager().getModule(AimAssistModule.class);
        if (aimAssist != null && aimAssist.isEnabled()) {
            aimAssist.onTick();
        }
    }
}
