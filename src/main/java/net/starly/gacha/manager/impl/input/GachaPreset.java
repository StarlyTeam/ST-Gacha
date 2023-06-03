package net.starly.gacha.manager.impl.input;

import lombok.Getter;
import net.starly.gacha.addon.AddonManager;
import net.starly.gacha.context.MessageContent;
import net.starly.gacha.context.MessageType;
import net.starly.gacha.manager.ChatListenerBase;
import net.starly.gacha.manager.GachaManager;
import net.starly.gacha.manager.impl.setting.GachaSettingInventory;
import net.starly.gacha.manager.impl.setting.RewardSettingInventory;
import net.starly.gacha.preset.PresetExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class GachaPreset extends ChatListenerBase {

    private static GachaPreset instance;
    public static GachaPreset getInstance() {
        if (instance == null) instance = new GachaPreset();
        return instance;
    }

    private final HashMap<UUID, String> gachaNameMap = new HashMap<>();

    @Override
    protected void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        MessageContent content = MessageContent.getInstance();
        /*if (event.getMessage().equalsIgnoreCase("취소")) {
            gachaNameMap.remove(event.getPlayer().getUniqueId());
            content.getMessageAfterPrefix(MessageType.NORMAL, "inputCancel").ifPresent(event.getPlayer()::sendMessage);
            GachaSettingInventory.getInstance().openInventory(event.getPlayer());
            return;
        } else if (!AddonManager.getInstance().hasPreset(event.getMessage())) {
            content.getMessageAfterPrefix(MessageType.ERROR, "noExistPreset").ifPresent(event.getPlayer()::sendMessage);
            instance.registerEvent(event.getPlayer());
            return;
        }*/
        String name = gachaNameMap.get(event.getPlayer().getUniqueId());
        PresetExecutor executor = null; //AddonManager.getInstance().getExecutor(event.getMessage());
        GachaManager gachaManager = GachaManager.getInstance();

        gachaManager.addGacha(name, executor, Collections.emptyList());
        content.getMessageAfterPrefix(MessageType.NORMAL, "gachaCreated").ifPresent(event.getPlayer()::sendMessage);
        RewardSettingInventory.getInstance().addData(event.getPlayer(),gachaManager.getGacha(name));
        RewardSettingInventory.getInstance().openInventory(event.getPlayer());
    }

    public void addGachaName(Player player, String name) {
        gachaNameMap.put(player.getUniqueId(), name);
    }
}
