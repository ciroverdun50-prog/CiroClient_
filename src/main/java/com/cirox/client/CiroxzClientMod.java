package com.cirox.client;

package com.ciroxz.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CiroxzClientMod implements ClientModInitializer {
    private static KeyBinding openGuiKey;

    // Estados globales reales leídos por los Mixins del juego
    public static boolean aimAssistActive = false;
    public static boolean triggerbotActive = false;
    public static boolean jumpResetActive = false;
    public static boolean hitboxActive = false;

    // Configuraciones ajustables en tiempo real
    public static float aimFov = 35.0f;
    public static float aimSpeed = 3.0f;
    public static int triggerCps = 10;
    public static int jumpResetChance = 85;
    public static double hitboxExpansion = 0.05;

    @Override
    public void onInitializeClient() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.ciroxzclient.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.ciroxzclient.title"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new CiroxzClientScreen());
                }
            }
        });
    }
}
