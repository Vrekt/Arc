package arc.command;

import arc.Arc;
import arc.permissions.Permissions;
import arc.utility.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A basic arc command.
 */
public abstract class ArcCommand {

    /**
     * Strikethrough
     */
    private static final String STRIKETHROUGH = ChatColor.RED + (ChatColor.STRIKETHROUGH + "----------------------------------------");

    /**
     * The header
     */
    private static final String HEADER = STRIKETHROUGH + "\n" + ChatColor.RED + "Arc [" + Arc.VERSION + "]" + "\n" + STRIKETHROUGH;

    /**
     * The help messages with their respective permissions.
     */
    private final Map<String, String> helpLinesWithPermissions = new HashMap<>();

    /**
     * The sub commands
     */
    private final Map<String, ArcSubCommand> subCommands = new HashMap<>();

    /**
     * The permission required
     */
    private final List<String> permissions;

    public ArcCommand(String... permissions) {
        this.permissions = Arrays.asList(permissions);
    }

    /**
     * Add a sub command
     *
     * @param command  the command
     * @param executor the executor
     */
    protected void addSubCommand(String command, String permission, ArcSubCommand executor) {
        executor.permission(permission);
        subCommands.put(command, executor);
    }

    /**
     * Check if the provided argument is a sub command
     *
     * @param argument the argument
     * @return {@code true} if so
     */
    protected boolean isSubCommand(String argument) {
        return subCommands.keySet().stream().anyMatch(argument::equalsIgnoreCase);
    }

    /**
     * Execute a sub command
     *
     * @param argument the argument
     * @param sender   the sender
     * @param args     the arguments
     * @return {@code true}
     */
    protected boolean executeSubCommand(String argument, CommandSender sender, String[] args) {
        final var subCommand = subCommands.get(argument);
        if (!sender.hasPermission(subCommand.permission()) || !sender.hasPermission(Permissions.ARC_COMMANDS_ALL)) {
            ChatUtil.sendMessage(sender, ChatColor.RED + "You do not have permission to execute this sub-command.");
            return true;
        }
        subCommands.get(argument).execute(sender, args);
        return true;
    }

    /**
     * Check if the sender has the permission required
     *
     * @param sender the sender
     * @return {@code true} if so
     */
    protected boolean hasPermission(CommandSender sender) {
        return permissions.stream().anyMatch(sender::hasPermission);
    }

    /**
     * Print no permission message
     *
     * @param sender the sender
     * @return true.
     */
    protected boolean printNoPermission(CommandSender sender) {
        sender.sendMessage(Arc.arc().configuration().noPermissionMessage());
        return true;
    }

    /**
     * Add a help line
     *
     * @param line       the line
     * @param permission the permission
     */
    protected void addHelpLine(String line, String permission) {
        helpLinesWithPermissions.put(line, permission);
    }

    /**
     * Print the help message
     *
     * @param sender the sender
     * @return {@code true}
     */
    protected boolean printHelpMessage(CommandSender sender) {
        final var builder = new StringBuilder();
        builder.append(HEADER);
        builder.append("\n");

        final var count = new AtomicInteger(0);
        helpLinesWithPermissions.forEach((line, permission) -> {
            if (sender.hasPermission(permission)) {
                final var get = count.addAndGet(1);
                builder.append(line);
                if (get == helpLinesWithPermissions.size()) {
                    builder.append(STRIKETHROUGH);
                } else {
                    builder.append("\n").append(" ").append("\n");
                }
            }
        });

        sender.sendMessage(builder.toString());
        return true;
    }

}
