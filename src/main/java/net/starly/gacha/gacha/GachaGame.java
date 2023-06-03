package net.starly.gacha.gacha;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class GachaGame {

    private Player player;
    private Gacha gacha;
}
