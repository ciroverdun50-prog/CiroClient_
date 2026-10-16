package com.cirox.client.module;

public class HUDModule extends Module {
    public HUDModule() {
        super("HUD", "Displays active modules on screen", Category.RENDER);
        this.toggle(); // Se enciende por defecto al iniciar
    }
}
