package net.starly.gacha.command;

import net.starly.core.jb.annotation.Subcommand;
import net.starly.gacha.GachaMain;
import net.starly.gacha.addon.AddonManager;
import net.starly.gacha.command.subcommand.*;
import net.starly.gacha.context.MessageContent;
import net.starly.gacha.context.MessageType;
import net.starly.gacha.gacha.Gacha;
import net.starly.gacha.manager.GachaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GachaExecutor implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(GachaMain.getInstance().getName() + "이 현재 " + GachaMain.getInstance().getServer().getBukkitVersion() + "에서 구동중입니다.");
            return true;
        }

        if (args[0].equalsIgnoreCase("생성"))
            new CreateGacha("starly.gacha.create", true, 3, 3).run(sender, args);
        else if (args[0].equalsIgnoreCase("삭제"))
            new DeleteGacha("starly.gacha.delete", true, 2, 2).run(sender, args);
        else if (args[0].equalsIgnoreCase("리로드"))
            new ReloadGacha("starly.gacha.reload",true, 1, 1).run(sender,args);
        else if (args[0].equalsIgnoreCase("보상설정"))
            new ModifyGacha("starly.gacha.reward", false, 2, 2).run(sender, args);
        else if (args[0].equalsIgnoreCase("뽑기"))
            new RollGacha("starly.gacha.roll", false, 2, 2).run(sender, args);

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 1) return Arrays.asList("생성","삭제","보상설정","뽑기","리로드");
        else if (args.length == 2 && !args[0].equalsIgnoreCase("리로드")) {
            return GachaManager.getInstance().getGachas().stream()
                    .map(Gacha::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("생성")) {
            return AddonManager.getInstance().getPresetMap().keySet().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
