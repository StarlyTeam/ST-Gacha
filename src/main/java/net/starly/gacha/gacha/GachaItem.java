package net.starly.gacha.gacha;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;


public class GachaItem extends ItemStack {

    @Getter
    @Setter
    private double percentage;

}
