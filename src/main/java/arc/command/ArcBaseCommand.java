package arc.command;

import arc.Arc;
import arc.command.commands.ArcSubCommand;
import arc.inventory.InventoryCreator;
import arc.permissions.Permissions;
import arc.utility.chat.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Arc base command
 */
public abstract class ArcBaseCommand {

    /**
     * Map of sub-commands.
     */
    private final Map<String, ArcSubCommand> subCommands = new HashMap<>();

    /**
     * Map of help-lines
     */
    private final Map<String, String> helpLines = new HashMap<>();

    /**
     * Help-lines without permissions
     */
    private final List<String> singularHelpLines = new ArrayList<>();

    /**
     * Add a sub command
     *
     * @param name    the name
     * @param command the command
     */
    protected void addSubCommand(String name, ArcSubCommand command) {
        subCommands.put(name, command);
    }

    /**
     * Add a help line.
     *
     * @param permission the permission
     * @param line       the line
     */
    protected void helpLine(String permission, String line) {
        helpLines.put(permission, line);
    }

    /**
     * Add a help line without permissions
     *
     * @param line the line
     */
    protected void helpLine(String line) {
        singularHelpLines.add(line);
    }

    /**
     * Check if the argument is a sub-command
     *
     * @param argument the argument
     * @return {@code true} if so
     */
    protected boolean isSubCommand(String argument) {
        return subCommands.containsKey(argument.toLowerCase());
    }

    /**
     * Execute a sub-command
     *
     * @param sender    the sender
     * @param arguments the arguments
     * @return {@code true}
     */
    protected boolean executeSubCommand(CommandSender sender, String command, String[] arguments) {
        final ArcSubCommand subCommand = subCommands.get(command);
        if (sender.hasPermission(Permissions.ARC_COMMANDS_ALL) || subCommand.hasPermission(sender)) {
            subCommand.execute(sender, arguments);
        } else {
            ChatUtil.sendMessage(sender, ChatColor.RED + "You do not have permission to do this.");
        }
        return true;
    }

    /**
     * Execute a sub-command inventory action
     *
     * @param player    the player
     * @param command   the command
     * @param item      the item
     * @param inventory the inventory
     */
    protected void executeSubCommandInventory(Player player, String command, ItemStack item, InventoryCreator inventory) {
        final ArcSubCommand subCommand = subCommands.get(command);
        if (player.hasPermission(Permissions.ARC_COMMANDS_ALL) || subCommand.hasPermission(player)) {
            subCommand.executeFromInventory(item, inventory, player);
        } else {
            ChatUtil.sendMessage(player, ChatColor.RED + "You do not have permission to do this.");
        }
    }

    /**
     * Check base permissions
     *
     * @param sender the sender
     * @return {@code false} if no permission
     */
    protected boolean checkBasePermissions(CommandSender sender) {
        if (!sender.hasPermission(Permissions.ARC_COMMANDS_BASE)
                || !sender.hasPermission(Permissions.ARC_COMMANDS_ALL)) {
            sender.sendMessage(Arc.getInstance().getArcConfiguration().commandNoPermissionMessage());
            return false;
        }
        return true;
    }

    /**
     * Check if the provided argument is help
     *
     * @param argument the argument
     * @return {@code true} if so
     */
    protected boolean help(String argument) {
        return argument.equalsIgnoreCase("help");
    }

    /**
     * Print help lines
     *
     * @param sender the sender
     * @return {@code true}
     */
    protected boolean printHelpLines(CommandSender sender) {
        final StringBuilder builder = new StringBuilder();
        singularHelpLines.forEach(line -> {
            builder.append(line);
            builder.append("\n");
        });
        helpLines.forEach((permission, line) -> {
            if (sender.hasPermission(permission)) {
                builder.append(line);
                builder.append("\n");
            }
        });

        sender.sendMessage(builder.toString());
        return true;
    }

}
