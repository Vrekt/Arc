package arc.command.commands;

import arc.Arc;
import arc.permissions.Permissions;
import arc.utility.chat.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Allows the sender to cancel player bans
 */
public final class CancelBanSubCommand extends ArcSubCommand {

    public CancelBanSubCommand() {
        super(Permissions.ARC_COMMANDS_CANCEL_BAN);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            ChatUtil.sendMessage(sender, ChatColor.RED + "You must provide a player name.");
            return;
        }

        final boolean pending = Arc.arc().punishment().hasPendingBan(arguments[0]);
        if (!pending) {
            ChatUtil.sendMessage(sender, ChatColor.RED + "That player does not have a pending ban.");
            return;
        }

        Arc.arc().punishment().cancelBan(arguments[0]);
        ChatUtil.sendMessage(sender, ChatColor.GREEN + "The pending ban for " + ChatColor.GRAY + arguments[0] + ChatColor.GREEN + " has been cancelled.");
    }
}
