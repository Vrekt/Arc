package arc.command.commands;

import arc.Arc;
import arc.command.ArcSubCommand;
import arc.utility.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Handles reloading the arc configuration.
 */
public final class ArcReloadConfigCommand extends ArcSubCommand {

    @Override
    protected void execute(CommandSender sender, String[] arguments) {
        ChatUtil.sendMessage(sender, ChatColor.RED + " Reloading " + ChatColor.GRAY + "the configuration.");
        ChatUtil.sendMessage(sender, ChatColor.RED + " All " + ChatColor.GRAY + "players will be exempt for 2 seconds afterwards.");

        try {
            Arc.arc().configuration().reloadConfiguration();
            Arc.arc().exemptions().exemptAllPlayersFromAllChecksFor(2000);
            ChatUtil.sendMessage(sender, ChatColor.GRAY + " Configuration successfully reloaded.");
        } catch (Exception exception) {
            ChatUtil.sendMessage(sender, ChatColor.RED + " An error occurred while trying to reload, it has been printed to the console.");
            exception.printStackTrace();
        }

    }
}
