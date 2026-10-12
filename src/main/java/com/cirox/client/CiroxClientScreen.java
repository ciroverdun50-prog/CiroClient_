package com.cirox.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CiroxClientScreen extends Screen {
    private String currentTab = "Aim Assist";
    private double scrollOffset = 0.0;
    private final double maxScroll = 160.0;

    public CiroxClientScreen() {
        super(Text.literal("Cirox Client Menu"));
    }

    @Override
    protected void init() {
        this.clearChildren();
        rebuildWidgets();
    }

    private void rebuildWidgets() {
        this.clearChildren();

        int guiLeft = (this.width - 500) / 2;
        int guiTop = (this.height - 320) / 2;
        int sidebarX = guiLeft + 18;
        int startY = guiTop + 75;
        int buttonWidth = 130;
        int buttonHeight = 24;
        int spacing = 28;

        // Pestañas de navegación
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

        int contentX = guiLeft + 165;
        int contentY = (int) (guiTop + 75 - scrollOffset);

        if (!currentTab.equals("Dashboard")) {
            boolean isActive = switch (currentTab) {
                case "Aim Assist" -> CiroxClientMod.aimAssistActive;
                case "Triggerbot" -> CiroxClientMod.triggerbotActive;
                case "Jump Reset" -> CiroxClientMod.jumpResetActive;
                case "HitBox Exp" -> CiroxClientMod.hitboxActive;
                default -> false;
            };

            // Activar/Desactivar Módulo
            String toggleText = isActive ? "Estado: §a[ACTIVO]" : "Estado: §c[APAGADO]";
            this.addDrawableChild(ButtonWidget.builder(Text.literal(toggleText), b -> {
                switch (currentTab) {
                    case "Aim Assist" -> CiroxClientMod.aimAssistActive = !CiroxClientMod.aimAssistActive;
                    case "Triggerbot" -> CiroxClientMod.triggerbotActive = !CiroxClientMod.triggerbotActive;
                    case "Jump Reset" -> CiroxClientMod.jumpResetActive = !CiroxClientMod.jumpResetActive;
                    case "HitBox Exp" -> CiroxClientMod.hitboxActive = !CiroxClientMod.hitboxActive;
                }
                rebuildWidgets();
            }).dimensions(contentX, contentY, 300, 24).build());

            // Controles de Aim Assist
            if (currentTab.equals("Aim Assist")) {
                // Control FOV
                this.addDrawableChild(ButtonWidget.builder(Text.literal("Rango FOV: " + CiroxClientMod.aimFov + "°"), b -> {
                    CiroxClientMod.aimFov = CiroxClientMod.aimFov >= 90.0f ? 15.0f : CiroxClientMod.aimFov + 15.0f;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 300, 24).build());

                // Control Distancia (Reach)
                this.addDrawableChild(ButtonWidget.builder(Text.literal("Distancia Máxima: " + CiroxClientMod.aimDistance + " bloques"), b -> {
                    CiroxClientMod.aimDistance = CiroxClientMod.aimDistance >= 6.0f ? 3.0f : Math.round((CiroxClientMod.aimDistance + 0.5f) * 10.0f) / 10.0f;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 68, 300, 24).build());

                // Control Velocidad (Suavizado)
                this.addDrawableChild(ButtonWidget.builder(Text.literal("Suavizado Velocidad: " + CiroxClientMod.aimSpeed), b -> {
                    CiroxClientMod.aimSpeed = CiroxClientMod.aimSpeed >= 10.0f ? 1.0f : CiroxClientMod.aimSpeed + 1.0f;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 102, 300, 24).build());

                // Casilla / Botón Only Sword
                String swordText = "Filtro Only Sword: " + (CiroxClientMod.onlySword ? "§a[SI]" : "§c[NO]");
                this.addDrawableChild(ButtonWidget.builder(Text.literal(swordText), b -> {
                    CiroxClientMod.onlySword = !CiroxClientMod.onlySword;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 136, 300, 24).build());

            } else if (currentTab.equals("Triggerbot")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("CPS Máximo: " + CiroxClientMod.triggerCps), b -> {
                    CiroxClientMod.triggerCps = CiroxClientMod.triggerCps >= 16 ? 6 : CiroxClientMod.triggerCps + 1;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 300, 24).build());

            } else if (currentTab.equals("Jump Reset")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("Probabilidad W-Tap: " + CiroxClientMod.jumpResetChance + "%"), b -> {
                    CiroxClientMod.jumpResetChance = CiroxClientMod.jumpResetChance >= 100 ? 50 : CiroxClientMod.jumpResetChance + 10;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 300, 24).build());

            } else if (currentTab.equals("HitBox Exp")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("Expansión Segura: +" + CiroxClientMod.hitboxExpansion), b -> {
                    double nextExp = CiroxClientMod.hitboxExpansion >= 0.12 ? 0.02 : CiroxClientMod.hitboxExpansion + 0.02;
                    CiroxClientMod.hitboxExpansion = Math.round(nextExp * 100.0) / 100.0;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 300, 24).build());
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

        int guiLeft = (this.width - 500) / 2;
        int guiTop = (this.height - 320) / 2;

        // Estilo Rojo Oscuro Carmesí
        context.fill(guiLeft, guiTop, guiLeft + 500, guiTop + 320, 0xEE1C0B0B);
        context.fill(guiLeft, guiTop, guiLeft + 500, guiTop + 50, 0xFF361010);
        context.fill(guiLeft, guiTop + 60, guiLeft + 155, guiTop + 305, 0xFF2A0D0D);
        context.drawBorder(guiLeft, guiTop, 500, 320, 0xFFFF3333);

        context.drawText(this.textRenderer, Text.literal("§lCirox Client // AimAssist Precision"), guiLeft + 18, guiTop + 18, 0xFF6666, true);

        if (currentTab.equals("Dashboard")) {
            int textX = guiLeft + 170;
            int textY = guiTop + 75;
            context.drawText(this.textRenderer, Text.literal("§lPanel de Configuración"), textX, textY, 0xFFFFFF, true);
            
            int statusY = textY + 40;
            context.drawText(this.textRenderer, Text.literal("Aim Assist: " + (CiroxClientMod.aimAssistActive ? "§a[ACTIVO]" : "§c[APAGADO]")), textX, statusY, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("Filtro Espada: " + (CiroxClientMod.onlySword ? "§a[ACTIVADO]" : "§7[DESACTIVADO]")), textX, statusY + 20, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("FOV Aim: " + CiroxClientMod.aimFov + "°"), textX, statusY + 40, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("Rango Detección: " + CiroxClientMod.aimDistance + " bloques"), textX, statusY + 60, 0xDDDDDD, false);
        } else {
            int textX = guiLeft + 170;
            int textY = guiTop + 80 - (int)scrollOffset;
            context.drawText(this.textRenderer, Text.literal("§lAjustes de " + currentTab), textX, textY, 0xFF6666, true);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
