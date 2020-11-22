package arc.command.commands;

import arc.Arc;
import arc.command.ArcSubCommand;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Handles reloading the arc configuration.
 */
public final class ArcReloadConfigCommand extends ArcSubCommand {

    @Override
    protected void execute(CommandSender sender, String[] arguments) {
        sender.sendMessage(ChatColor.RED + "Reloading the configuration....");
        sender.sendMessage(ChatColor.RED + "All players will be exempt for 5 seconds afterwards.");

        // exempt all players then reload.
        Arc.arc().exemptions().exemptAllPlayersFromAllChecksFor(5000);
        try {
            Arc.arc().configuration().reload();
            sender.sendMessage(ChatColor.GREEN + "Configuration successfully reloaded.");
        } catch (Exception exception) {
            sender.sendMessage(ChatColor.RED + "An error occurred. \n" + ExceptionUtils.getRootCauseMessage(exception));
            exception.printStackTrace();
        }

    }
}
