package net.starly.gacha.command.subcommand;

import net.starly.gacha.command.GachaSub;
import net.starly.gacha.context.MessageContent;
import net.starly.gacha.context.MessageType;
import net.starly.gacha.manager.GachaManager;
import org.bukkit.command.CommandSender;

public class DeleteGacha extends GachaSub {
    //가챠 삭제 [<이름>]

    public DeleteGacha(String permission, boolean consoleAble, int minArg, int maxArg) {
        super(permission, minArg, maxArg, consoleAble);
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        MessageContent content = MessageContent.getInstance();

        String name = args[1];

        if (!GachaManager.getInstance().hasGacha(name)) {
            content.getMessageAfterPrefix(MessageType.ERROR, "noExistGacha").ifPresent(sender::sendMessage);
            return;
        }

        GachaManager.getInstance().removeGacha(name);
        content.getMessageAfterPrefix(MessageType.NORMAL, "gachaDeleted").ifPresent(sender::sendMessage);

    }
}
