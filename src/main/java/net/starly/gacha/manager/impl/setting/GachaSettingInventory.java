package net.starly.gacha.manager.impl.setting;

import net.starly.gacha.GachaMain;
import net.starly.gacha.builder.ItemBuilder;
import net.starly.gacha.context.MessageContent;
import net.starly.gacha.context.MessageType;
import net.starly.gacha.manager.InventoryListenerBase;
import net.starly.gacha.manager.impl.input.GachaName;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class GachaSettingInventory extends InventoryListenerBase {

    private static GachaSettingInventory instance;

    public static GachaSettingInventory getInstance() {
        if (instance == null) instance = new GachaSettingInventory();
        return instance;
    }

    private final JavaPlugin plugin = GachaMain.getInstance();

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getSlot() == 0) {
            GachaName.getInstance().registerEvent((Player) event.getWhoClicked());
            event.getWhoClicked().closeInventory();
            MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL, "inputGachaName").ifPresent(event.getWhoClicked()::sendMessage);
        }
    }

    @Override
    public void openInventory(Player player) {
        Inventory inventory = plugin.getServer().createInventory(null, InventoryType.HOPPER, plugin.getName() + " 관리메뉴");

        inventory.addItem(new ItemBuilder(Material.ARROW)
                .setName("§b§l생성")
                .setLore("가챠를 생성합니다.")
                .build());

        inventory.addItem(new ItemBuilder(Material.TNT)
                .setName("§b§l삭제")
                .setLore("가챠를 삭제합니다.")
                .build());

        inventory.addItem(new ItemBuilder(Material.BARRIER)
                .setName("§c§l닫기")
                .setLore("창을 닫습니다.")
                .build());

        inventory.addItem(new ItemBuilder(Material.BOOK)
                .setName("§b§l목록")
                .setLore("가챠 목록을 출력합니다.")
                .build());

        inventory.addItem(new ItemBuilder(Material.COMPASS)
                .setName("§b§l설정")
                .setLore("가챠를 설정합니다.")
                .build());
        openInventoryAndRegisterEvent(player,inventory);
    }
}
