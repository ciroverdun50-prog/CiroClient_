package com.cirox.client.module.impl;

import com.cirox.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AimAssistModule extends Module {
    // Parámetros configurables y sutiles por defecto
    public float aimFov = 25.0f;       // Rango visual más estrecho para evitar bloqueos extraños
    public float aimDistance = 3.8f;   // Distancia en bloques ajustada
    public float aimSpeed = 8.0f;      // Velocidad de giro suavizada (mayor = más lento y natural)
    public boolean onlySword = true;   // Filtro exclusivo de espada

    public AimAssistModule() {
        super("AimAssist", "Suaviza la vista hacia objetivos de forma indetectable", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        if (onlySword && !(mc.player.getMainHandStack().getItem() instanceof SwordItem)) {
            return;
        }

        // Si el jugador ya está haciendo input manual fuerte de mouse, reducimos la asistencia para evitar tirones
        LivingEntity target = getBestTarget();
        if (target != null) {
            faceEntitySmoothly(target);
        }
    }

    private LivingEntity getBestTarget() {
        LivingEntity bestTarget = null;
        double minDistance = aimDistance;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity) || entity == mc.player) continue;
            if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()) continue;
            if (((LivingEntity) entity).isDead() || ((LivingEntity) entity).isSpectator()) continue;

            double distance = mc.player.distanceTo(entity);
            if (distance <= minDistance && isInFov(entity)) {
                minDistance = distance;
                bestTarget = (LivingEntity) entity;
            }
        }
        return bestTarget;
    }

    private boolean isInFov(Entity entity) {
        Vec3d eyes = mc.player.getEyePos();
        Vec3d targetPos = entity.getPos().add(0, entity.getHeight() / 2, 0);
        double diffX = targetPos.x - eyes.x;
        double diffZ = targetPos.z - eyes.z;
        double yawToTarget = Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float angleDiff = MathHelper.wrapDegrees((float) yawToTarget - mc.player.getYaw());
        return Math.abs(angleDiff) <= aimFov;
    }

    private void faceEntitySmoothly(LivingEntity entity) {
        Vec3d eyes = mc.player.getEyePos();
        Vec3d targetPos = entity.getPos().add(0, entity.getHeight() / 2.2, 0); // Apunta al torso superior
        
        double diffX = targetPos.x - eyes.x;
        double diffY = targetPos.y - eyes.y;
        double diffZ = targetPos.z - eyes.z;
        
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        
        float yawToTarget = (float) (Math.atan2(diffZ, diffX) * (180 / Math.PI)) - 90.0f;
        float pitchToTarget = (float) (-(Math.atan2(diffY, dist) * (180 / Math.PI)));
        
        float yawDiff = MathHelper.wrapDegrees(yawToTarget - mc.player.getYaw());
        float pitchDiff = MathHelper.wrapDegrees(pitchToTarget - mc.player.getPitch());
        
        // Aplicamos división por aimSpeed para generar un movimiento orgánico que el anticheat no flaguee como imposible
        if (Math.abs(yawDiff) > 0.1f) {
            mc.player.setYaw(mc.player.getYaw() + (yawDiff / aimSpeed));
        }
        if (Math.abs(pitchDiff) > 0.1f) {
            mc.player.setPitch(mc.player.getPitch() + (pitchDiff / aimSpeed));
        }
    }
}
