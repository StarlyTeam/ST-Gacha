package net.starly.gacha.command.subcommand;

import net.starly.gacha.GachaMain;
import net.starly.gacha.command.GachaSub;
import net.starly.gacha.context.MessageContent;
import net.starly.gacha.context.MessageType;
import net.starly.gacha.repo.GachaRepository;
import org.bukkit.command.CommandSender;

public class ReloadGacha extends GachaSub {
    public ReloadGacha(String permission, boolean consoleAble, int minArg, int maxArg) {
        super(permission, minArg, maxArg, consoleAble);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        GachaMain.getInstance().reloadConfig();
        MessageContent.getInstance().initialize(GachaMain.getInstance().getConfig());

        GachaRepository.getInstance().saveData();
        GachaRepository.getInstance().loadData();

        MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL, "reloadCompleted").ifPresent(sender::sendMessage);
    }
}
