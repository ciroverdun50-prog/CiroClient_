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
        super("Triggerbot", "Punish mode: waits for falling state to critical hit", Category.COMBAT);
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
                if (mc.player.getAttackCooldownProgress(0.5f) >= 0.9f) {
                    // Detecta si estás en el aire y cayendo (ideal para hacer punish y crítico tras recibir golpe o salto)
                    boolean isFalling = !mc.player.isOnGround() && mc.player.getVelocity().y < 0.0D;

                    if (isFalling) {
                        mc.interactionManager.attackEntity(mc.player, entity);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        delay = 1 + random.nextInt(3);
                    }
                }
            }
        }
    }
}
