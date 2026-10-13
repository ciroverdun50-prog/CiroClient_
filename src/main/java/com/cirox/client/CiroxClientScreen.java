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
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        int startX = 50;
        int startY = 50;
        int index = 0;

        for (Module m : CiroxClientMod.getModuleManager().getModules()) {
            int x = startX;
            int y = startY + (index * 25);
            int width = 140;
            int height = 20;

            boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
            int color = hovered ? 0x66FFFFFF : 0x44000000;
            
            context.fill(x, y, x + width, y + height, color);
            
            String status = m.isEnabled() ? " [§aON§r]" : " [§cOFF§r]";
            context.drawTextWithShadow(this.textRenderer, m.getName() + status, x + 5, y + 6, 0xFFFFFF);

            index++;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int startX = 50;
        int startY = 50;
        int index = 0;

        for (Module m : CiroxClientMod.getModuleManager().getModules()) {
            int x = startX;
            int y = startY + (index * 25);
            int width = 140;
            int height = 20;

            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                m.toggle();
                return true;
            }
            index++;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
