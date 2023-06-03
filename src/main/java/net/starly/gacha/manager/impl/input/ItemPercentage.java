package net.starly.gacha.manager.impl.input;

import net.starly.gacha.GachaMain;
import net.starly.gacha.gacha.Gacha;
import net.starly.gacha.gacha.GachaItem;
import net.starly.gacha.manager.InventoryListenerBase;
import net.starly.gacha.manager.impl.setting.RewardSettingInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemPercentage extends InventoryListenerBase {

    private static ItemPercentage instance;

    public static ItemPercentage getInstance() {
        if (instance == null) instance = new ItemPercentage();
        return instance;
    }

    private final JavaPlugin plugin = GachaMain.getInstance();
    private final Map<UUID, Gacha> gachaMap = new HashMap<>();

    @Override
    protected void onClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().getType().equals(InventoryType.ANVIL)) return;
        if (event.getSlot() == 1) {
            event.setCancelled(true);
            return;
        }
        if (event.getSlot() == 0) return;

        event.setCancelled(false);
        AnvilInventory inventory = (AnvilInventory) event.getClickedInventory();

        if (inventory.getItem(2) == null) return;
        double percentage;
        try {
            percentage = Double.parseDouble(inventory.getRenameText());
        } catch (NumberFormatException exception) {
            return;
        }

        GachaItem item = (GachaItem) inventory.getItem(0);
        item.setPercentage(percentage);
        event.setCancelled(true);
        event.getWhoClicked().closeInventory();
        gachaMap.get(event.getWhoClicked().getUniqueId()).getItemList().add(item);
        RewardSettingInventory.getInstance().openInventory((Player) event.getWhoClicked());
    }

    @Override
    public void openInventory(Player player) {
        Inventory inventory = plugin.getServer().createInventory(null, InventoryType.ANVIL, "확률 설정하기");
        inventory.setItem(1,new ItemStack(Material.BARRIER));
        openInventoryAndRegisterEvent(player, inventory);
    }
}
