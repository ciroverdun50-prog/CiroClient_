package com.cirox.client.ui;

import com.cirox.client.CiroxClientMod;
import com.cirox.client.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CiroxClientScreen extends Screen {
    public CiroxClientScreen() {
        super(Text.literal("CiroxClient GUI"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Fondo translúcido estilo dark minimalista
        context.fill(0, 0, this.width, this.height, 0xAA121218);
        
        // Título del cliente
        context.drawText(this.textRenderer, "CiroxClient v1.0 [1.20.1]", 30, 30, 0xFFFFFFFF, true);

        int startX = 30;
        int startY = 60;

        for (Module m : CiroxClientMod.getModuleManager().getModules()) {
            int color = m.isEnabled() ? 0xFF4646A0 : 0xFF22221E;
            context.fill(startX, startY, startX + 160, startY + 22, color);
            context.drawText(this.textRenderer, m.getName(), startX + 8, startY + 7, 0xFFFFFFFF, false);
            startY += 26;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = 30;
        int startY = 60;

        for (Module m : CiroxClientMod.getModuleManager().getModules()) {
            if (mouseX >= startX && mouseX <= startX + 160 && mouseY >= startY && mouseY <= startY + 22) {
                if (button == 0) {
                    m.toggle();
                    return true;
                }
            }
            startY += 26;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
