package com.cirox.client.mixin;

import com.cirox.client.CiroxClientMod;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
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
            
            if (hit.getEntity() instanceof PlayerEntity targetPlayer && targetPlayer != player) {
                // Cálculo de rotación suave (Interpolación lineal + Ruido gaussiano para burlar Grim/Vulcan Angle checks)
                double dx = targetPlayer.getX() - player.getX();
                double dz = targetPlayer.getZ() - player.getZ();
                float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);

                float currentYaw = player.getYaw();
                float yawDiff = Math.wrapDegrees(targetYaw - currentYaw);

                if (Math.abs(yawDiff) < CiroxClientMod.aimFov) {
                    float speed = CiroxClientMod.aimSpeed;
                    float step = Math.min(Math.abs(yawDiff), Math.max(0.5f, Math.abs(yawDiff) / speed));
                    float noise = (float) (ThreadLocalRandom.current().nextGaussian() * 0.1);
                    
                    player.setYaw(currentYaw + (Math.signum(yawDiff) * step) + noise);
                }
            }
        }
    }
}
