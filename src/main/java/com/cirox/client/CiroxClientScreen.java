package com.cirox.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CiroxClientScreen extends Screen {
    private String currentTab = "Dashboard";
    private double scrollOffset = 0.0;
    private final double maxScroll = 120.0;

    public CiroxClientScreen() {
        super(Text.literal("Cirox Client GUI"));
    }

    @Override
    protected void init() {
        this.clearChildren();
        rebuildWidgets();
    }

    private void rebuildWidgets() {
        this.clearChildren();

        int guiLeft = (this.width - 460) / 2;
        int guiTop = (this.height - 300) / 2;
        int sidebarX = guiLeft + 18;
        int startY = guiTop + 75;
        int buttonWidth = 120;
        int buttonHeight = 24;
        int spacing = 28;

        // Botones del Sidebar interactivos
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

        int contentX = guiLeft + 155;
        int contentY = (int) (guiTop + 75 - scrollOffset);

        if (!currentTab.equals("Dashboard")) {
            boolean isActive = switch (currentTab) {
                case "Aim Assist" -> CiroxClientMod.aimAssistActive;
                case "Triggerbot" -> CiroxClientMod.triggerbotActive;
                case "Jump Reset" -> CiroxClientMod.jumpResetActive;
                case "HitBox Exp" -> CiroxClientMod.hitboxActive;
                default -> false;
            };

            // Botón de Estado (Se pone verde brillante si está activo, rojo/apagado si no)
            String toggleText = isActive ? "Estado: §a[ACTIVO]" : "Estado: §c[APAGADO]";
            this.addDrawableChild(ButtonWidget.builder(Text.literal(toggleText), b -> {
                switch (currentTab) {
                    case "Aim Assist" -> CiroxClientMod.aimAssistActive = !CiroxClientMod.aimAssistActive;
                    case "Triggerbot" -> CiroxClientMod.triggerbotActive = !CiroxClientMod.triggerbotActive;
                    case "Jump Reset" -> CiroxClientMod.jumpResetActive = !CiroxClientMod.jumpResetActive;
                    case "HitBox Exp" -> CiroxClientMod.hitboxActive = !CiroxClientMod.hitboxActive;
                }
                rebuildWidgets();
            }).dimensions(contentX, contentY, 265, 24).build());

            // Controles configurables reales
            if (currentTab.equals("Aim Assist")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("FOV Límite: " + CiroxClientMod.aimFov), b -> {
                    CiroxClientMod.aimFov = CiroxClientMod.aimFov >= 50.0f ? 20.0f : CiroxClientMod.aimFov + 5.0f;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 265, 24).build());

                this.addDrawableChild(ButtonWidget.builder(Text.literal("Velocidad Curva: " + CiroxClientMod.aimSpeed), b -> {
                    CiroxClientMod.aimSpeed = CiroxClientMod.aimSpeed >= 6.0f ? 1.5f : CiroxClientMod.aimSpeed + 0.5f;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 68, 265, 24).build());

            } else if (currentTab.equals("Triggerbot")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("CPS Límite: " + CiroxClientMod.triggerCps), b -> {
                    CiroxClientMod.triggerCps = CiroxClientMod.triggerCps >= 16 ? 6 : CiroxClientMod.triggerCps + 1;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 265, 24).build());

            } else if (currentTab.equals("Jump Reset")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("Probabilidad W-Tap: " + CiroxClientMod.jumpResetChance + "%"), b -> {
                    CiroxClientMod.jumpResetChance = CiroxClientMod.jumpResetChance >= 100 ? 50 : CiroxClientMod.jumpResetChance + 10;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 265, 24).build());

            } else if (currentTab.equals("HitBox Exp")) {
                this.addDrawableChild(ButtonWidget.builder(Text.literal("Expansión Real: +" + CiroxClientMod.hitboxExpansion), b -> {
                    double nextExp = CiroxClientMod.hitboxExpansion >= 0.12 ? 0.02 : CiroxClientMod.hitboxExpansion + 0.02;
                    CiroxClientMod.hitboxExpansion = Math.round(nextExp * 100.0) / 100.0;
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 265, 24).build());
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scrollOffset = Math.max(0.0, Math.min(maxScroll, scrollOffset - (amount * 18.0)));
        rebuildWidgets();
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        int guiLeft = (this.width - 460) / 2;
        int guiTop = (this.height - 300) / 2;

        // Fondo Rojo Carmesí Oscuro Profesional con acentos carmesí brillante
        context.fill(guiLeft, guiTop, guiLeft + 460, guiTop + 300, 0xEE1A0D0D); // Fondo rojo muy oscuro
        context.fill(guiLeft, guiTop, guiLeft + 460, guiTop + 50, 0xFF2E1010);   // Barra superior roja
        context.fill(guiLeft, guiTop + 60, guiLeft + 145, guiTop + 285, 0xFF240C0C); // Sidebar rojo
        context.drawBorder(guiLeft, guiTop, 460, 300, 0xFFFF3333); // Borde rojo neón

        context.drawText(this.textRenderer, Text.literal("§lCirox Client // Crimson Core"), guiLeft + 18, guiTop + 18, 0xFF5555, true);
        context.drawText(this.textRenderer, Text.literal("v6.0 [Scrollable]"), guiLeft + 130, guiTop + 20, 0xAA7777, true);

        if (currentTab.equals("Dashboard")) {
            int textX = guiLeft + 160;
            int textY = guiTop + 75;
            context.drawText(this.textRenderer, Text.literal("§lPanel de Control Principal"), textX, textY, 0xFFFFFF, true);
            context.drawText(this.textRenderer, Text.literal("Usa la rueda del ratón para desplazar los menús."), textX, textY + 20, 0xAA8888, false);

            int statusY = textY + 60;
            // Indicadores que se ponen verdes cuando están activos
            context.drawText(this.textRenderer, Text.literal("Aim Assist: " + (CiroxClientMod.aimAssistActive ? "§a[ACTIVO]" : "§c[APAGADO]")), textX, statusY, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("Triggerbot: " + (CiroxClientMod.triggerbotActive ? "§a[ACTIVO]" : "§c[APAGADO]")), textX, statusY + 25, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("Jump Reset: " + (CiroxClientMod.jumpResetActive ? "§a[ACTIVO]" : "§c[APAGADO]")), textX, statusY + 50, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("HitBox Exp: " + (CiroxClientMod.hitboxActive ? "§a[ACTIVO]" : "§c[APAGADO]")), textX, statusY + 75, 0xDDDDDD, false);
        } else {
            int textX = guiLeft + 160;
            int textY = guiTop + 80 - (int)scrollOffset;
            context.drawText(this.textRenderer, Text.literal("§lConfigurando: " + currentTab), textX, textY, 0xFF5555, true);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
