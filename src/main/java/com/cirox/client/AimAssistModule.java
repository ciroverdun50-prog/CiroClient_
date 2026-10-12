package com.cirox.client.module.impl;

import com.cirox.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AimAssistModule extends Module {
    public float aimFov = 25.0f;       // FOV angular
    public float aimDistance = 3.8f;   // Rango en bloques
    public float aimSpeed = 6.0f;      // Factor de suavizado (mayor = más suave)
    public boolean onlySword = true;   // Filtro de espada

    public AimAssistModule() {
        super("AimAssist", "Suaviza la vista hacia objetivos de forma indetectable", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        if (onlySword && !(mc.player.getMainHandStack().getItem() instanceof SwordItem)) {
            return;
        }

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
        Vec3d targetPos = entity.getPos().add(0, entity.getHeight() / 2.2, 0);
        
        double diffX = targetPos.x - eyes.x;
        double diffY = targetPos.y - eyes.y;
        double diffZ = targetPos.z - eyes.z;
        
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        
        float yawToTarget = (float) (Math.atan2(diffZ, diffX) * (180 / Math.PI)) - 90.0f;
        float pitchToTarget = (float) (-(Math.atan2(diffY, dist) * (180 / Math.PI)));
        
        float yawDiff = MathHelper.wrapDegrees(yawToTarget - mc.player.getYaw());
        float pitchDiff = MathHelper.wrapDegrees(pitchToTarget - mc.player.getPitch());
        
        // Limitamos el cambio máximo por tick (delta capping) para evitar flags de anticheat por movimientos ilegales
        float maxDelta = 25.0f / Math.max(1.0f, aimSpeed);
        yawDiff = MathHelper.clamp(yawDiff, -maxDelta, maxDelta);
        pitchDiff = MathHelper.clamp(pitchDiff, -maxDelta, maxDelta);

        mc.player.setYaw(mc.player.getYaw() + yawDiff);
        mc.player.setPitch(mc.player.getPitch() + pitchDiff);
    }
}
