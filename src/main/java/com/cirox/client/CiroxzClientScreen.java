package com.cirox.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CiroxClientScreen extends Screen {
    private String currentTab = "Dashboard";

    public CiroxClientScreen() {
        super(Text.literal("Cirox Client Core"));
    }

    @Override
    protected void init() {
        this.clearChildren();
        rebuildWidgets();
    }

    private void rebuildWidgets() {
        this.clearChildren();

        int guiLeft = (this.width - 440) / 2;
        int guiTop = (this.height - 280) / 2;
        int sidebarX = guiLeft + 18;
        int startY = guiTop + 75;
        int buttonWidth = 110;
        int buttonHeight = 24;
        int spacing = 28;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Dashboard"), b -> { currentTab = "Dashboard"; rebuildWidgets(); })
                .dimensions(sidebarX, startY, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Aim Assist"), b -> { currentTab = "Aim Assist"; rebuildWidgets(); })
                .dimensions(sidebarX, startY + spacing, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Triggerbot"), b -> { currentTab = "Triggerbot"; rebuildWidgets(); })
                .dimensions(sidebarX, startY + (spacing * 2), buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Jump Reset"), b -> { currentTab = "Jump Reset"; rebuildWidgets(); })
                .dimensions(sidebarX, startY + (spacing * 3), buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("HitBox Exp"), b -> { currentTab = "HitBox Exp"; rebuildWidgets(); })
                .dimensions(sidebarX, startY + (spacing * 4), buttonWidth, buttonHeight).build());

        int contentX = guiLeft + 145;
        int contentY = guiTop + 75;

        if (!currentTab.equals("Dashboard")) {
            boolean isActive = switch (currentTab) {
                case "Aim Assist" -> CiroxClientMod.aimAssistActive;
                case "Triggerbot" -> CiroxClientMod.triggerbotActive;
                case "Jump Reset" -> CiroxClientMod.jumpResetActive;
                case "HitBox Exp" -> CiroxClientMod.hitboxActive;
                default -> false;
            };

            String toggleText = isActive ? "Estado: §aActivo" : "Estado: §cApagado";
            this.addDrawableChild(ButtonWidget.builder(Text.literal(toggleText), b -> {
                switch (currentTab) {
                    case "Aim Assist" -> CiroxClientMod.aimAssistActive = !CiroxClientMod.aimAssistActive;
                    case "Triggerbot" -> CiroxClientMod.triggerbotActive = !CiroxClientMod.triggerbotActive;
                    case "Jump Reset" -> CiroxClientMod.jumpResetActive = !CiroxClientMod.jumpResetActive;
                    case "HitBox Exp" -> CiroxClientMod.hitboxActive = !CiroxClientMod.hitboxActive;
                }
                rebuildWidgets();
            }).dimensions(contentX, contentY, 275, 24).build());

            if (currentTab.equals("Aim Assist")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("FOV Límite: " + CiroxClientMod.aimFov), b -> {
                    CiroxClientMod.aimFov = CiroxClientMod.aimFov >= 50.0f ? 20.0f : CiroxClientMod.aimFov + 5.0f;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 275, 24).build());

                this.addDrawableChild(ButtonWidget.builder(Text.literal("Velocidad Curva: " + CiroxClientMod.aimSpeed), b -> {
                    CiroxClientMod.aimSpeed = CiroxClientMod.aimSpeed >= 6.0f ? 1.5f : CiroxClientMod.aimSpeed + 0.5f;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 68, 275, 24).build());

            } else if (currentTab.equals("Triggerbot")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("CPS Límite: " + CiroxClientMod.triggerCps), b -> {
                    CiroxClientMod.triggerCps = CiroxClientMod.triggerCps >= 16 ? 8 : CiroxClientMod.triggerCps + 1;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 275, 24).build());

            } else if (currentTab.equals("Jump Reset")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("Probabilidad W-Tap: " + CiroxClientMod.jumpResetChance + "%"), b -> {
                    CiroxClientMod.jumpResetChance = CiroxClientMod.jumpResetChance >= 100 ? 60 : CiroxClientMod.jumpResetChance + 10;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 275, 24).build());

            } else if (currentTab.equals("HitBox Exp")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("Expansión Segura: +" + CiroxClientMod.hitboxExpansion), b -> {
                    double nextExp = CiroxClientMod.hitboxExpansion >= 0.12 ? 0.02 : CiroxClientMod.hitboxExpansion + 0.02;
                    CiroxClientMod.hitboxExpansion = Math.round(nextExp * 100.0) / 100.0;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 275, 24).build());
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int guiLeft = (this.width - 440) / 2;
        int guiTop = (this.height - 280) / 2;

        context.fill(guiLeft, guiTop, guiLeft + 440, guiTop + 280, 0xEE111813);
        context.fill(guiLeft, guiTop, guiLeft + 440, guiTop + 45, 0xFF162B1D);
        context.fill(guiLeft, guiTop + 55, guiLeft + 135, guiTop + 265, 0xFF142419);
        context.drawBorder(guiLeft, guiTop, 440, 280, 0xFF2ED16A);

        context.drawText(this.textRenderer, Text.literal("§lCirox Client // Core"), guiLeft + 18, guiTop + 16, 0x55FF88, true);
        context.drawText(this.textRenderer, Text.literal("v5.8"), guiLeft + 95, guiTop + 18, 0x88AA99, true);

        if (currentTab.equals("Dashboard")) {
            int textX = guiLeft + 145;
            int textY = guiTop + 75;
            context.drawText(this.textRenderer, Text.literal("§lPanel de Control"), textX, textY, 0xFFFFFF, true);
            context.drawText(this.textRenderer, Text.literal("Sincronización de módulos activa."), textX, textY + 20, 0x88AA99, false);

            int statusY = textY + 60;
            context.drawText(this.textRenderer, Text.literal("Aim Assist: " + (CiroxClientMod.aimAssistActive ? "§aHooked" : "§7Off")), textX, statusY, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("Triggerbot: " + (CiroxClientMod.triggerbotActive ? "§aHooked" : "§7Off")), textX, statusY + 20, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("Jump Reset: " + (CiroxClientMod.jumpResetActive ? "§aHooked" : "§7Off")), textX, statusY + 40, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("HitBox Exp: " + (CiroxClientMod.hitboxActive ? "§aHooked" : "§7Off")), textX, statusY + 60, 0xDDDDDD, false);
        } else {
            int textX = guiLeft + 145;
            int textY = guiTop + 200;
            context.drawText(this.textRenderer, Text.literal("Módulo Seleccionado: " + currentTab), textX, textY, 0xAAAAAA, false);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
