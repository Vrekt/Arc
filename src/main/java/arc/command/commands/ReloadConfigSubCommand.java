package arc.command.commands;

import arc.Arc;
import arc.permissions.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Allows reloading of the config
 */
public final class ReloadConfigSubCommand extends ArcSubCommand {

    public ReloadConfigSubCommand() {
        super(Permissions.ARC_COMMANDS_RELOAD_CONFIG);

        setCommand("/arc reload");
        setUsage("/arc reload");
        setDescription("Allows you to view timings information.");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        sendMessage(sender, ChatColor.DARK_AQUA + "Reloading....");
        try {
            Arc.getInstance().getArcConfiguration().reloadConfiguration();
            sendMessage(sender, ChatColor.DARK_AQUA + "Configuration reloaded.");
        } catch (Exception any) {
            any.printStackTrace();
            sendErrorMessage(sender, "Any internal error occurred, it has been printed to console.");
        }
    }
}
