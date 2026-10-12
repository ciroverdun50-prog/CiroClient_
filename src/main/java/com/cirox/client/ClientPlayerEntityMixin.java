package com.cirox.client.mixin;

import com.cirox.client.CiroxClientMod;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!CiroxClientMod.aimAssistActive) return;

        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();

        if (client.crosshairTarget != null && client.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY) {
            net.minecraft.util.hit.EntityHitResult hit = (net.minecraft.util.hit.EntityHitResult) client.crosshairTarget;
            
            double targetYaw = player.getYaw();
            float speed = CiroxClientMod.aimSpeed;
            double noise = ThreadLocalRandom.current().nextGaussian() * 0.12;

            float currentYaw = player.getYaw();
            float yawDiff = (float) (targetYaw - currentYaw);
            
            if (Math.abs(yawDiff) < CiroxClientMod.aimFov) {
                player.setYaw(currentYaw + (yawDiff / speed) + (float)noise);
            }
        }
    }
}
