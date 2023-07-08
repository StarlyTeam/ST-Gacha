package net.starly.gacha.command.subcommand;

import net.starly.gacha.addon.AddonManager;
import net.starly.gacha.command.GachaSub;
import net.starly.gacha.context.MessageContent;
import net.starly.gacha.context.MessageType;
import net.starly.gacha.gacha.Gacha;
import net.starly.gacha.manager.GachaManager;
import net.starly.gacha.gui.impl.RewardSettingInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CreateGacha extends GachaSub {

    //가챠 생성 [<이름>] [<프리셋>]

    public CreateGacha(String permission, boolean consoleAble, int minArg, int maxArg) {
        super(permission, minArg, maxArg, consoleAble);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        MessageContent content = MessageContent.getInstance();

        String name = args[1];
        String preset = args[2];

        if (GachaManager.getInstance().hasGacha(name)) {
            content.getMessageAfterPrefix(MessageType.ERROR, "existGacha").ifPresent(sender::sendMessage);
            return;
        }

        if (!AddonManager.getInstance().hasPreset(preset)) {
            content.getMessageAfterPrefix(MessageType.ERROR, "noExistPreset").ifPresent(sender::sendMessage);
            return;
        }

        Gacha gacha = new Gacha(name, preset, new ArrayList<>());
        GachaManager.getInstance().addGacha(gacha);
        content.getMessageAfterPrefix(MessageType.NORMAL, "gachaCreated").ifPresent(sender::sendMessage);

        if (sender instanceof Player) RewardSettingInventory.getInstance().openInventory((Player) sender, gacha);

    }
}
