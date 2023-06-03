package net.starly.gacha.manager.impl.setting;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.starly.core.jb.util.Pair;
import net.starly.core.jb.util.PlayerSkullManager;
import net.starly.gacha.GachaMain;
import net.starly.gacha.builder.ItemBuilder;
import net.starly.gacha.gacha.Gacha;
import net.starly.gacha.manager.InventoryListenerBase;
import net.starly.gacha.manager.impl.input.ItemPercentage;
import net.starly.gacha.page.PaginationManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RewardSettingInventory extends InventoryListenerBase {

    private static RewardSettingInventory instance;

    public static RewardSettingInventory getInstance() {
        if (instance == null) instance = new RewardSettingInventory();
        return instance;
    }

    private final Map<UUID, Gacha> gachaMap = new HashMap<>();
    private final Map<UUID, PaginationManager> pageMap = new HashMap<>();
    private final JavaPlugin plugin = GachaMain.getInstance();

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getSlot() == 53) {
            ItemPercentage.getInstance().openInventory((Player) event.getWhoClicked());
        }
    }

    @Override
    public void openInventory(Player player) {
        Gacha gacha = gachaMap.get(player.getUniqueId());
        Inventory inventory = plugin.getServer().createInventory(null,54,gacha.getName() + " :: 보상설정");

        PaginationManager pageManager = new PaginationManager(gacha.getItemList());
        pageMap.put(player.getUniqueId(), pageManager);

        pageManager.getCurrentPageData().getItems().forEach(gachaItem -> {
            ItemMeta meta = gachaItem.getItemMeta();
            inventory.addItem(new ItemBuilder(gachaItem)
                    .setName(meta.getDisplayName() + " §r§l[ " + gachaItem.getPercentage() + "% ]")
                    .build());
        });

        for (int i = 45; i < inventory.getSize(); i++) {
            try {
                inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 3));
            } catch (NoSuchFieldError error) {
                inventory.setItem(i, new ItemStack(Material.valueOf("LIGHT_BLUE_STAINED_GLASS_PANE")));
            }
        }

        inventory.setItem(45, new ItemBuilder(Material.BARRIER)
                .setName("§c닫기")
                .build());
        inventory.setItem(48, PlayerSkullManager.getCustomSkull("da53d04797b47a68484d111025d940a34886a0fa8dc806e7457024a87f1abd56"));
        inventory.setItem(50, new ItemBuilder(PlayerSkullManager.getCustomSkull("65a84e6394baf8bd795fe747efc582cde9414fccf2f1c8608f1be18c0e079138"))
                .setName("§b다음 페이지")
                .build());
        inventory.setItem(53, new ItemBuilder(Material.ANVIL)
                .setName("§b추가")
                .build());
        openInventoryAndRegisterEvent(player,inventory);
    }

    public void addData(Player player, Gacha gacha) {
        gachaMap.put(player.getUniqueId(), gacha);
    }
}
