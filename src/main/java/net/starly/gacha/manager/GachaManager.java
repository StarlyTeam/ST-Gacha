package net.starly.gacha.manager;

import lombok.Getter;
import net.starly.gacha.gacha.GachaItem;
import net.starly.gacha.gacha.Gacha;
import net.starly.gacha.gacha.GachaGame;
import net.starly.gacha.preset.PresetExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GachaManager {

    private static GachaManager instance;
    public static GachaManager getInstance() {
        if (instance == null) instance = new GachaManager();
        return instance;
    }

    private GachaManager() {}

    @Getter
    private final List<Gacha> gachas = new ArrayList<>();

    public void addGacha(String name, PresetExecutor executor, List<GachaItem> rewards) {
        if (hasGacha(name)) return;
        gachas.add(new Gacha(name, executor, rewards));
    }

    public void addGacha(Gacha gacha) {
        if (gachas.contains(gacha)) return;
        gachas.add(gacha);
    }

    public void removeGacha(String name) {
        if (!hasGacha(name)) return;
        gachas.remove(getGacha(name));
    }

    public Gacha getGacha(String name) {
        for (Gacha gacha : gachas) {
            if (gacha.getName().equalsIgnoreCase(name)) {
                return gacha;
            }
        }
        return null;
    }

    public boolean hasGacha(String name) {
        for (Gacha gacha : gachas) {
            if (gacha.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void roll(Player player, Gacha gacha) {
        gacha.getPresetExecutor().execute(new GachaGame(player, gacha));
    }
}
