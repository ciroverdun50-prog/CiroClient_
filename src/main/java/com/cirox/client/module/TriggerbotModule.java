package com.cirox.client.module.impl;

import com.cirox.client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.Random;

public class TriggerbotModule extends Module {
    public boolean onlySword = true;
    public boolean serverCooldown = true;
    public int baseCps = 10;
    
    private long lastAttackTime = 0;
    private long currentDelay = 0;
    private final Random random = new Random();

    public TriggerbotModule() {
        super("Triggerbot", "Ataca con temporización humanizada al apuntar a una entidad", Category.COMBAT);
        setKey(0);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        if (onlySword && !(mc.player.getMainHandStack().getItem() instanceof SwordItem)) {
            return;
        }

        HitResult hit = mc.crosshairTarget;
        if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            Entity target = entityHit.getEntity();

            if (target instanceof LivingEntity && target != mc.player) {
                if (target instanceof PlayerEntity && ((PlayerEntity) target).isCreative()) return;
                if (((LivingEntity) target).isDead() || ((LivingEntity) target).isSpectator()) return;

                if (serverCooldown) {
                    if (mc.player.getAttackCooldownProgress(0.5f) >= 0.95f) {
                        attackTarget(target);
                    }
                } else {
                    long now = System.currentTimeMillis();
                    if (now - lastAttackTime >= currentDelay) {
                        attackTarget(target);
                        lastAttackTime = now;
                        calculateNextDelay();
                    }
                }
            }
        }
    }

    private void attackTarget(Entity target) {
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private void calculateNextDelay() {
        double targetDelayMs = 1000.0 / Math.max(1, baseCps);
        double noise = (random.nextDouble() * 0.30 - 0.15) * targetDelayMs;
        this.currentDelay = (long) (targetDelayMs + noise);
    }
}
