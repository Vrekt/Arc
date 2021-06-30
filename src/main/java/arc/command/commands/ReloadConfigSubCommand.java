package arc.command.commands;

import arc.Arc;
import arc.permissions.Permissions;
import arc.utility.chat.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Allows reloading of the config
 */
public final class ReloadConfigSubCommand extends ArcSubCommand {

    public ReloadConfigSubCommand() {
        super(Permissions.ARC_COMMANDS_RELOAD_CONFIG);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        ChatUtil.sendMessage(sender, ChatColor.RED + "Reloading....");
        try {
            Arc.getInstance().getArcConfiguration().reloadConfiguration();
            ChatUtil.sendMessage(sender, ChatColor.GREEN + "Configuration reloaded.");
        } catch (Exception any) {
            any.printStackTrace();
            ChatUtil.sendMessage(sender, ChatColor.RED + "Any internal error occurred, it has been printed to console.");
        }
    }
}
