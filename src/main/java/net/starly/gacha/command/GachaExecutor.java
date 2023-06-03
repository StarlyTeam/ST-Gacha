package net.starly.gacha.command;

import net.starly.gacha.GachaMain;
import net.starly.gacha.builder.ItemBuilder;
import net.starly.gacha.context.MessageContent;
import net.starly.gacha.context.MessageType;
import net.starly.gacha.manager.impl.setting.GachaSettingInventory;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class GachaExecutor implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {

        MessageContent content = MessageContent.getInstance();

        if (!sender.hasPermission("starly.gacha.admin")) {
            content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
            return false;
        }

        if (!(sender instanceof Player)) {
            content.getMessageAfterPrefix(MessageType.ERROR,"noConsole").ifPresent(sender::sendMessage);
            return false;
        }

        GachaSettingInventory.getInstance().openInventory((Player) sender);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
