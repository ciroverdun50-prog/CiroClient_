package com.cirox.client.module;

import net.minecraft.client.MinecraftClient;

public abstract class Module {
    protected static MinecraftClient mc = MinecraftClient.getInstance();
    private String name;
    private String description;
    private Category category;
    private boolean enabled;
    private int key;

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
        this.key = 0;
    }

    public void toggle() {
        this.enabled = !this.enabled;
        if (this.enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick() {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public boolean isEnabled() { return enabled; }
    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }

    public enum Category {
        COMBAT, RENDER, MOVEMENT, PLAYER, MISC
    }
}
