package net.starly.gacha.command.subcommand;

import net.starly.gacha.GachaMain;
import net.starly.gacha.command.GachaSub;
import net.starly.gacha.context.MessageContent;
import net.starly.gacha.context.MessageType;
import net.starly.gacha.gacha.Gacha;
import net.starly.gacha.manager.GachaManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RollGacha extends GachaSub {
    //가챠 삭제 [<이름>]

    public RollGacha(String permission, boolean consoleAble, int minArg, int maxArg) {
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

        if (args.length == 2) {
            Player player = GachaMain.getInstance().getServer().getPlayer(args[1]);
            if (player == null || !player.isOnline()) {
                content.getMessageAfterPrefix(MessageType.ERROR,"noPlayer").ifPresent(sender::sendMessage);
                return;
            }

            GachaManager.getInstance().roll(player, GachaManager.getInstance().getGacha(name));
            return;
        }
        GachaManager.getInstance().roll((Player) sender, GachaManager.getInstance().getGacha(name));

    }
}
