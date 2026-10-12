package com.cirox.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CiroxClientScreen extends Screen {
    private String currentTab = "Dashboard";
    private double scrollOffset = 0.0;
    private final double maxScroll = 140.0;

    public CiroxClientScreen() {
        super(Text.literal("Cirox Client Elite"));
    }

    @Override
    protected void init() {
        this.clearChildren();
        rebuildWidgets();
    }

    private void rebuildWidgets() {
        this.clearChildren();

        int guiLeft = (this.width - 480) / 2;
        int guiTop = (this.height - 310) / 2;
        int sidebarX = guiLeft + 18;
        int startY = guiTop + 75;
        int buttonWidth = 125;
        int buttonHeight = 24;
        int spacing = 28;

        // Menú lateral de navegación
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Dashboard"), b -> { currentTab = "Dashboard"; scrollOffset = 0; rebuildWidgets(); })
                .dimensions(sidebarX, startY, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Aim Assist"), b -> { currentTab = "Aim Assist"; scrollOffset = 0; rebuildWidgets(); })
                .dimensions(sidebarX, startY + spacing, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Triggerbot"), b -> { currentTab = "Triggerbot"; scrollOffset = 0; rebuildWidgets(); })
                .dimensions(sidebarX, startY + (spacing * 2), buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Jump Reset"), b -> { currentTab = "Jump Reset"; scrollOffset = 0; rebuildWidgets(); })
                .dimensions(sidebarX, startY + (spacing * 3), buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("HitBox Exp"), b -> { currentTab = "HitBox Exp"; scrollOffset = 0; rebuildWidgets(); })
                .dimensions(sidebarX, startY + (spacing * 4), buttonWidth, buttonHeight).build());

        int contentX = guiLeft + 160;
        int contentY = (int) (guiTop + 75 - scrollOffset);

        if (!currentTab.equals("Dashboard")) {
            boolean isActive = switch (currentTab) {
                case "Aim Assist" -> CiroxClientMod.aimAssistActive;
                case "Triggerbot" -> CiroxClientMod.triggerbotActive;
                case "Jump Reset" -> CiroxClientMod.jumpResetActive;
                case "HitBox Exp" -> CiroxClientMod.hitboxActive;
                default -> false;
            };

            // Botón de Estado (Verde si está activo, Rojo si está apagado)
            String toggleText = isActive ? "Estado: §a[ACTIVO]" : "Estado: §c[APAGADO]";
            this.addDrawableChild(ButtonWidget.builder(Text.literal(toggleText), b -> {
                switch (currentTab) {
                    case "Aim Assist" -> CiroxClientMod.aimAssistActive = !CiroxClientMod.aimAssistActive;
                    case "Triggerbot" -> CiroxClientMod.triggerbotActive = !CiroxClientMod.triggerbotActive;
                    case "Jump Reset" -> CiroxClientMod.jumpResetActive = !CiroxClientMod.jumpResetActive;
                    case "HitBox Exp" -> CiroxClientMod.hitboxActive = !CiroxClientMod.hitboxActive;
                }
                rebuildWidgets();
            }).dimensions(contentX, contentY, 280, 24).build());

            // Controles de configuración independientes por módulo
            if (currentTab.equals("Aim Assist")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("FOV Límite: " + CiroxClientMod.aimFov + "°"), b -> {
                    CiroxClientMod.aimFov = CiroxClientMod.aimFov >= 45.0f ? 15.0f : CiroxClientMod.aimFov + 5.0f;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 280, 24).build());

                this.addDrawableChild(ButtonWidget.builder(Text.literal("Suavizado (Speed): " + CiroxClientMod.aimSpeed), b -> {
                    CiroxClientMod.aimSpeed = CiroxClientMod.aimSpeed >= 7.0f ? 2.0f : CiroxClientMod.aimSpeed + 0.5f;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 68, 280, 24).build());

            } else if (currentTab.equals("Triggerbot")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("CPS Máximo: " + CiroxClientMod.triggerCps), b -> {
                    CiroxClientMod.triggerCps = CiroxClientMod.triggerCps >= 14 ? 6 : CiroxClientMod.triggerCps + 1;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 280, 24).build());

            } else if (currentTab.equals("Jump Reset")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("Probabilidad W-Tap: " + CiroxClientMod.jumpResetChance + "%"), b -> {
                    CiroxClientMod.jumpResetChance = CiroxClientMod.jumpResetChance >= 100 ? 60 : CiroxClientMod.jumpResetChance + 10;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 280, 24).build());

            } else if (currentTab.equals("HitBox Exp")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("Expansión Segura: +" + CiroxClientMod.hitboxExpansion), b -> {
                    double nextExp = CiroxClientMod.hitboxExpansion >= 0.08 ? 0.02 : CiroxClientMod.hitboxExpansion + 0.01;
                    CiroxClientMod.hitboxExpansion = Math.round(nextExp * 100.0) / 100.0;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 280, 24).build());
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scrollOffset = Math.max(0.0, Math.min(maxScroll, scrollOffset - (amount * 20.0)));
        rebuildWidgets();
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int guiLeft = (this.width - 480) / 2;
        int guiTop = (this.height - 310) / 2;

        // Estética visual: Fondo Rojo Carmesí Oscuro con bordes e interiores limpios
        context.fill(guiLeft, guiTop, guiLeft + 480, guiTop + 310, 0xEE1C0B0B); // Fondo rojo base
        context.fill(guiLeft, guiTop, guiLeft + 480, guiTop + 50, 0xFF361010);   // Barra superior
        context.fill(guiLeft, guiTop + 60, guiLeft + 150, guiTop + 295, 0xFF2A0D0D); // Sidebar izquierdo
        context.drawBorder(guiLeft, guiTop, 480, 310, 0xFFFF4444); // Borde rojo brillante

        context.drawText(this.textRenderer, Text.literal("§lCirox Client // Grim & Vulcan Bypass"), guiLeft + 18, guiTop + 18, 0xFF6666, true);
        context.drawText(this.textRenderer, Text.literal("v7.2"), guiLeft + 145, guiTop + 20, 0xAA8888, true);

        if (currentTab.equals("Dashboard")) {
            int textX = guiLeft + 165;
            int textY = guiTop + 75;
            context.drawText(this.textRenderer, Text.literal("§lPanel de Control Anti-Heurístico"), textX, textY, 0xFFFFFF, true);
            context.drawText(this.textRenderer, Text.literal("Usa la rueda del ratón para explorar opciones."), textX, textY + 20, 0xBB8888, false);

            int statusY = textY + 60;
            context.drawText(this.textRenderer, Text.literal("Aim Assist: " + (CiroxClientMod.aimAssistActive ? "§a[ACTIVO]" : "§c[APAGADO]")), textX, statusY, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("Triggerbot: " + (CiroxClientMod.triggerbotActive ? "§a[ACTIVO]" : "§c[APAGADO]")), textX, statusY + 25, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("Jump Reset: " + (CiroxClientMod.jumpResetActive ? "§a[ACTIVO]" : "§c[APAGADO]")), textX, statusY + 50, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("HitBox Exp: " + (CiroxClientMod.hitboxActive ? "§a[ACTIVO]" : "§c[APAGADO]")), textX, statusY + 75, 0xDDDDDD, false);
        } else {
            int textX = guiLeft + 165;
            int textY = guiTop + 80 - (int)scrollOffset;
            context.drawText(this.textRenderer, Text.literal("§lMódulo: " + currentTab), textX, textY, 0xFF6666, true);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
