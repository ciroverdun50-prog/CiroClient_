package com.cirox.client.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.Hand;

import java.util.Random;

public class TriggerbotModule extends Module {
    private final Random random = new Random();
    private int attackDelay = 0;
    private int targetTicks = 0;

    public TriggerbotModule() {
        super("Triggerbot", "Advanced Punish & Anti-Cheat Bypass (Grim/Vulcan)", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;

        if (attackDelay > 0) {
            attackDelay--;
            return;
        }

        HitResult hit = mc.crosshairTarget;
        if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            Entity entity = entityHit.getEntity();

            if (entity instanceof LivingEntity && entity != mc.player && !entity.isSpectator()) {
                double distance = mc.player.distanceTo(entity);
                
                // Distancia estricta para evitar baneos por Reach en Grim y Vulcan
                if (distance <= 3.0D) {
                    targetTicks++;
                    
                    // Simulación de tiempo de reacción humano (no ataca en el frame exacto 0)
                    if (targetTicks < 1 + random.nextInt(2)) {
                        return;
                    }

                    // Umbral de cooldown dinámico y aleatorio para burlar análisis estadísticos
                    float requiredCooldown = 0.90f + (random.nextFloat() * 0.08f);
                    if (mc.player.getAttackCooldownProgress(0.5f) >= requiredCooldown) {
                        
                        // Lógica de Punish Crit: verifica que estés cayendo en el aire
                        boolean isFalling = !mc.player.isOnGround() && mc.player.getVelocity().y < -0.01D;

                        if (isFalling) {
                            // 95% de éxito por golpe para simular la imperfección humana y eludir heurísticas
                            if (random.nextFloat() <= 0.95f) {
                                mc.interactionManager.attackEntity(mc.player, entity);
                                mc.player.swingHand(Hand.MAIN_HAND);
                            }
                            
                            // Retraso aleatorio variable entre ataques
                            attackDelay = 2 + random.nextInt(4);
                            targetTicks = 0;
                        }
                    }
                }
            } else {
                targetTicks = 0;
            }
        } else {
            targetTicks = 0;
        }
    }
}
