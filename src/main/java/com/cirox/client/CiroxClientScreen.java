package com.cirox.client.ui;

import com.cirox.client.CiroxClientMod;
import com.cirox.client.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class CiroxClientScreen extends Screen {
    private Module selectedModuleForBind = null;
    private boolean waitingForKey = false;

    public CiroxClientScreen() {
        super(Text.literal("CiroxClient GUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        int startX = this.width / 2 - 110;
        int startY = 50;
        int index = 0;

        for (Module m : CiroxClientMod.getModuleManager().getModules()) {
            int x = startX;
            int y = startY + (index * 28);
            int width = 220;
            int height = 22;

            boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            int color = hovered ? 0x66FFFFFF : 0x44000000;
            
            context.fill(x, y, x + width, y + height, color);
            
            String status = m.isEnabled() ? "§aON" : "§cOFF";
            String keyName = m.getKey() == 0 ? "NONE" : GLFW.glfwGetKeyName(m.getKey(), 0);
            if (keyName == null) keyName = "KEY_" + m.getKey();
            
            if (waitingForKey && selectedModuleForBind == m) {
                keyName = "...";
            }

            context.drawTextWithShadow(this.textRenderer, m.getName(), x + 6, y + 7, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "[" + status + "]", x + 120, y + 7, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "[" + keyName.toUpperCase() + "]", x + 160, y + 7, 0xFFAAAAAA);

            index++;
        }

        if (waitingForKey) {
            context.drawCenteredTextWithShadow(this.textRenderer, "§ePress any key to bind (ESC to clear)...", this.width / 2, this.height - 30, 0xFFFFFF);
        } else {
            context.drawCenteredTextWithShadow(this.textRenderer, "§7Left-click: Toggle | Right-click: Bind Key", this.width / 2, this.height - 30, 0xFFFFFF);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (waitingForKey) return true;

        int startX = this.width / 2 - 110;
        int startY = 50;
        int index = 0;

        for (Module m : CiroxClientMod.getModuleManager().getModules()) {
            int x = startX;
            int y = startY + (index * 28);
            int width = 220;
            int height = 22;

            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                if (button == 0) {
                    // Clic izquierdo: Activa o desactiva el módulo
                    m.toggle();
                    return true;
                } else if (button == 1) {
                    // Clic derecho: Inicia el modo de vinculación de tecla
                    selectedModuleForBind = m;
                    waitingForKey = true;
                    return true;
                }
            }
            index++;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForKey && selectedModuleForBind != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                selectedModuleForBind.setKey(0); // Borra la tecla si presiona ESC
            } else {
                selectedModuleForBind.setKey(keyCode); // Asigna la nueva tecla
            }
            waitingForKey = false;
            selectedModuleForBind = null;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
