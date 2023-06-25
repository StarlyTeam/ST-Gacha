package net.starly.gacha.gui.impl;

import net.starly.core.jb.util.PlayerSkullManager;
import net.starly.gacha.GachaMain;
import net.starly.gacha.builder.ItemBuilder;
import net.starly.gacha.gacha.Gacha;
import net.starly.gacha.gui.InventoryListenerBase;
import net.starly.gacha.gui.input.ItemPercentage;
import net.starly.gacha.page.PaginationManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;

        PaginationManager pageManager = pageMap.get(event.getWhoClicked().getUniqueId());
        Inventory inventory = event.getClickedInventory();
        switch (event.getSlot()) {
            case 53: {
                ItemPercentage.getInstance().openInventory((Player) event.getWhoClicked(), gachaMap.get(event.getWhoClicked().getUniqueId()));
                gachaMap.remove(event.getWhoClicked().getUniqueId());
                break;
            }

            case 50: {
                pageManager.nextPage();
                reloadPage(inventory, pageManager);
                break;
            }

            case 48: {
                pageManager.prevPage();
                reloadPage(inventory, pageManager);
                break;
            }

            case 45: {
                event.getWhoClicked().closeInventory();
                break;
            }

            default: {
                if (event.getSlot() >= 45) break;
                if (!event.getClick().equals(ClickType.SHIFT_RIGHT)) break;
                if (event.getClickedInventory().getItem(event.getSlot()) == null) break;
                int slot = event.getSlot() + (45 * (pageManager.getCurrentPage() - 1));
                Gacha gacha = gachaMap.get(event.getWhoClicked().getUniqueId());
                gacha.getItemList().remove(slot);
                int page = pageManager.getCurrentPage();

                pageManager = new PaginationManager(gacha.getItemList());
                System.out.println(page);
                System.out.println(pageManager.getPages().size());
                System.out.println(pageManager.getCurrentPage());
                if (pageManager.getPages().size() < page) page = pageManager.getPages().size();

                while (page != pageManager.getCurrentPage()) pageManager.nextPage();

                pageMap.put(event.getWhoClicked().getUniqueId(), pageManager);
                reloadPage(inventory, pageManager);
                break;
            }
        }
    }

    public void openInventory(Player player, Gacha gacha) {
        gachaMap.put(player.getUniqueId(), gacha);
        openInventory(player);
    }

    @Override
    public void openInventory(Player player) {
        if (!gachaMap.containsKey(player.getUniqueId())) return;
        Gacha gacha = gachaMap.get(player.getUniqueId());

        AtomicReference<Double> percentage = new AtomicReference<>(0d);
        gacha.getItemList().forEach(gachaItem -> percentage.updateAndGet(v -> v + gachaItem.getPercentage()));
        ChatColor color = percentage.get() == 100 ? ChatColor.GREEN : ChatColor.RED;

        Inventory inventory = plugin.getServer().
                createInventory(null,54,gacha.getName() + " :: 보상설정 ( 합계 : " + color + percentage.get() + "§r% )");

        PaginationManager pageManager = new PaginationManager(gacha.getItemList());
        pageMap.put(player.getUniqueId(), pageManager);


        AtomicInteger slot = new AtomicInteger();
        pageManager.getCurrentPageData().getItems().forEach(gachaItem -> {
            ItemMeta meta = gachaItem.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add("§r§l[ " + gachaItem.getPercentage() + "% ]");
            lore.add("§c§l[ Shift + 우클릭으로 삭제 ]");
            inventory.setItem(slot.get(), new ItemBuilder(gachaItem.clone())
                    .setLore(lore)
                    .build());
            slot.getAndIncrement();
        });

        for (int i = 45; i < inventory.getSize(); i++) {
            try {
                inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 3));
            } catch (NoSuchFieldError error) {
                inventory.setItem(i, new ItemStack(Material.valueOf("LIGHT_BLUE_STAINED_GLASS_PANE")));
            }
        }

        inventory.setItem(45, new ItemBuilder(Material.BARRIER)
                .setName("§c이전화면")
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

    protected void reloadPage(Inventory inventory, PaginationManager pageManager) {
        inventory.clear();
        System.out.println(pageManager.getCurrentPageData().getItems());
        if (!pageManager.getCurrentPageData().getItems().isEmpty()) {
            AtomicInteger slot = new AtomicInteger();
            pageManager.getCurrentPageData().getItems().forEach(gachaItem -> {
                ItemMeta meta = gachaItem.getItemMeta();
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add("§r§l[ " + gachaItem.getPercentage() + "% ]");
                lore.add("§c§l[ Shift + 우클릭으로 삭제 ]");
                inventory.setItem(slot.get(), new ItemBuilder(gachaItem.clone())
                        .setLore(lore)
                        .build());
                slot.getAndIncrement();
            });
        }

        for (int i = 45; i < inventory.getSize(); i++) {
            try {
                inventory.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE,1,(short) 3));
            } catch (NoSuchFieldError error) {
                inventory.setItem(i, new ItemStack(Material.valueOf("LIGHT_BLUE_STAINED_GLASS_PANE")));
            }
        }

        inventory.setItem(45, new ItemBuilder(Material.BARRIER)
                .setName("§c이전화면")
                .build());
        inventory.setItem(48, PlayerSkullManager.getCustomSkull("da53d04797b47a68484d111025d940a34886a0fa8dc806e7457024a87f1abd56"));
        inventory.setItem(50, new ItemBuilder(PlayerSkullManager.getCustomSkull("65a84e6394baf8bd795fe747efc582cde9414fccf2f1c8608f1be18c0e079138"))
                .setName("§b다음 페이지")
                .build());
        inventory.setItem(53, new ItemBuilder(Material.ANVIL)
                .setName("§b추가")
                .build());
    }
}
