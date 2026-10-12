package com.cirox.client.mixin;

import com.cirox.client.CiroxClientMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null || player.isSpectator() || !player.isAlive()) return;

        // Validar filtro "Only Sword"
        if (CiroxClientMod.onlySword && !(player.getMainHandStack().getItem() instanceof SwordItem)) {
            return;
        }

        PlayerEntity target = findBestTarget(player, client);
        if (target == null) return;

        // Posición de los ojos para cálculo de ángulos exactos
        Vec3d eyePos = player.getEyePos();
        Vec3d targetEyePos = target.getEyePos();

        double dx = targetEyePos.x - eyePos.x;
        double dy = targetEyePos.y - eyePos.y;
        double dz = targetEyePos.z - eyePos.z;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        // Ángulos ideales calculados
        float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
        float targetPitch = (float) (-Math.toDegrees(Math.atan2(dy, horizontalDistance)));

        // Diferencia angular desde la posición actual del jugador
        float yawDelta = MathHelper.wrapDegrees(targetYaw - player.getYaw());
        float pitchDelta = MathHelper.wrapDegrees(targetPitch - player.getPitch());

        // Factor de suavizado (Speed) e imperfección humana estocástica
        float speed = Math.max(1.0f, CiroxClientMod.aimSpeed);
        float noiseYaw = (float) (ThreadLocalRandom.current().nextGaussian() * 0.08);
        float noisePitch = (float) (ThreadLocalRandom.current().nextGaussian() * 0.05);

        float yawStep = (yawDelta / speed) + noiseYaw;
        float pitchStep = (pitchDelta / speed) + noisePitch;

        // Aplicar rotación progresiva a la cámara
        player.setYaw(player.getYaw() + yawStep);
        player.setPitch(MathHelper.clamp(player.getPitch() + pitchStep, -90.0F, 90.0F));
    }

    private PlayerEntity findBestTarget(ClientPlayerEntity player, MinecraftClient client) {
        PlayerEntity bestTarget = null;
        double closestAngle = CiroxClientMod.aimFov;

        for (PlayerEntity otherPlayer : client.world.getPlayers()) {
            if (otherPlayer == player || !otherPlayer.isAlive() || otherPlayer.isSpectator()) continue;

            double distance = player.distanceTo(otherPlayer);
            if (distance > CiroxClientMod.aimDistance) continue;

            Vec3d eyePos = player.getEyePos();
            Vec3d targetEyePos = otherPlayer.getEyePos();

            double dx = targetEyePos.x - eyePos.x;
            double dz = targetEyePos.z - eyePos.z;
            float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
            float yawDelta = Math.abs(MathHelper.wrapDegrees(targetYaw - player.getYaw()));

            if (yawDelta <= closestAngle) {
                closestAngle = yawDelta;
                bestTarget = otherPlayer;
            }
        }
        return bestTarget;
    }
}
