package com.cirox.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CiroxzClientMod implements ClientModInitializer {
private static KeyBinding openGuiKey;

public static boolean aimAssistActive = false;
public static boolean triggerbotActive = false;
public static boolean jumpResetActive = false;
public static boolean hitboxActive = false;

public static final Map<String, Map<String, Object>> configurations = new HashMap<>();

@Override
public void onInitializeClient() {
    openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.ciroxzclient.open_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.ciroxzclient.title"
    ));

    initConfigs();

    ClientTickEvents.END_CLIENT_TICK.register(client -> {
        while (openGuiKey.wasPressed()) {
            if (client.currentScreen == null) {
                client.setScreen(new CiroxzClientScreen());
            }
        }

        if (client.player == null || client.world == null) return;

        if (triggerbotActive && client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) client.crosshairTarget;
            int cps = (int) configurations.get("Triggerbot").get("CPS");
            if (evaluateTriggerbotTick(cps)) {
                client.interactionManager.attackEntity(client.player, entityHit.getEntity());
                client.player.swingHand(client.player.getActiveHand());
            }
        }

        if (jumpResetActive && client.player.hurtTime > 0) {
            int chance = (int) configurations.get("JumpReset").get("Chance");
            if (ThreadLocalRandom.current().nextInt(100) < chance) {
                client.options.forwardKey.setPressed(false);
            }
        }
    });
}

private void initConfigs() {
    Map<String, Object> aim = new HashMap<>();
    aim.put("FOV", 35.0);
    aim.put("Speed", 3.2);
    configurations.put("AimAssist", aim);

    Map<String, Object> trig = new HashMap<>();
    trig.put("CPS", 10);
    configurations.put("Triggerbot", trig);

    Map<String, Object> jr = new HashMap<>();
    jr.put("Chance", 85);
    configurations.put("JumpReset", jr);

    Map<String, Object> hb = new HashMap<>();
    hb.put("Expansion", 0.05);
    configurations.put("HitBox", hb);
}

private static long lastAttackTick = 0;
private static boolean evaluateTriggerbotTick(int targetCps) {
    long currentMillis = System.currentTimeMillis();
    long interval = 1000L / targetCps;
    long jitter = ThreadLocalRandom.current().nextLong(25, 60);
    if (currentMillis - lastAttackTick >= (interval + jitter)) {
        lastAttackTick = currentMillis;
        return true;
    }
    return false;
}

public static class CiroxzClientScreen extends Screen {
    private String currentTab = "Dashboard";

    public CiroxzClientScreen() {
        super(Text.literal("Ciroxz Client"));
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
                case "Aim Assist" -> aimAssistActive;
                case "Triggerbot" -> triggerbotActive;
                case "Jump Reset" -> jumpResetActive;
                case "HitBox Exp" -> hitboxActive;
                default -> false;
            };

            String toggleText = isActive ? "Estado: Activo" : "Estado: Apagado";
            this.addDrawableChild(ButtonWidget.builder(Text.literal(toggleText), b -> {
                switch (currentTab) {
                    case "Aim Assist" -> aimAssistActive = !aimAssistActive;
                    case "Triggerbot" -> triggerbotActive = !triggerbotActive;
                    case "Jump Reset" -> jumpResetActive = !jumpResetActive;
                    case "HitBox Exp" -> hitboxActive = !hitboxActive;
                }
                rebuildWidgets();
            }).dimensions(contentX, contentY, 275, 24).build());

            if (currentTab.equals("Aim Assist")) {
                double fov = (double) configurations.get("AimAssist").get("FOV");
                double speed = (double) configurations.get("AimAssist").get("Speed");

                this.addDrawableChild(ButtonWidget.builder(Text.literal("FOV Límite: " + fov), b -> {
                    double nextFov = fov >= 50.0 ? 20.0 : fov + 5.0;
                    configurations.get("AimAssist").put("FOV", nextFov);
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 275, 24).build());

                this.addDrawableChild(ButtonWidget.builder(Text.literal("Velocidad: " + speed), b -> {
                    double nextSpeed = speed >= 6.0 ? 2.0 : speed + 0.5;
                    configurations.get("AimAssist").put("Speed", nextSpeed);
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 68, 275, 24).build());

            } else if (currentTab.equals("Triggerbot")) {
                int cps = (int) configurations.get("Triggerbot").get("CPS");

                this.addDrawableChild(ButtonWidget.builder(Text.literal("CPS Límite: " + cps), b -> {
                    int nextCps = cps >= 16 ? 8 : cps + 1;
                    configurations.get("Triggerbot").put("CPS", nextCps);
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 275, 24).build());

            } else if (currentTab.equals("Jump Reset")) {
                int chance = (int) configurations.get("JumpReset").get("Chance");

                this.addDrawableChild(ButtonWidget.builder(Text.literal("Probabilidad W-Tap: " + chance + "%"), b -> {
                    int nextChance = chance >= 100 ? 60 : chance + 10;
                    configurations.get("JumpReset").put("Chance", nextChance);
                    rebuildWidgets();
                }).dimensions(contentX, contentY + 34, 275, 24).build());

            } else if (currentTab.equals("HitBox Exp")) {
                double exp = (double) configurations.get("HitBox").get("Expansion");

                this.addDrawableChild(ButtonWidget.builder(Text.literal("Expansión Segura: +" + exp), b -> {
                    double nextExp = exp >= 0.10 ? 0.02 : exp + 0.02;
                    configurations.get("HitBox").put("Expansion", Math.round(nextExp * 100.0) / 100.0);
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

        context.fill(guiLeft, guiTop, guiLeft + 440, guiTop + 280, 0xEE141416);
        context.fill(guiLeft, guiTop, guiLeft + 440, guiTop + 45, 0xFF1C1C21);
        context.fill(guiLeft, guiTop + 55, guiLeft + 135, guiTop + 265, 0xFF18181D);
        context.drawBorder(guiLeft, guiTop, 440, 280, 0xFF32323D);

        context.drawText(this.textRenderer, Text.literal("Ciroxz Client"), guiLeft + 18, guiTop + 16, 0xFFFFFF, true);
        context.drawText(this.textRenderer, Text.literal("v5.4"), guiLeft + 98, guiTop + 18, 0x888899, true);

        if (currentTab.equals("Dashboard")) {
            int textX = guiLeft + 145;
            int textY = guiTop + 75;
            context.drawText(this.textRenderer, Text.literal("Panel Principal"), textX, textY, 0xFFFFFF, true);
            context.drawText(this.textRenderer, Text.literal("Cliente configurado para estabilidad en servidores."), textX, textY + 20, 0x888899, false);

            int statusY = textY + 60;
            context.drawText(this.textRenderer, Text.literal("Aim Assist: " + (aimAssistActive ? "§aActivado" : "§7Apagado")), textX, statusY, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("Triggerbot: " + (triggerbotActive ? "§aActivado" : "§7Apagado")), textX, statusY + 20, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("Jump Reset: " + (jumpResetActive ? "§aActivado" : "§7Apagado")), textX, statusY + 40, 0xDDDDDD, false);
            context.drawText(this.textRenderer, Text.literal("HitBox Exp: " + (hitboxActive ? "§aActivado" : "§7Apagado")), textX, statusY + 60, 0xDDDDDD, false);
        } else {
            int textX = guiLeft + 145;
            int textY = guiTop + 200;
            context.drawText(this.textRenderer, Text.literal("Módulo: " + currentTab), textX, textY, 0xAAAAAA, false);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
}
