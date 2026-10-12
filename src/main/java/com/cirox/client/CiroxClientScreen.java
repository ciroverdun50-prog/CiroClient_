package com.cirox.client.ui;

import com.cirox.client.CiroxClientMod;
import com.cirox.client.module.Module;
import com.cirox.client.module.impl.AimAssistModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CiroxClientScreen extends Screen {
    private Module selectedModule = null;

    public CiroxClientScreen() {
        super(Text.literal("CiroxClient GUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Fondo translúcido moderno
        context.fill(0, 0, this.width, this.height, 0xAA121218);
        
        // Título del cliente
        context.drawText(this.textRenderer, "CiroxClient v1.0 [1.20.1]", 30, 25, 0xFFFFFFFF, true);

        int startX = 30;
        int startY = 60;

        // Renderizado de Módulos (Panel Izquierdo)
        for (Module m : CiroxClientMod.getModuleManager().getModules()) {
            int color = m.isEnabled() ? 0xFF4646A0 : 0xFF22221E;
            if (selectedModule == m) {
                color = 0xFF5A5AB4; // Resaltado si está seleccionado para configurar
            }
            context.fill(startX, startY, startX + 160, startY + 24, color);
            context.drawText(this.textRenderer, m.getName(), startX + 8, startY + 8, 0xFFFFFFFF, false);
            context.drawText(this.textRenderer, ">", startX + 146, startY + 8, 0xFF888888, false);
            startY += 28;
        }

        // Renderizado del Panel de Configuración Desplegable (Panel Derecho)
        if (selectedModule instanceof AimAssistModule) {
            AimAssistModule aim = (AimAssistModule) selectedModule;
            int cfgX = 210;
            int cfgY = 60;
            
            context.fill(cfgX, cfgY, cfgX + 220, cfgY + 160, 0xEE1A1A24);
            context.drawText(this.textRenderer, "Config: " + aim.getName(), cfgX + 10, cfgY + 10, 0xFF4682B4, true);

            // Opciones visuales de configuración
            context.drawText(this.textRenderer, "Distancia: " + String.format("%.1f", aim.aimDistance) + " blk [+/-]", cfgX + 10, cfgY + 35, 0xFFFFFFFF, false);
            context.drawText(this.textRenderer, "Velocidad: " + String.format("%.1f", aim.aimSpeed) + "   [< / >]", cfgX + 10, cfgY + 65, 0xFFFFFFFF, false);
            context.drawText(this.textRenderer, "FOV: " + String.format("%.1f", aim.aimFov) + "°      [[ / ]]", cfgX + 10, cfgY + 95, 0xFFFFFFFF, false);
            context.drawText(this.textRenderer, "Solo Espada: " + (aim.onlySword ? "ON" : "OFF") + " [CLICK]", cfgX + 10, cfgY + 125, 0xFFFFFFFF, false);
            
            context.drawText(this.textRenderer, "Usa teclas numéricas/flechas para ajustar", cfgX + 10, cfgY + 143, 0xFFAAAAAA, false);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = 30;
        int startY = 60;

        for (Module m : CiroxClientMod.getModuleManager().getModules()) {
            if (mouseX >= startX && mouseX <= startX + 160 && mouseY >= startY && mouseY <= startY + 24) {
                if (button == 0) {
                    // Clic izquierdo: Activa/Desactiva el módulo
                    m.toggle();
                    return true;
                } else if (button == 1) {
                    // Clic derecho: Abre el menú de configuración desplegable
                    selectedModule = (selectedModule == m) ? null : m;
                    return true;
                }
            }
            startY += 28;
        }

        // Interacción dentro del panel de configuración de AimAssist
        if (selectedModule instanceof AimAssistModule && mouseX >= 210 && mouseX <= 430 && mouseY >= 60 && mouseY <= 220) {
            AimAssistModule aim = (AimAssistModule) selectedModule;
            if (mouseY >= 120 && mouseY <= 140) {
                aim.onlySword = !aim.onlySword; // Toggle de espada con clic
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Controles rápidos para ajustar valores de AimAssist cuando está seleccionado
        if (selectedModule instanceof AimAssistModule) {
            AimAssistModule aim = (AimAssistModule) selectedModule;
            if (keyCode == 262) { // Flecha derecha -> Aumenta velocidad
                aim.aimSpeed = Math.min(20.0f, aim.aimSpeed + 0.5f);
            } else if (keyCode == 263) { // Flecha izquierda -> Reduce velocidad
                aim.aimSpeed = Math.max(1.0f, aim.aimSpeed - 0.5f);
            } else if (keyCode == 265) { // Flecha arriba -> Aumenta distancia
                aim.aimDistance = Math.min(6.0f, aim.aimDistance + 0.2f);
            } else if (keyCode == 264) { // Flecha abajo -> Reduce distancia
                aim.aimDistance = Math.max(2.0f, aim.aimDistance - 0.2f);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
