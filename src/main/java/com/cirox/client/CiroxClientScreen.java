package com.cirox.client.ui;

import com.cirox.client.CiroxClientMod;
import com.cirox.client.module.AimAssistModule;
import com.cirox.client.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class CiroxClientScreen extends Screen {
    private Module selectedModuleForBind = null;
    private Module activeConfigModule = null;
    private boolean waitingForKey = false;

    public CiroxClientScreen() {
        super(Text.literal("CiroxClient GUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);

        int startX = this.width / 2 - 120;
        int startY = 35;
        int index = 0;

        for (Module m : CiroxClientMod.getModuleManager().getModules()) {
            int x = startX;
            int y = startY + (index * 24);
            int width = 240;
            int height = 20;

            boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            int color = hovered ? 0x66FFFFFF : 0x44000000;
            
            context.fill(x, y, x + width, y + height, color);
            
            String status = m.isEnabled() ? "§aON" : "§cOFF";
            String keyName = m.getKey() == 0 ? "NONE" : GLFW.glfwGetKeyName(m.getKey(), 0);
            if (keyName == null) keyName = "KEY_" + m.getKey();
            
            if (waitingForKey && selectedModuleForBind == m) {
                keyName = "...";
            }

            context.drawTextWithShadow(this.textRenderer, m.getName(), x + 6, y + 6, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "[" + status + "]", x + 130, y + 6, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, "[" + keyName.toUpperCase() + "]", x + 175, y + 6, 0xFFAAAAAA);

            index++;
        }

        // Panel de configuración interactivo para AimAssist
        if (activeConfigModule instanceof AimAssistModule) {
            AimAssistModule aim = (AimAssistModule) activeConfigModule;
            int panelX = this.width / 2 - 120;
            int panelY = 185;
            int panelW = 240;
            int panelH = 95;

            context.fill(panelX, panelY, panelX + panelW, panelY + panelH, 0xEE111111);
            context.drawTextWithShadow(this.textRenderer, "§eSettings: AimAssist", panelX + 8, panelY + 6, 0xFFFFFF);

            // Speed
            context.drawTextWithShadow(this.textRenderer, "Speed: " + String.format("%.1f", aim.aimSpeed), panelX + 8, panelY + 24, 0xCCCCCC);
            context.drawTextWithShadow(this.textRenderer, "[-]  [+]", panelX + 180, panelY + 24, 0x55FF55);

            // FOV
            context.drawTextWithShadow(this.textRenderer, "FOV: " + String.format("%.1f", aim.aimFov), panelX + 8, panelY + 40, 0xCCCCCC);
            context.drawTextWithShadow(this.textRenderer, "[-]  [+]", panelX + 180, panelY + 40, 0x55FF55);

            // Distance
            context.drawTextWithShadow(this.textRenderer, "Distance: " + String.format("%.1f", aim.aimDistance), panelX + 8, panelY + 56, 0xCCCCCC);
            context.drawTextWithShadow(this.textRenderer, "[-]  [+]", panelX + 180, panelY + 56, 0x55FF55);

            // Only Sword
            String swordStatus = aim.onlySword ? "§aTrue" : "§cFalse";
            context.drawTextWithShadow(this.textRenderer, "Only Sword: " + swordStatus, panelX + 8, panelY + 72, 0xCCCCCC);
        }

        if (waitingForKey) {
            context.drawCenteredTextWithShadow(this.textRenderer, "§ePress any key to bind (ESC to clear)...", this.width / 2, this.height - 20, 0xFFFFFF);
        } else {
            context.drawCenteredTextWithShadow(this.textRenderer, "§7Left-click: Toggle | Right-click: Bind | Click AimAssist to Configure", this.width / 2, this.height - 20, 0xFFFFFF);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (waitingForKey) return true;

        int startX = this.width / 2 - 120;
        int startY = 35;
        int index = 0;

        for (Module m : CiroxClientMod.getModuleManager().getModules()) {
            int x = startX;
            int y = startY + (index * 24);
            int width = 240;
            int height = 20;

            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                if (button == 0) {
                    m.toggle();
                    if (m instanceof AimAssistModule) {
                        activeConfigModule = m;
                    }
                    return true;
                } else if (button == 1) {
                    selectedModuleForBind = m;
                    waitingForKey = true;
                    return true;
                }
            }
            index++;
        }

        // Clics dentro del panel de configuración de AimAssist
        if (activeConfigModule instanceof AimAssistModule) {
            AimAssistModule aim = (AimAssistModule) activeConfigModule;
            int panelX = this.width / 2 - 120;
            int panelY = 185;

            // Speed buttons [-] [+]
            if (mouseX >= panelX + 175 && mouseX <= panelX + 198 && mouseY >= panelY + 22 && mouseY <= panelY + 34) {
                aim.aimSpeed = Math.max(1.0f, aim.aimSpeed - 0.5f); return true;
            }
            if (mouseX >= panelX + 202 && mouseX <= panelX + 225 && mouseY >= panelY + 22 && mouseY <= panelY + 34) {
                aim.aimSpeed = Math.min(20.0f, aim.aimSpeed + 0.5f); return true;
            }

            // FOV buttons [-] [+]
            if (mouseX >= panelX + 175 && mouseX <= panelX + 198 && mouseY >= panelY + 38 && mouseY <= panelY + 50) {
                aim.aimFov = Math.max(5.0f, aim.aimFov - 5.0f); return true;
            }
            if (mouseX >= panelX + 202 && mouseX <= panelX + 225 && mouseY >= panelY + 38 && mouseY <= panelY + 50) {
                aim.aimFov = Math.min(180.0f, aim.aimFov + 5.0f); return true;
            }

            // Distance buttons [-] [+]
            if (mouseX >= panelX + 175 && mouseX <= panelX + 198 && mouseY >= panelY + 54 && mouseY <= panelY + 66) {
                aim.aimDistance = Math.max(1.0f, aim.aimDistance - 0.2f); return true;
            }
            if (mouseX >= panelX + 202 && mouseX <= panelX + 225 && mouseY >= panelY + 54 && mouseY <= panelY + 66) {
                aim.aimDistance = Math.min(6.0f, aim.aimDistance + 0.2f); return true;
            }

            // Only Sword toggle click
            if (mouseX >= panelX + 100 && mouseX <= panelX + 180 && mouseY >= panelY + 70 && mouseY <= panelY + 85) {
                aim.onlySword = !aim.onlySword; return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForKey && selectedModuleForBind != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                selectedModuleForBind.setKey(0);
            } else {
                selectedModuleForBind.setKey(keyCode);
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
