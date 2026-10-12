package com.cirox.client.ui;

import com.cirox.client.CiroxClientMod;
import com.cirox.client.module.Module;
import com.cirox.client.module.impl.AimAssistModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class CiroxClientScreen extends Screen {
    private Module selectedModule = null;
    private boolean bindingKey = false;

    public CiroxClientScreen() {
        super(Text.literal("CiroxClient GUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Fondo semi-transparente oscuro con degradado
        context.fill(0, 0, this.width, this.height, 0xCC0D0D12);
        
        // Header de CiroxClient
        context.fill(20, 15, 480, 42, 0xFF161622);
        context.fill(20, 40, 480, 42, 0xFF6C5CE7); // Acento de neón violeta
        context.drawText(this.textRenderer, "CIROX CLIENT", 30, 22, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, "v1.0 | 1.20.1", 120, 24, 0xFFAAAAAA, false);

        int startX = 20;
        int startY = 55;

        // Renderizado de Lista de Módulos (Panel Izquierdo)
        for (Module m : CiroxClientMod.getModuleManager().getModules()) {
            int bgColor = m.isEnabled() ? 0xFF2A265F : 0xFF161620;
            int borderColor = (selectedModule == m) ? 0xFF6C5CE7 : 0xFF282838;

            context.fill(startX, startY, startX + 170, startY + 28, bgColor);
            context.fill(startX, startY, startX + 2, startY + 28, borderColor); // Borde lateral

            String keyName = m.getKey() == 0 ? "NONE" : InputUtil.fromKeyCode(m.getKey(), 0).getLocalizedText().getString().toUpperCase();
            
            context.drawText(this.textRenderer, m.getName(), startX + 10, startY + 10, 0xFFFFFFFF, false);
            context.drawText(this.textRenderer, "[" + keyName + "]", startX + 115, startY + 10, 0xFF8888AA, false);

            startY += 32;
        }

        // Panel de Configuración Estético (Panel Derecho)
        if (selectedModule instanceof AimAssistModule) {
            AimAssistModule aim = (AimAssistModule) selectedModule;
            int cfgX = 200;
            int cfgY = 55;
            
            context.fill(cfgX, cfgY, cfgX + 280, cfgY + 220, 0xFF161622);
            context.fill(cfgX, cfgY, cfgX + 280, cfgY + 2, 0xFF6C5CE7);
            
            context.drawText(this.textRenderer, "AJUSTES: " + aim.getName().toUpperCase(), cfgX + 12, cfgY + 12, 0xFF6C5CE7, true);

            // Asignador de Tecla (Keybind)
            context.drawText(this.textRenderer, "Tecla de acceso:", cfgX + 12, cfgY + 40, 0xFFDCDCC3, false);
            String bindText = bindingKey ? "> PRESIONA TECLA <" : "[" + (aim.getKey() == 0 ? "NONE" : InputUtil.fromKeyCode(aim.getKey(), 0).getLocalizedText().getString().toUpperCase()) + "]";
            context.fill(cfgX + 140, cfgY + 35, cfgX + 265, cfgY + 53, bindingKey ? 0xFF8C7AE6 : 0xFF222232);
            context.drawText(this.textRenderer, bindText, cfgX + 150, cfgY + 40, 0xFFFFFFFF, false);

            // Controles de Clic [-] [+]
            renderControlRow(context, "Distancia", String.format("%.1f blk", aim.aimDistance), cfgX + 12, cfgY + 75);
            renderControlRow(context, "Velocidad", String.format("%.1f", aim.aimSpeed), cfgX + 12, cfgY + 110);
            renderControlRow(context, "FOV", String.format("%.1f°", aim.aimFov), cfgX + 12, cfgY + 145);

            // Solo Espada Switch
            context.drawText(this.textRenderer, "Solo Espada:", cfgX + 12, cfgY + 180, 0xFFFFFFFF, false);
            context.fill(cfgX + 180, cfgY + 175, cfgX + 245, cfgY + 193, aim.onlySword ? 0xFF00B894 : 0xFFD63031);
            context.drawText(this.textRenderer, aim.onlySword ? "ON" : "OFF", cfgX + 202, cfgY + 180, 0xFFFFFFFF, true);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderControlRow(DrawContext context, String label, String value, int x, int y) {
        context.drawText(this.textRenderer, label + ": " + value, x, y, 0xFFFFFFFF, false);
        context.fill(x + 180, x - 12 + 180, y - 4, y + 14, 0xFF28283D);
        context.fill(x + 180, y - 4, x + 205, y + 14, 0xFF222232);
        context.drawText(this.textRenderer, "-", x + 190, y, 0xFFFFFFFF, true);
        context.fill(x + 215, y - 4, x + 240, y + 14, 0xFF222232);
        context.drawText(this.textRenderer, "+", x + 224, y, 0xFFFFFFFF, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = 20;
        int startY = 55;

        // Selección de módulos y activación por Clic Izquierdo / Clic Derecho para abrir menú
        for (Module m : CiroxClientMod.getModuleManager().getModules()) {
            if (mouseX >= startX && mouseX <= startX + 170 && mouseY >= startY && mouseY <= startY + 28) {
                if (button == 0) {
                    m.toggle();
                } else if (button == 1) {
                    selectedModule = (selectedModule == m) ? null : m;
                    bindingKey = false;
                }
                return true;
            }
            startY += 32;
        }

        // Interacciones dentro de los ajustes de AimAssist
        if (selectedModule instanceof AimAssistModule) {
            AimAssistModule aim = (AimAssistModule) selectedModule;
            int cfgX = 200;
            int cfgY = 55;

            // Clic en Bind
            if (mouseX >= cfgX + 140 && mouseX <= cfgX + 265 && mouseY >= cfgY + 35 && mouseY <= cfgY + 53) {
                bindingKey = true;
                return true;
            }

            // Distancia [-] [+]
            if (mouseY >= cfgY + 71 && mouseY <= cfgY + 89) {
                if (mouseX >= cfgX + 192 && mouseX <= cfgX + 217) aim.aimDistance = Math.max(1.0f, aim.aimDistance - 0.2f);
                if (mouseX >= cfgX + 227 && mouseX <= cfgX + 252) aim.aimDistance = Math.min(6.0f, aim.aimDistance + 0.2f);
            }
            // Velocidad [-] [+]
            if (mouseY >= cfgY + 106 && mouseY <= cfgY + 124) {
                if (mouseX >= cfgX + 192 && mouseX <= cfgX + 217) aim.aimSpeed = Math.max(1.0f, aim.aimSpeed - 0.5f);
                if (mouseX >= cfgX + 227 && mouseX <= cfgX + 252) aim.aimSpeed = Math.min(20.0f, aim.aimSpeed + 0.5f);
            }
            // FOV [-] [+]
            if (mouseY >= cfgY + 141 && mouseY <= cfgY + 159) {
                if (mouseX >= cfgX + 192 && mouseX <= cfgX + 217) aim.aimFov = Math.max(5.0f, aim.aimFov - 5.0f);
                if (mouseX >= cfgX + 227 && mouseX <= cfgX + 252) aim.aimFov = Math.min(180.0f, aim.aimFov + 5.0f);
            }
            // Solo Espada
            if (mouseX >= cfgX + 180 && mouseX <= cfgX + 245 && mouseY >= cfgY + 175 && mouseY <= cfgY + 193) {
                aim.onlySword = !aim.onlySword;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (bindingKey && selectedModule != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                selectedModule.setKey(0); // Borrar keybind
            } else {
                selectedModule.setKey(keyCode);
            }
            bindingKey = false;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
