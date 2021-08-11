package arc.command.commands;

import arc.Arc;
import arc.permissions.Permissions;
import arc.utility.chat.ColoredChat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Allows the sender to cancel player bans
 */
public final class CancelBanSubCommand extends ArcSubCommand {

    public CancelBanSubCommand() {
        super(Permissions.ARC_COMMANDS_CANCEL_BAN);

        setCommand("/arc cancelban (player)");
        setDescription("Allows you to cancel a pending ban.");
        setUsage("/arc cancelban (player)");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            printUsage(sender);
            return;
        }

        final String playerName = arguments[0];
        final boolean pending = Arc.getInstance().getPunishmentManager().hasPendingBan(playerName);
        if (!pending) {
            sendErrorMessage(sender, ChatColor.RED + "That player does not have a pending ban.");
            return;
        }

        Arc.getInstance().getPunishmentManager().cancelBan(playerName);
        ColoredChat.forRecipient(sender)
                .setMainColor(ChatColor.DARK_AQUA)
                .setParameterColor(ChatColor.GRAY)
                .message("The pending ban for ")
                .parameter(playerName)
                .message(" has been cancelled.");
    }
}
