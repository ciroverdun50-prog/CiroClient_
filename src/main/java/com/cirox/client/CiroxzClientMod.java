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
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CiroxzClientMod implements ClientModInitializer {
private static KeyBinding openGuiKey;

@Override
public void onInitializeClient() {
    openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.ciroxzclient.open_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.ciroxzclient.title"
    ));

    ClientTickEvents.END_CLIENT_TICK.register(client -> {
        while (openGuiKey.wasPressed()) {
            if (client.currentScreen == null) {
                client.setScreen(new CiroxzClientScreen());
            }
        }
    });
}

public static class CiroxzClientScreen extends Screen {
    private String currentTab = "Dashboard";
    private final Map<String, Map<String, Object>> configurations = new HashMap<>();

    private boolean aimAssistActive = false;
    private boolean triggerbotActive = false;
    private boolean jumpResetActive = false;
    private boolean hitboxActive = false;

    public CiroxzClientScreen() {
        super(Text.literal("Ciroxz Client // Fabric Core"));
        initConfigs();
    }

    private void initConfigs() {
        configurations.put("AimAssist", createDefaultAimConfig());
        configurations.put("Triggerbot", createDefaultTriggerConfig());
        configurations.put("JumpReset", createDefaultJumpResetConfig());
        configurations.put("HitBox", createDefaultHitBoxConfig());
    }

    private Map<String, Object> createDefaultAimConfig() {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put("FOV", 38.5);
        cfg.put("Speed", 4.1);
        cfg.put("SmoothingCurve", "Gaussian Dynamic");
        return cfg;
    }

    private Map<String, Object> createDefaultTriggerConfig() {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put("CPS", 12);
        cfg.put("JitterMS", 35);
        cfg.put("RaycastCheck", true);
        return cfg;
    }

    private Map<String, Object> createDefaultJumpResetConfig() {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put("Chance", 90);
        cfg.put("VelocityModifier", 0.58);
        return cfg;
    }

    private Map<String, Object> createDefaultHitBoxConfig() {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put("ExpansionX", 0.12);
        cfg.put("ExpansionZ", 0.12);
        cfg.put("StrictRaycast", true);
        return cfg;
    }

    @Override
    protected void init() {
        int sidebarX = 25;
        int startY = 60;
        int buttonWidth = 120;
        int buttonHeight = 24;
        int spacing = 28;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Dashboard"), b -> currentTab = "Dashboard")
                .dimensions(sidebarX, startY, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Aim Assist"), b -> currentTab = "Aim Assist")
                .dimensions(sidebarX, startY + spacing, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Triggerbot"), b -> currentTab = "Triggerbot")
                .dimensions(sidebarX, startY + (spacing * 2), buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Jump Reset"), b -> currentTab = "Jump Reset")
                .dimensions(sidebarX, startY + (spacing * 3), buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("HitBox Exp"), b -> currentTab = "HitBox Exp")
                .dimensions(sidebarX, startY + (spacing * 4), buttonWidth, buttonHeight).build());

        // Main Action Toggle Button
        int contentX = 175;
        int contentY = 120;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Toggle Module Status"), b -> {
            switch (currentTab) {
                case "Aim Assist" -> {
                    aimAssistActive = !aimAssistActive;
                    if (aimAssistActive) {
                        double fov = (double) configurations.get("AimAssist").get("FOV");
                        double speed = (double) configurations.get("AimAssist").get("Speed");
                        double vector = computeGaussianSmoothing(fov, 0.0, speed);
                        System.out.println("[AimAssist] Vector calculated: " + vector);
                    }
                }
                case "Triggerbot" -> {
                    triggerbotActive = !triggerbotActive;
                    if (triggerbotActive) {
                        int cps = (int) configurations.get("Triggerbot").get("CPS");
                        boolean hit = evaluateTriggerbotHit(System.currentTimeMillis() - 100, cps);
                        System.out.println("[Triggerbot] Executed: " + hit);
                    }
                }
                case "Jump Reset" -> {
                    jumpResetActive = !jumpResetActive;
                    if (jumpResetActive) {
                        int chance = (int) configurations.get("JumpReset").get("Chance");
                        boolean reset = evaluateJumpReset(chance);
                        System.out.println("[JumpReset] Triggered: " + reset);
                    }
                }
                case "HitBox Exp" -> {
                    hitboxActive = !hitboxActive;
                    if (hitboxActive) {
                        double expX = (double) configurations.get("HitBox").get("ExpansionX");
                        System.out.println("[HitBox] Expansion offset: +" + expX);
                    }
                }
            }
        }).dimensions(contentX, contentY + 40, 180, 24).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        // Background panel container
        context.fill(15, 15, this.width - 15, this.height - 15, 0xEE121217);
        context.drawBorder(15, 15, this.width - 30, this.height - 30, 0xFF353545);

        // Top Bar Title
        context.drawText(this.textRenderer, Text.literal("CIROXZ CLIENT // FABRIC KERNEL ARCHITECTURE"), 25, 25, 0xDCDCE0, true);

        // Tab Header
        context.drawText(this.textRenderer, Text.literal("Active Module: " + currentTab), 175, 65, 0x9090A0, true);

        // Status message
        String statusText = switch (currentTab) {
            case "Aim Assist" -> "Status: " + (aimAssistActive ? "ACTIVE (Gaussian Curve)" : "OFF");
            case "Triggerbot" -> "Status: " + (triggerbotActive ? "ACTIVE (Jitter Delay)" : "OFF");
            case "Jump Reset" -> "Status: " + (jumpResetActive ? "ACTIVE (Friction Mimic)" : "OFF");
            case "HitBox Exp" -> "Status: " + (hitboxActive ? "ACTIVE (Strict Raycast)" : "OFF");
            default -> "System Status: Operational & Operational Handlers Loaded";
        };

        int statusColor = aimAssistActive || triggerbotActive || jumpResetActive || hitboxActive ? 0x64DC78 : 0xDC6464;
        if (currentTab.equals("Dashboard")) statusColor = 0x64DC78;

        context.drawText(this.textRenderer, Text.literal(statusText), 175, 95, statusColor, true);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

public static double computeGaussianSmoothing(double targetAngle, double currentAngle, double smoothingFactor) {
    double delta = targetAngle - currentAngle;
    double randomizedVariance = ThreadLocalRandom.current().nextGaussian() * 0.18;
    return currentAngle + (delta / smoothingFactor) + randomizedVariance;
}

public static boolean evaluateTriggerbotHit(long lastHitTimeMillis, int targetCps) {
    long delayThreshold = 1000L / targetCps;
    long elapsed = System.currentTimeMillis() - lastHitTimeMillis;
    long jitteredThreshold = (long)(delayThreshold + (ThreadLocalRandom.current().nextDouble() * 40 - 20));
    return elapsed >= jitteredThreshold;
}

public static boolean evaluateJumpReset(int chancePercent) {
    int roll = ThreadLocalRandom.current().nextInt(100);
    return roll < chancePercent;
}
}
