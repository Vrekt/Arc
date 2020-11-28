package arc.command;

import arc.command.commands.ArcCancelBanCommand;
import arc.command.commands.ArcDebugCommand;
import arc.command.commands.ArcReloadConfigCommand;
import arc.command.commands.ArcViolationsCommand;
import arc.permissions.Permissions;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * The base Arc command.
 */
public class CommandArc extends ArcCommand implements CommandExecutor {

    public CommandArc() {
        super(Permissions.ARC_COMMANDS_BASE, Permissions.ARC_COMMANDS_ALL);

        // sub commands
        addSubCommand("reload", Permissions.ARC_COMMANDS_RELOAD_CONFIG, new ArcReloadConfigCommand());
        addSubCommand("cancelban", Permissions.ARC_COMMANDS_CANCEL_BAN, new ArcCancelBanCommand());
        addSubCommand("violations", Permissions.ARC_COMMANDS_TOGGLE_VIOLATIONS, new ArcViolationsCommand());
        addSubCommand("debug", Permissions.ARC_COMMANDS_TOGGLE_DEBUG, new ArcDebugCommand());
        // help lines
        addHelpLine(ChatColor.DARK_AQUA + " /arc reload: " + ChatColor.GRAY + "Allows you to reload the configuration.", Permissions.ARC_COMMANDS_RELOAD_CONFIG);
        addHelpLine(ChatColor.DARK_AQUA + " /arc cancelban: " + ChatColor.GRAY + "Cancels a ban scheduled by Arc.", Permissions.ARC_COMMANDS_CANCEL_BAN);
        addHelpLine(ChatColor.DARK_AQUA + " /arc violations: " + ChatColor.GRAY + "Allows you to toggle violations on or off.", Permissions.ARC_COMMANDS_TOGGLE_VIOLATIONS);
        addHelpLine(ChatColor.DARK_AQUA + " /arc debug: " + ChatColor.GRAY + "Allows you to toggle debug information.", Permissions.ARC_COMMANDS_TOGGLE_VIOLATIONS);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender)) return printNoPermission(sender);

        if (args.length == 0) {
            // we have no arguments.
            return printHelpMessage(sender);
        }

        // retrieve the sub command and execute it.
        final String subCommand = args[0];
        if (isSubCommand(subCommand))
            return executeSubCommand(subCommand, sender, ArrayUtils.remove(args, 0));

        // no sub command, print help.
        return printHelpMessage(sender);
    }

}
