package net.starly.gacha.gacha;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.Map;


public class GachaItem extends ItemStack {

    @Getter
    @Setter
    private double percentage;

}
