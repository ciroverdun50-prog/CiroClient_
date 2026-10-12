package com.cirox.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ThreadLocalRandom;

public class CiroxClientMod implements ClientModInitializer {
    private static KeyBinding openGuiKey;

    // Estados reales de los módulos
    public static boolean aimAssistActive = false;
    public static boolean triggerbotActive = false;
    public static boolean jumpResetActive = false;
    public static boolean hitboxActive = false;

    // Configuraciones ajustables y funcionales
    public static float aimFov = 35.0f;
    public static float aimSpeed = 3.0f;
    public static int triggerCps = 10;
    public static int jumpResetChance = 85;
    public static double hitboxExpansion = 0.05;

    private static long lastAttackTick = 0;

    @Override
    public void onInitializeClient() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ciroxclient.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.ciroxclient.title"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new CiroxClientScreen());
                }
            }

            if (client.player == null || client.world == null) return;

            // 1. Triggerbot Funcional sincronizado por ticks con CPS configurable y swing legítimo
            if (triggerbotActive && client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHit = (EntityHitResult) client.crosshairTarget;
                if (evaluateTriggerbotTick(triggerCps)) {
                    client.interactionManager.attackEntity(client.player, entityHit.getEntity());
                    client.player.swingHand(client.player.getActiveHand());
                }
            }

            // 2. Jump Reset / W-Tap Funcional con probabilidad configurable
            if (jumpResetActive && client.player.hurtTime > 0) {
                if (ThreadLocalRandom.current().nextInt(100) < jumpResetChance) {
                    client.options.forwardKey.setPressed(false);
                }
            }
        });
    }

    private static boolean evaluateTriggerbotTick(int targetCps) {
        long currentMillis = System.currentTimeMillis();
        long interval = 1000L / targetCps;
        long jitter = ThreadLocalRandom.current().nextLong(20, 50);
        if (currentMillis - lastAttackTick >= (interval + jitter)) {
            lastAttackTick = currentMillis;
            return true;
        }
        return false;
    }
}
