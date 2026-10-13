package com.cirox.client;

import com.cirox.client.module.Module;
import com.cirox.client.module.ModuleManager;
import com.cirox.client.module.HUDModule;
import com.cirox.client.ui.CiroxClientScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class CiroxClientMod implements ClientModInitializer {
    public static final String MOD_ID = "ciroxclient";
    
    private static KeyBinding openGuiKey;
    private static ModuleManager moduleManager;
    private static final Map<Integer, Boolean> keyStates = new HashMap<>();

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

            if (client.currentScreen == null && client.getWindow() != null) {
                long window = client.getWindow().getHandle();
                for (Module m : moduleManager.getModules()) {
                    int key = m.getKey();
                    if (key != 0) {
                        boolean isPressed = InputUtil.isKeyPressed(window, key);
                        boolean wasPressed = keyStates.getOrDefault(key, false);
                        
                        if (isPressed && !wasPressed) {
                            m.toggle();
                        }
                        keyStates.put(key, isPressed);
                    }
                }
            }

            moduleManager.onTick();
        });

        HUDModule.class.getName(); // reference check
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            HUDModule hud = moduleManager.getModule(HUDModule.class);
            if (hud != null && hud.isEnabled() && net.minecraft.client.MinecraftClient.getInstance().options.hudHidden == false) {
                int x = drawContext.getScaledWindowWidth() - 4;
                int y = 4;
                int index = 0;
                for (Module m : moduleManager.getModules()) {
                    if (m.isEnabled()) {
                        String text = m.getName();
                        int textWidth = net.minecraft.client.MinecraftClient.getInstance().textRenderer.getWidth(text);
                        drawContext.fill(x - textWidth - 4, y + (index * 12) - 2, x + 2, y + (index * 12) + 10, 0x75000000);
                        drawContext.drawTextWithShadow(net.minecraft.client.MinecraftClient.getInstance().textRenderer, text, x - textWidth, y + (index * 12), 0xFF55FF55);
                        index++;
                    }
                }
            }
        });
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }
}
