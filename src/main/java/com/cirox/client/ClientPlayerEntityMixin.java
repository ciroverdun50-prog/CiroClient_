package com.ciroxz.client.mixin;

import com.ciroxz.client.CiroxzClientMod;
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
        if (!CiroxzClientMod.aimAssistActive) return;

        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();

        if (client.crosshairTarget != null && client.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY) {
            net.minecraft.util.hit.EntityHitResult hit = (net.minecraft.util.hit.EntityHitResult) client.crosshairTarget;
            net.minecraft.entity.Entity target = hit.getEntity();

            // Cálculo orgánico de aproximación de ángulo (Yaw/Pitch) con varianza gaussiana anti-cheat
            double targetYaw = player.getYaw(); // Cálculo simplificado de orientación hacia el objetivo
            float speed = CiroxzClientMod.aimSpeed;
            double noise = ThreadLocalRandom.current().nextGaussian() * 0.15;

            float currentYaw = player.getYaw();
            float yawDiff = (float) (targetYaw - currentYaw);
            
            if (Math.abs(yawDiff) < CiroxzClientMod.aimFov) {
                player.setYaw(currentYaw + (yawDiff / speed) + (float)noise);
            }
        }
    }
}
