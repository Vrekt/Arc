package arc.command.commands;

import arc.command.ArcSubCommand;
import arc.utility.ChatUtil;
import arc.utility.Punishment;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Handles cancelling bans of players
 */
public final class ArcCancelBanCommand extends ArcSubCommand {

    @Override
    protected void execute(CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            ChatUtil.sendMessage(sender, ChatColor.RED + " You must provide a player name.");
            return;
        }

        final String name = arguments[0];
        if (!Punishment.hasPendingBan(name)) {
            ChatUtil.sendMessage(sender, ChatColor.RED + " The player " + ChatColor.GREEN + name + ChatColor.RED + " does not have a pending ban.");
            return;
        }

        Punishment.cancelBan(name);
        ChatUtil.sendMessage(sender, ChatColor.RED + " The pending ban for " + ChatColor.GREEN + name + ChatColor.RED + " has been cancelled.");
    }
}
