package com.cirox.client;

import com.cirox.client.module.ModuleManager;
import com.cirox.client.ui.CiroxClientScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CiroxClientMod implements ClientModInitializer {
    public static final String MOD_ID = "ciroxclient";
    
    private static KeyBinding openGuiKey;
    private static ModuleManager moduleManager;

    @Override
    public void onInitializeClient() {
        moduleManager = new ModuleManager();

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

            moduleManager.onTick();
        });
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }
}
