package net.starly.gacha.preset.service;

import net.starly.gacha.GachaMain;
import net.starly.gacha.addon.registry.AddonRegistry;
import net.starly.gacha.gacha.GachaGame;
import net.starly.gacha.preset.PresetExecutor;
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

        AtomicBoolean deceleration = new AtomicBoolean(false);

        AtomicInteger rollCount = new AtomicInteger(0);
        AtomicInteger delay = new AtomicInteger(2);

        AtomicInteger tick = new AtomicInteger(0);

        new BukkitRunnable() {
            @Override
            public void run() {

                if (!gacha.getPlayer().isOnline()) {
                    HandlerList.unregisterAll(inventoryClickListener);
                    cancel();
                }

                gacha.getPlayer().openInventory(gameInventory);
                tick.getAndIncrement();

                if (tick.get() >= delay.get()) {
                    gacha.getPlayer().playSound(gacha.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,100,2);

                    ItemStack item = AddonRegistry.chooseItem(gacha.getGacha().getItemList());
                    for (int i = 0; i < 5; i++) gameInventory.setItem(i, item);

                    if (deceleration.get()) delay.getAndAdd(2);
                    else if (rollCount.get() >= 7) deceleration.set(true);

                    tick.set(0);
                    rollCount.getAndIncrement();
                } else if (delay.get() > 25) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            showResultInventory(gacha.getPlayer(), gameInventory.getItem(0), inventoryClickListener);
                        }
                    }.runTaskLater(plugin, delay.get());
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
