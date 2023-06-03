package net.starly.gacha.preset;

import net.starly.gacha.gacha.GachaGame;
import org.bukkit.entity.Player;

public interface PresetExecutor {
    void execute(Player player, GachaGame gacha);
}
