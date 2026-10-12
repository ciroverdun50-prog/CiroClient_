package com.cirox.client.module;

import com.cirox.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import java.util.Random;

public class AimAssistModule extends Module {
    public float aimFov = 25.0f;
    public float aimDistance = 3.8f;
    public float aimSpeed = 6.0f;
    public boolean onlySword = true;

    private final Random random = new Random();

    public AimAssistModule() {
        super("AimAssist", "Suaviza la vista simulando movimiento de mouse real", Category.COMBAT);
        setKey(0); // 0 = Sin tecla asignada por defecto
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        if (onlySword && !(mc.player.getMainHandStack().getItem() instanceof SwordItem)) {
            return;
        }

        LivingEntity target = getBestTarget();
        if (target != null) {
            faceEntityHumanized(target);
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

    private void faceEntityHumanized(LivingEntity entity) {
        Vec3d eyes = mc.player.getEyePos();
        // Offset dinámico para no apuntar siempre al centro exacto del Hitbox (evita flags de inconsistencia)
        double heightOffset = entity.getHeight() * (0.5 + (random.nextDouble() * 0.1 - 0.05));
        Vec3d targetPos = entity.getPos().add(0, heightOffset, 0);
        
        double diffX = targetPos.x - eyes.x;
        double diffY = targetPos.y - eyes.y;
        double diffZ = targetPos.z - eyes.z;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        
        float targetYaw = (float) (Math.atan2(diffZ, diffX) * (180 / Math.PI)) - 90.0f;
        float targetPitch = (float) (-(Math.atan2(diffY, dist) * (180 / Math.PI)));
        
        float yawDiff = MathHelper.wrapDegrees(targetYaw - mc.player.getYaw());
        float pitchDiff = MathHelper.wrapDegrees(targetPitch - mc.player.getPitch());

        // Aplicar constante del GCD del mouse de Minecraft para bypass de anticheat
        double sensitivity = mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
        double gcd = sensitivity * sensitivity * sensitivity * 8.0 * 0.15;

        // Suavizado exponencial con micro-variación (Jitter orgánico)
        float speedFactor = (float) (aimSpeed + (random.nextDouble() * 0.4 - 0.2));
        float deltaYaw = yawDiff / speedFactor;
        float deltaPitch = pitchDiff / speedFactor;

        // Ajuste de GCD (Gears/Mouse step alignment)
        deltaYaw = (float) (Math.round(deltaYaw / gcd) * gcd);
        deltaPitch = (float) (Math.round(deltaPitch / gcd) * gcd);

        if (Math.abs(yawDiff) > 0.05f) {
            mc.player.setYaw(mc.player.getYaw() + deltaYaw);
        }
        if (Math.abs(pitchDiff) > 0.05f) {
            mc.player.setPitch(mc.player.getPitch() + deltaPitch);
        }
    }
}
