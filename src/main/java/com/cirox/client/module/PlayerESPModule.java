package com.cirox.client.module.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerESPModule extends Module {
    public PlayerESPModule() {
        super("PlayerESP", "Highlights players through walls", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (mc.world == null) return;
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player) {
                entity.setGlowing(true);
            }
        }
    }

    @Override
    public void onDisable() {
        if (mc.world == null) return;
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player) {
                entity.setGlowing(false);
            }
        }
    }
}
