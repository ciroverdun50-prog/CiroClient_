package com.cirox.client.module;

import com.cirox.client.module.Module;

public class PlayerESPModule extends Module {
    public boolean boxEsp = true;
    public boolean tracerEsp = false;

    public PlayerESPModule() {
        super("PlayerESP", "Muestra la posición de los jugadores a través de paredes", Category.RENDER);
    }
}
