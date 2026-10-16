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

    public TriggerbotModule() {
        super("Triggerbot", "Attacks always on cooldown with critical hit optimization", Category.COMBAT);
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
                
                if (distance <= 3.0D) {
                    // Sincronización de cooldown de espada
                    float requiredCooldown = 0.90f + (random.nextFloat() * 0.08f);
                    if (mc.player.getAttackCooldownProgress(0.5f) >= requiredCooldown) {
                        
                        mc.interactionManager.attackEntity(mc.player, entity);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        
                        attackDelay = 2 + random.nextInt(3);
                    }
                }
            }
        }
    }
}
