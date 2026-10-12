package com.cirox.client.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.Hand;

import java.util.Random;

public class TriggerbotModule extends Module {
    private final Random random = new Random();
    private int delay = 0;

    public TriggerbotModule() {
        super("Triggerbot", "Waits for fall state to punish with critical hits", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) return;

        if (delay > 0) {
            delay--;
            return;
        }

        HitResult hit = mc.crosshairTarget;
        if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            Entity entity = entityHit.getEntity();

            if (entity instanceof LivingEntity && entity != mc.player && !entity.isSpectator()) {
                // 1. Verifica que el cooldown de ataque esté listo
                if (mc.player.getAttackCooldownProgress(0.5f) >= 0.9f) {
                    
                    // 2. Condición de crítico / Punish: 
                    // No debe estar en el suelo (!isOnGround) y su velocidad vertical debe ser negativa (cayendo).
                    // Esto detecta tanto cuando caes de un salto como cuando caes tras recibir un golpe/knockback.
                    boolean isFalling = !mc.player.isOnGround() && mc.player.getVelocity().y < 0.0D;

                    if (isFalling) {
                        mc.interactionManager.attackEntity(mc.player, entity);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        
                        // Pequeño retardo aleatorio para mantener el bypass contra Grim y Vulcan
                        delay = 1 + random.nextInt(3);
                    }
                }
            }
        }
    }
}
