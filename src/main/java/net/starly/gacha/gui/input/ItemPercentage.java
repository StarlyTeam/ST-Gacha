package net.starly.gacha.gui.input;

import net.starly.gacha.GachaMain;
import net.starly.gacha.gacha.Gacha;
import net.starly.gacha.gacha.GachaItem;
import net.starly.gacha.gui.impl.RewardSettingInventory;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.math.BigDecimal;
import java.util.*;

public class ItemPercentage{

    private static ItemPercentage instance;

    public static ItemPercentage getInstance() {
        if (instance == null) instance = new ItemPercentage();
        return instance;
    }

    private final JavaPlugin plugin = GachaMain.getInstance();
    private final Map<UUID, Gacha> gachaMap = new HashMap<>();

    protected List<AnvilGUI.ResponseAction> onClick(Integer slot, AnvilGUI.StateSnapshot snapshot) {
        if (slot != AnvilGUI.Slot.OUTPUT) return Collections.emptyList();

        if (snapshot.getLeftItem() == null) return Collections.emptyList();

        double percentage;
        try {
            percentage = Double.parseDouble(snapshot.getText());
        } catch (NumberFormatException exception) {
            return Collections.emptyList();
        }
        GachaItem item = new GachaItem(snapshot.getLeftItem(), BigDecimal.valueOf(percentage));
        Gacha gacha = gachaMap.get(snapshot.getPlayer().getUniqueId());
        gacha.getItemList().add(item);
        snapshot.getPlayer().closeInventory();
        snapshot.getPlayer().getInventory().addItem(snapshot.getLeftItem());
        RewardSettingInventory.getInstance().openInventory(snapshot.getPlayer(), gacha);
        gachaMap.remove(snapshot.getPlayer().getUniqueId());
        return Collections.emptyList();
    }

    public void openInventory(Player player, Gacha gacha) {
        gachaMap.put(player.getUniqueId(), gacha);
        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    RewardSettingInventory.getInstance().openInventory(stateSnapshot.getPlayer(), gachaMap.get(player.getUniqueId()));
                    gachaMap.remove(player.getUniqueId());
                    stateSnapshot.getPlayer().closeInventory();
                })
                .onClick(this::onClick)
                .plugin(plugin)
                .interactableSlots(AnvilGUI.Slot.INPUT_LEFT)
                .open(player);
    }
}

