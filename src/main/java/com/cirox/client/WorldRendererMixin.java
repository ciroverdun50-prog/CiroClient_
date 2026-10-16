package com.cirox.client.mixin;

import com.cirox.client.CiroxClientMod;
import com.cirox.client.module.PlayerESPModule;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo ci) {
        PlayerESPModule esp = CiroxClientMod.getModuleManager().getModule(PlayerESPModule.class);
        if (esp != null && esp.isEnabled()) {
            // Callback de renderizado cuando PlayerESP está activo
        }
    }
}
