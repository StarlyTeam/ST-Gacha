package net.starly.gacha.manager;

import net.starly.gacha.GachaMain;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ChatListenerBase {

    protected static final Map<UUID, Listener> listenerMap = new HashMap<>();
    protected abstract void onChat(AsyncPlayerChatEvent event);


    public void registerEvent(Player player) {
        Listener listener = registerChatEvent(player.getUniqueId());
        if (listenerMap.containsKey(player.getUniqueId())) HandlerList.unregisterAll(listenerMap.get(player.getUniqueId()));
        listenerMap.put(player.getUniqueId(), listener);
    }

    protected Listener registerChatEvent(UUID uuid) {
        Server server = GachaMain.getInstance().getServer();
        Listener listener = new Listener() {};

        server.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, listener, EventPriority.LOWEST, (listeners, event) -> {
            if (event instanceof AsyncPlayerChatEvent) {
                AsyncPlayerChatEvent chatEvent = (AsyncPlayerChatEvent) event;
                if (uuid.equals(chatEvent.getPlayer().getUniqueId())) {
                    onChat(chatEvent);
                    listenerMap.remove(uuid);
                    HandlerList.unregisterAll(listener);
                }
            }
        }, GachaMain.getInstance());
        return listener;
    }
}
