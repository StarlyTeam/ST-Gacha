package net.starly.gacha.manager.impl.input;

import net.minecraft.server.v1_12_R1.ContainerAnvil;
import net.starly.core.jb.version.nms.tank.NmsOtherUtil;
import net.starly.gacha.GachaMain;
import net.starly.gacha.gacha.Gacha;
import net.starly.gacha.gacha.GachaItem;
import net.starly.gacha.manager.InventoryListenerBase;
import net.starly.gacha.manager.impl.setting.RewardSettingInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftInventoryAnvil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
        Inventory inventory = event.getClickedInventory();
        System.out.println(event.getClickedInventory().getClass().getName());
        if (inventory.getItem(2) == null) return;
        double percentage;
        try {
            percentage = Double.parseDouble(inventory.getItem(2).getItemMeta().getDisplayName());
        } catch (NumberFormatException exception) {
            return;
        }

        GachaItem item = (GachaItem) inventory.getItem(0);
        item.setPercentage(percentage);
        System.out.println(item.getPercentage());
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
