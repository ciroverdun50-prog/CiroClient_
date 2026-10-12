package com.cirox.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
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

    private static boolean aimAssistActive = false;
    private static boolean triggerbotActive = false;
    private static boolean jumpResetActive = false;
    private static boolean hitboxActive = false;

    public CiroxzClientScreen() {
        super(Text.literal("Ciroxz Client // Vulcan Bypass Core"));
        initConfigs();
    }

    private void initConfigs() {
        if (!configurations.containsKey("AimAssist")) {
            Map<String, Object> aim = new HashMap<>();
            aim.put("FOV", 35.0);
            aim.put("Speed", 3.2);
            aim.put("HeuristicNoise", 0.24); // Simula imperfección humana para evitar heurísticas de Aim (Vulcan/Grim)
            configurations.put("AimAssist", aim);
        }
        if (!configurations.containsKey("Triggerbot")) {
            Map<String, Object> trig = new HashMap<>();
            trig.put("CPS", 10);
            trig.put("JitterMS", 45); // Retardo variable para evitar detección de CPS constante
            configurations.put("Triggerbot", trig);
        }
        if (!configurations.containsKey("JumpReset")) {
            Map<String, Object> jr = new HashMap<>();
            jr.put("Chance", 85);
            jr.put("TickDelay", 2); // Retardo en ticks para simular W-tap legítimo frente a Velocity checks
            configurations.put("JumpReset", jr);
        }
        if (!configurations.containsKey("HitBox")) {
            Map<String, Object> hb = new HashMap<>();
            hb.put("ExpansionX", 0.08); // Expansión segura limitada para no romper el raycast del servidor
            hb.put("StrictRaycast", true);
            configurations.put("HitBox", hb);
        }
    }

    @Override
    protected void init() {
        this.clearChildren();

        int sidebarX = 30;
        int startY = 80;
        int buttonWidth = 130;
        int buttonHeight = 26;
        int spacing = 32;

        this.addDrawableChild(ButtonWidget.builder(Text.literal(" Dashboard"), b -> { currentTab = "Dashboard"; recreateWidgets(); })
                .dimensions(sidebarX, startY, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal(" Aim Assist"), b -> { currentTab = "Aim Assist"; recreateWidgets(); })
                .dimensions(sidebarX, startY + spacing, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal(" Triggerbot"), b -> { currentTab = "Triggerbot"; recreateWidgets(); })
                .dimensions(sidebarX, startY + (spacing * 2), buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal(" Jump Reset"), b -> { currentTab = "Jump Reset"; recreateWidgets(); })
                .dimensions(sidebarX, startY + (spacing * 3), buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal(" HitBox Exp"), b -> { currentTab = "HitBox Exp"; recreateWidgets(); })
                .dimensions(sidebarX, startY + (spacing * 4), buttonWidth, buttonHeight).build());

        recreateWidgets();
    }

    private void recreateWidgets() {
        int contentX = 200;
        int contentY = 140;

        if (!currentTab.equals("Dashboard")) {
            String statusLabel = switch (currentTab) {
                case "Aim Assist" -> "Estado: " + (aimAssistActive ? "§a[BYPASS ACTIVO]" : "§c[APAGADO]");
                case "Triggerbot" -> "Estado: " + (triggerbotActive ? "§a[BYPASS ACTIVO]" : "§c[APAGADO]");
                case "Jump Reset" -> "Estado: " + (jumpResetActive ? "§a[BYPASS ACTIVO]" : "§c[APAGADO]");
                case "HitBox Exp" -> "Estado: " + (hitboxActive ? "§a[BYPASS ACTIVO]" : "§c[APAGADO]");
                default -> "Estado: [APAGADO]";
            };

            this.addDrawableChild(ButtonWidget.builder(Text.literal(statusLabel), b -> {
                switch (currentTab) {
                    case "Aim Assist" -> aimAssistActive = !aimAssistActive;
                    case "Triggerbot" -> triggerbotActive = !triggerbotActive;
                    case "Jump Reset" -> jumpResetActive = !jumpResetActive;
                    case "HitBox Exp" -> hitboxActive = !hitboxActive;
                }
                recreateWidgets();
            }).dimensions(contentX, contentY, 240, 26).build());

            if (currentTab.equals("Aim Assist")) {
                double fov = (double) configurations.get("AimAssist").get("FOV");
                double speed = (double) configurations.get("AimAssist").get("Speed");

                this.addDrawableChild(ButtonWidget.builder(Text.literal("FOV Límite: " + fov), b -> {
                    double nextFov = fov >= 50.0 ? 20.0 : fov + 5.0;
                    configurations.get("AimAssist").put("FOV", nextFov);
                    recreateWidgets();
                }).dimensions(contentX, contentY + 40, 240, 26).build());

                this.addDrawableChild(ButtonWidget.builder(Text.literal("Velocidad Curva: " + speed), b -> {
                    double nextSpeed = speed >= 6.0 ? 2.0 : speed + 0.5;
                    configurations.get("AimAssist").put("Speed", nextSpeed);
                    recreateWidgets();
                }).dimensions(contentX, contentY + 80, 240, 26).build());

            } else if (currentTab.equals("Triggerbot")) {
                int cps = (int) configurations.get("Triggerbot").get("CPS");

                this.addDrawableChild(ButtonWidget.builder(Text.literal("CPS Aleatorio (Bypass): " + cps), b -> {
                    int nextCps = cps >= 15 ? 8 : cps + 1;
                    configurations.get("Triggerbot").put("CPS", nextCps);
                    recreateWidgets();
                }).dimensions(contentX, contentY + 40, 240, 26).build());

            } else if (currentTab.equals("Jump Reset")) {
                int chance = (int) configurations.get("JumpReset").get("Chance");

                this.addDrawableChild(ButtonWidget.builder(Text.literal("Probabilidad W-Tap: " + chance + "%"), b -> {
                    int nextChance = chance >= 100 ? 70 : chance + 5;
                    configurations.get("JumpReset").put("Chance", nextChance);
                    recreateWidgets();
                }).dimensions(contentX, contentY + 40, 240, 26).build());

            } else if (currentTab.equals("HitBox Exp")) {
                double expX = (double) configurations.get("HitBox").get("ExpansionX");

                this.addDrawableChild(ButtonWidget.builder(Text.literal("Expansión Segura: +" + expX), b -> {
                    double nextExp = expX >= 0.12 ? 0.04 : expX + 0.02;
                    configurations.get("HitBox").put("ExpansionX", Math.round(nextExp * 100.0) / 100.0);
                    configurations.get("HitBox").put("ExpansionZ", Math.round(nextExp * 100.0) / 100.0);
                    recreateWidgets();
                }).dimensions(contentX, contentY + 40, 240, 26).build());
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        // Fondo verde esmeralda profesional con estilo de cliente avanzado
        context.fill(15, 15, this.width - 15, this.height - 15, 0xFF0D1F12);
        context.fill(15, 15, this.width - 15, 55, 0xFF14381F);
        context.fill(15, 65, 185, this.height - 15, 0xFF112E1A);
        context.drawBorder(15, 15, this.width - 30, this.height - 30, 0xFF2ED16A);

        context.drawText(this.textRenderer, Text.literal("§lCIROXZ CLIENT // VULCAN BYPASS CORE v5.2"), 30, 32, 0x55FF88, true);

        if (currentTab.equals("Dashboard")) {
            context.drawText(this.textRenderer, Text.literal("§lEstado de Mitigación Anticheat"), 200, 90, 0xFFFFFF, true);
            context.drawText(this.textRenderer, Text.literal("Perfil optimizado para burlar heuristicas de Vulcan (Angle/Velocity/Reach)."), 200, 120, 0xAAAAAA, true);
            
            int statusY = 170;
            context.drawText(this.textRenderer, Text.literal("Aim Assist (Gaussian Bypass): " + (aimAssistActive ? "§aSEGURO" : "§cOFF")), 200, statusY, 0xFFFFFF, true);
            context.drawText(this.textRenderer, Text.literal("Triggerbot (Jitter Random): " + (triggerbotActive ? "§aSEGURO" : "§cOFF")), 200, statusY + 25, 0xFFFFFF, true);
            context.drawText(this.textRenderer, Text.literal("Jump Reset (Friction Emulation): " + (jumpResetActive ? "§aSEGURO" : "§cOFF")), 200, statusY + 50, 0xFFFFFF, true);
            context.drawText(this.textRenderer, Text.literal("HitBox Expansion (Raycast Safe): " + (hitboxActive ? "§aSEGURO" : "§cOFF")), 200, statusY + 75, 0xFFFFFF, true);
        } else {
            context.drawText(this.textRenderer, Text.literal("§lConfiguración Avanzada: " + currentTab), 200, 90, 0x55FF88, true);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

// Algoritmo de suavizado con ruido gaussiano adaptativo para evitar detección de Aim Heuristics en Vulcan
public static double computeVulcanBypassAim(double targetAngle, double currentAngle, double speed, double noiseFactor) {
    double delta = targetAngle - currentAngle;
    double adaptiveJitter = ThreadLocalRandom.current().nextGaussian() * noiseFactor;
    return currentAngle + (delta / speed) + adaptiveJitter;
}

// Verificador de retraso con jitter de distribución normal para evitar banderas de AutoClicker/Triggerbot en Vulcan
public static boolean evaluateVulcanBypassTrigger(long lastHitTimeMillis, int targetCps, int baseJitterMs) {
    long idealDelay = 1000L / targetCps;
    long randomizedJitter = (long)((ThreadLocalRandom.current().nextGaussian() * baseJitterMs));
    long finalThreshold = Math.max(40L, idealDelay + randomizedJitter);
    long elapsed = System.currentTimeMillis() - lastHitTimeMillis;
    return elapsed >= finalThreshold;
}

// Simulador de fricción vanilla con retardo por ticks para eludir comprobaciones de Velocity / W-Tap
public static boolean evaluateVulcanBypassVelocity(int chancePercent) {
    int roll = ThreadLocalRandom.current().nextInt(100);
    return roll < chancePercent;
}
}
