package net.starly.gacha.manager.impl.input;

import net.starly.gacha.context.MessageContent;
import net.starly.gacha.context.MessageType;
import net.starly.gacha.manager.ChatListenerBase;
import net.starly.gacha.manager.GachaManager;
import net.starly.gacha.manager.impl.setting.GachaSettingInventory;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class GachaName extends ChatListenerBase {

    private static GachaName instance;

    public static GachaName getInstance() {
        if (instance == null) instance = new GachaName();
        return instance;
    }

    @Override
    protected void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        MessageContent content = MessageContent.getInstance();
        if (event.getMessage().equalsIgnoreCase("취소")) {
            content.getMessageAfterPrefix(MessageType.NORMAL, "inputCancel").ifPresent(event.getPlayer()::sendMessage);
            GachaSettingInventory.getInstance().openInventory(event.getPlayer());
            return;
        } else if (GachaManager.getInstance().hasGacha(event.getMessage())) {
            content.getMessageAfterPrefix(MessageType.ERROR, "existGacha").ifPresent(event.getPlayer()::sendMessage);
            instance.registerEvent(event.getPlayer());
            return;
        }

        GachaPreset.getInstance().addGachaName(event.getPlayer(), event.getMessage());
        GachaPreset.getInstance().registerEvent(event.getPlayer());
        content.getMessageAfterPrefix(MessageType.NORMAL, "inputGachaPreset").ifPresent(event.getPlayer()::sendMessage);
    }
}
