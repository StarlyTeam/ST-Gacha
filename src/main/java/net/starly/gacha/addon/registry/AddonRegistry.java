package net.starly.gacha.addon.registry;

import net.starly.gacha.addon.AddonManager;
import net.starly.gacha.gacha.Gacha;
import net.starly.gacha.manager.GachaManager;
import net.starly.gacha.preset.PresetExecutor;

public class AddonRegistry {

    private static final AddonManager manager = AddonManager.getInstance();

    public static boolean registerPreset(String name, PresetExecutor executor) {
        if (manager.hasPreset(name) && manager.hasPreset(executor)) return false;
        return manager.addPreset(name, executor);
    }

    public static boolean unregisterPreset(String name) {
        if (!manager.hasPreset(name)) return false;
        return manager.removePreset(name);
    }

    public static boolean unregisterPreset(PresetExecutor executor) {
        if (!manager.hasPreset(executor)) return false;
        return manager.removePreset(manager.getPreset(executor));
    }

    public static boolean hasPreset(String preset) { return manager.hasPreset(preset); }
    public static boolean hasPreset(PresetExecutor executor) { return manager.hasPreset(executor); }

    public static Gacha getGacha(String name) { return GachaManager.getInstance().getGacha(name); }

}
