package arc.command;

import arc.command.commands.*;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * The base command for /arc
 */
public final class ArcCommand extends ArcBaseCommand implements CommandExecutor {

    /**
     * Initialize
     */
    public ArcCommand() {
        initializeSubCommands();
        populateCommandHelp();
    }

    /**
     * Initialize sub commands
     */
    private void initializeSubCommands() {
        addSubCommand("violations", new ToggleViolationsSubCommand());
        addSubCommand("reload", new ReloadConfigSubCommand());
        addSubCommand("timings", new TimingsSubCommand());
        addSubCommand("cancelban", new CancelBanSubCommand());
        addSubCommand("exempt", new ExemptPlayerSubCommand());
        addSubCommand("debug", new DebugSubCommand());
        addSubCommand("version", new ArcVersionSubCommand());
    }

    /**
     * Populate the command help line(s)
     */
    private void populateCommandHelp() {
        subCommands.values()
                .forEach((command) ->
                        addHelpLine(command.getPermission(), ChatColor.DARK_AQUA + command.getCommand() + " - "
                                + ChatColor.GRAY + command.getDescription()));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!checkBasePermissions(sender)) return true;
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) return printHelpLines(sender);

        final String argument = args[0];
        // execute sub-commands
        if (isSubCommand(argument)) {
            return executeSubCommand(sender, argument, (String[]) ArrayUtils.remove(args, 0));
        } else {
            // not found, print help.
            return printHelpLines(sender);
        }
    }

}
