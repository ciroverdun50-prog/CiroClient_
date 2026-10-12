package com.cirox.client.module;

import com.cirox.client.module.impl.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        modules.add(new AimAssistModule());
        modules.add(new TriggerbotModule());
        modules.add(new HitboxModule());
        modules.add(new JumpResetModule());
        modules.add(new PlayerESPModule()); // <-- Registrado correctamente aquí
    }

    public List<Module> getModules() {
        return modules;
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : modules) {
            if (clazz.isAssignableFrom(m.getClass())) {
                return (T) m;
            }
        }
        return null;
    }

    public void onTick() {
        for (Module m : modules) {
            if (m.isEnabled()) {
                m.onTick();
            }
        }
    }
}
