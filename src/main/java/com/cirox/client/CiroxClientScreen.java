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
                color = 0xFF5A5AB4;
            }
            context.fill(startX, startY, startX + 160, startY + 24, color);
            context.drawText(this.textRenderer, m.getName(), startX + 8, startY + 8, 0xFFFFFFFF, false);
            context.drawText(this.textRenderer, ">", startX + 146, startY + 8, 0xFF888888, false);
            startY += 28;
        }

        // Panel de Configuración Desplegable (Panel Derecho - Todo con Clic)
        if (selectedModule instanceof AimAssistModule) {
            AimAssistModule aim = (AimAssistModule) selectedModule;
            int cfgX = 210;
            int cfgY = 60;
            
            context.fill(cfgX, cfgY, cfgX + 240, cfgY + 180, 0xEE1A1A24);
            context.drawText(this.textRenderer, "Config: " + aim.getName(), cfgX + 10, cfgY + 10, 0xFF4682B4, true);

            // Opción: Distancia [ - ] [ + ]
            context.drawText(this.textRenderer, "Distancia: " + String.format("%.1f", aim.aimDistance) + " blk", cfgX + 10, cfgY + 40, 0xFFFFFFFF, false);
            context.fill(cfgX + 150, cfgY + 36, cfgX + 175, cfgY + 54, 0xFF333344);
            context.drawText(this.textRenderer, "-", cfgX + 161, cfgY + 41, 0xFFFFFFFF, true);
            context.fill(cfgX + 185, cfgY + 36, cfgX + 210, cfgY + 54, 0xFF333344);
            context.drawText(this.textRenderer, "+", cfgX + 195, cfgY + 41, 0xFFFFFFFF, true);

            // Opción: Velocidad [ - ] [ + ]
            context.drawText(this.textRenderer, "Velocidad: " + String.format("%.1f", aim.aimSpeed), cfgX + 10, cfgY + 75, 0xFFFFFFFF, false);
            context.fill(cfgX + 150, cfgY + 71, cfgX + 175, cfgY + 89, 0xFF333344);
            context.drawText(this.textRenderer, "-", cfgX + 161, cfgY + 76, 0xFFFFFFFF, true);
            context.fill(cfgX + 185, cfgY + 71, cfgX + 210, cfgY + 89, 0xFF333344);
            context.drawText(this.textRenderer, "+", cfgX + 195, cfgY + 76, 0xFFFFFFFF, true);

            // Opción: FOV [ - ] [ + ]
            context.drawText(this.textRenderer, "FOV: " + String.format("%.1f", aim.aimFov) + "°", cfgX + 10, cfgY + 110, 0xFFFFFFFF, false);
            context.fill(cfgX + 150, cfgY + 106, cfgX + 175, cfgY + 124, 0xFF333344);
            context.drawText(this.textRenderer, "-", cfgX + 161, cfgY + 111, 0xFFFFFFFF, true);
            context.fill(cfgX + 185, cfgY + 106, cfgX + 210, cfgY + 124, 0xFF333344);
            context.drawText(this.textRenderer, "+", cfgX + 195, cfgY + 111, 0xFFFFFFFF, true);

            // Opción: Solo Espada [ TOGGLE ]
            context.drawText(this.textRenderer, "Solo Espada:", cfgX + 10, cfgY + 145, 0xFFFFFFFF, false);
            int swordColor = aim.onlySword ? 0xFF4646A0 : 0xFF333344;
            context.fill(cfgX + 150, cfgY + 141, cfgX + 210, cfgY + 159, swordColor);
            context.drawText(this.textRenderer, aim.onlySword ? "ON" : "OFF", cfgX + 168, cfgY + 146, 0xFFFFFFFF, true);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) { // Solo usamos Click Izquierdo (0)
            if (button == 1) {
                // Click derecho abre/cierra panel de config
                int startX = 30;
                int startY = 60;
                for (Module m : CiroxClientMod.getModuleManager().getModules()) {
                    if (mouseX >= startX && mouseX <= startX + 160 && mouseY >= startY && mouseY <= startY + 24) {
                        selectedModule = (selectedModule == m) ? null : m;
                        return true;
                    }
                    startY += 28;
                }
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        int startX = 30;
        int startY = 60;

        // Selección de módulo con clic izquierdo
        for (Module m : CiroxClientMod.getModuleManager().getModules()) {
            if (mouseX >= startX && mouseX <= startX + 160 && mouseY >= startY && mouseY <= startY + 24) {
                m.toggle();
                return true;
            }
            startY += 28;
        }

        // Interacción por Clic Izquierdo en el Panel de Configuración de AimAssist
        if (selectedModule instanceof AimAssistModule) {
            AimAssistModule aim = (AimAssistModule) selectedModule;
            int cfgX = 210;
            int cfgY = 60;

            // Distancia [-] [+](Y: 36 a 54)
            if (mouseY >= cfgY + 36 && mouseY <= cfgY + 54) {
                if (mouseX >= cfgX + 150 && mouseX <= cfgX + 175) aim.aimDistance = Math.max(1.0f, aim.aimDistance - 0.2f);
                if (mouseX >= cfgX + 185 && mouseX <= cfgX + 210) aim.aimDistance = Math.min(6.0f, aim.aimDistance + 0.2f);
                return true;
            }
            // Velocidad [-] [+](Y: 71 a 89)
            if (mouseY >= cfgY + 71 && mouseY <= cfgY + 89) {
                if (mouseX >= cfgX + 150 && mouseX <= cfgX + 175) aim.aimSpeed = Math.max(1.0f, aim.aimSpeed - 0.5f);
                if (mouseX >= cfgX + 185 && mouseX <= cfgX + 210) aim.aimSpeed = Math.min(20.0f, aim.aimSpeed + 0.5f);
                return true;
            }
            // FOV [-] [+](Y: 106 a 124)
            if (mouseY >= cfgY + 106 && mouseY <= cfgY + 124) {
                if (mouseX >= cfgX + 150 && mouseX <= cfgX + 175) aim.aimFov = Math.max(5.0f, aim.aimFov - 5.0f);
                if (mouseX >= cfgX + 185 && mouseX <= cfgX + 210) aim.aimFov = Math.min(180.0f, aim.aimFov + 5.0f);
                return true;
            }
            // Solo Espada Toggle (Y: 141 a 159)
            if (mouseY >= cfgY + 141 && mouseY <= cfgY + 159) {
                if (mouseX >= cfgX + 150 && mouseX <= cfgX + 210) {
                    aim.onlySword = !aim.onlySword;
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
