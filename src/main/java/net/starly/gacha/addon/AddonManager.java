package net.starly.gacha.addon;

import lombok.Getter;
import net.starly.gacha.preset.PresetExecutor;

import java.util.HashMap;
import java.util.Map;

public class AddonManager {

    private static AddonManager instance;

    public static AddonManager getInstance() {
        if (instance == null) instance = new AddonManager();
        return instance;
    }

    @Getter
    private final HashMap<String, PresetExecutor> presetMap = new HashMap<>();

    public PresetExecutor getExecutor(String preset) {
        return presetMap.get(preset);
    }

    public String getPreset(PresetExecutor executor) {
        for (Map.Entry<String, PresetExecutor> entry : presetMap.entrySet()) {
            if (entry.getValue().equals(executor)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private AddonManager() {}

    public boolean addPreset(String preset, PresetExecutor executor) {
        if (presetMap.containsKey(preset)) return false;
        else {
            presetMap.put(preset,executor);
            return true;
        }
    }

    public boolean removePreset(String preset) {
        if (presetMap.containsKey(preset)) {
            presetMap.remove(preset);
            return true;
        } else return false;
    }

    public boolean hasPreset(String preset) {
        return presetMap.containsKey(preset);
    }

    public boolean hasPreset(PresetExecutor executor) {
        return presetMap.containsValue(executor);
    }

}
