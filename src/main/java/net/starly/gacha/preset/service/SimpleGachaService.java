package net.starly.gacha.preset.service;

import net.starly.gacha.GachaMain;
import net.starly.gacha.addon.registry.AddonRegistry;
import net.starly.gacha.builder.ItemBuilder;
import net.starly.gacha.gacha.GachaGame;
import net.starly.gacha.preset.PresetExecutor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class SimpleGachaService implements PresetExecutor {

    GachaMain plugin = GachaMain.getInstance();

    @Override
    public void execute(GachaGame gacha) {

        Listener inventoryClickListener = registerListener(gacha.getPlayer());

        Inventory gameInventory = plugin.getServer().createInventory(null, InventoryType.HOPPER, gacha.getGacha().getName());

        new BukkitRunnable() {
            int rollCount = 0;
            int delay = 2;
            int tick = 0;
            boolean deceleration = false;

            @Override
            public void run() {

                if (!gacha.getPlayer().isOnline()) {
                    HandlerList.unregisterAll(inventoryClickListener);

                    ItemStack item = AddonRegistry.chooseItem(gacha.getGacha().getItemList());
                    if (item == null) item = new ItemStack(Material.AIR);

                    gacha.getPlayer().getInventory().addItem(item);
                    cancel();
                }

                gacha.getPlayer().openInventory(gameInventory);
                tick++;

                if (tick >= delay) {
                    gacha.getPlayer().playSound(gacha.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,100,2);

                    ItemStack item = AddonRegistry.chooseItem(gacha.getGacha().getItemList());
                    if (item == null) item = new ItemStack(Material.AIR);

                    for (int i = 0; i < 5; i++) gameInventory.setItem(i, item);

                    if (deceleration) delay += 1;
                    else if (rollCount >= 8) deceleration = true;

                    tick = 0;
                    rollCount++;
                } else if (delay > 16) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            showResultInventory(gacha.getPlayer(), gameInventory.getItem(0), inventoryClickListener);
                        }
                    }.runTaskLater(plugin, delay);
                    cancel();
                }
            }
        }.runTaskTimer(plugin,0L,1L);
    }

    private void showResultInventory(Player player, ItemStack result, Listener inventoryClickListener) {

        Inventory resultInventory = plugin.getServer().createInventory(null, InventoryType.DISPENSER, "보상 수령");

        resultInventory.setItem(4, result);

        HandlerList.unregisterAll(inventoryClickListener);

        player.openInventory(resultInventory);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1);

        Listener inventoryCloseListener = new Listener() {};

        plugin.getServer().getPluginManager().registerEvent(InventoryCloseEvent.class, inventoryCloseListener, EventPriority.LOWEST, (listeners, event) -> {
            if (event instanceof InventoryCloseEvent) {
                InventoryCloseEvent closeEvent = (InventoryCloseEvent) event;
                if (!player.getUniqueId().equals(closeEvent.getPlayer().getUniqueId())) {
                    return;
                }

                Arrays.stream(resultInventory.getContents()).filter(item -> item != null).forEach(item -> {
                    HashMap<Integer, ItemStack> stack = player.getInventory().addItem(item);
                    if (!stack.isEmpty()) {
                        stack.forEach((integer, itemStack) -> {
                            player.getWorld().dropItem(player.getLocation(), itemStack);
                        });
                    }
                });
                HandlerList.unregisterAll(inventoryCloseListener);
            }
        }, GachaMain.getInstance());
    }

    private Listener registerListener(Player player) {
        Listener inventoryClickListener = new Listener() {};

        plugin.getServer().getPluginManager().registerEvent(InventoryClickEvent.class, inventoryClickListener, EventPriority.LOWEST, (listeners, event) -> {
            if (event instanceof InventoryClickEvent) {
                InventoryClickEvent clickEvent = (InventoryClickEvent) event;
                if (player.getUniqueId().equals(clickEvent.getWhoClicked().getUniqueId())) clickEvent.setCancelled(true);
            }
        }, GachaMain.getInstance());

        return inventoryClickListener;
    }
}
