package arc.command.commands;

import arc.Arc;
import arc.command.ArcSubCommand;
import arc.utility.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles toggling debug information
 */
public final class ArcDebugCommand extends ArcSubCommand {

    @Override
    protected void execute(CommandSender sender, String[] arguments) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + " You must be a player to run this command.");
            return;
        }

        final var player = (Player) sender;
        final var state = Arc.arc().violations().isDebugViewer(player);
        Arc.arc().violations().toggleDebugViewer(player, !state);

        ChatUtil.sendMessage(player, ChatColor.GRAY + " Debug information is now " + (!state ? ChatColor.GREEN + "on." : ChatColor.RED + "off."));
    }
}
