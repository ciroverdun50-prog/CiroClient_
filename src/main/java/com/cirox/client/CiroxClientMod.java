package com.cirox.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ThreadLocalRandom;

public class CiroxClientMod implements ClientModInitializer {
    private static KeyBinding openGuiKey;

    // Estados globales de los módulos
    public static boolean aimAssistActive = false;
    public static boolean triggerbotActive = false;
    public static boolean jumpResetActive = false;
    public static boolean hitboxActive = false;

    // Configuraciones ajustables
    public static float aimFov = 30.0f;
    public static float aimSpeed = 4.0f;       // Velocidad de interpolación por tick (Evita flags de Grim Angle)
    public static int triggerCps = 9;          // CPS controlado para evitar picos de Autoclicker
    public static int jumpResetChance = 80;
    public static double hitboxExpansion = 0.03; // Expansión ultra-conservadora para eludir Reach checks de Vulcan/Grim

    private static int attackTickCounter = 0;
    private static int jumpResetTicksLeft = 0;

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

            // 1. Triggerbot sincronizado por Ticks de juego (Bypass Grim/Vulcan CPS & Killaura checks)
            if (triggerbotActive && client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHit = (EntityHitResult) client.crosshairTarget;
                if (entityHit.getEntity() instanceof PlayerEntity targetPlayer && targetPlayer != client.player) {
                    attackTickCounter++;
                    int targetInterval = Math.max(2, 20 / Math.max(1, triggerCps)); // Ticks entre golpes
                    int randomizedJitter = ThreadLocalRandom.current().nextInt(0, 2); // Variabilidad humana
                    
                    if (attackTickCounter >= (targetInterval + randomizedJitter)) {
                        attackTickCounter = 0;
                        client.interactionManager.attackEntity(client.player, targetPlayer);
                        client.player.swingHand(client.player.getActiveHand()); // Paquete Swing obligatorio
                    }
                }
            }

            // 2. Jump Reset / W-Tap Legítimo basado en Ticks (Bypass Velocity / Motion checks)
            if (jumpResetActive) {
                if (jumpResetTicksLeft > 0) {
                    jumpResetTicksLeft--;
                    client.options.forwardKey.setPressed(false); // Libera la W temporalmente
                } else if (client.player.hurtTime > 3) {
                    if (ThreadLocalRandom.current().nextInt(100) < jumpResetChance) {
                        jumpResetTicksLeft = 2; // Mantiene la W soltada exactamente 2 ticks de juego
                    }
                }
            }
        });
    }
}
