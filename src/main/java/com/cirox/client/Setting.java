package com.cirox.client.setting;

public abstract class Setting {
    private final String name;
    private boolean visible = true;

    public Setting(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
