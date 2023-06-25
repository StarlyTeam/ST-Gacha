package net.starly.gacha.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.starly.gacha.context.MessageContent;
import net.starly.gacha.context.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
@Setter
public abstract class GachaSub {

    private String permission;

    private int minArg;
    private int maxArg;

    private boolean consoleAble;

    public void run(CommandSender sender, String[] args) {
        MessageContent content = MessageContent.getInstance();
        if (!sender.hasPermission(permission)) {
            content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
            return;
        }

        if (!consoleAble && !(sender instanceof Player)) {
            content.getMessageAfterPrefix(MessageType.ERROR,"noConsole").ifPresent(sender::sendMessage);
            return;
        }

        if (args.length < minArg || args.length > maxArg) {
            content.getMessageAfterPrefix(MessageType.ERROR,"wrongCommand").ifPresent(sender::sendMessage);
            return;
        }

        execute(sender, args);
    }

    protected abstract void execute(CommandSender sender, String[] args);
}
