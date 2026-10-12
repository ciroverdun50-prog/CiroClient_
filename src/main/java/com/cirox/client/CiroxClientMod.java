package com.cirox.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CiroxClientMod implements ClientModInitializer {
    private static KeyBinding openGuiKey;

    // Estados de módulos
    public static boolean aimAssistActive = false;
    public static boolean triggerbotActive = false;
    public static boolean jumpResetActive = false;
    public static boolean hitboxActive = false;

    // Parámetros avanzados de Aim Assist
    public static float aimFov = 35.0f;            // Ámbito angular máximo (grados)
    public static float aimDistance = 4.2f;         // Rango máximo de detección en bloques
    public static float aimSpeed = 4.0f;            // Factor de suavizado de giro
    public static boolean onlySword = true;         // Filtro: Solo se activa si el jugador sostiene una espada

    // Parámetros de otros módulos
    public static int triggerCps = 10;
    public static int jumpResetChance = 85;
    public static double hitboxExpansion = 0.04;

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
        });
    }
}
