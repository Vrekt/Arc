package arc.command;

import arc.Arc;
import arc.command.commands.CancelBanSubCommand;
import arc.command.commands.ReloadConfigSubCommand;
import arc.command.commands.TimingsSubCommand;
import arc.command.commands.ToggleViolationsSubCommand;
import arc.inventory.InventoryCreator;
import arc.inventory.ItemBuilder;
import arc.permissions.Permissions;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * The base command for /arc
 */
public final class ArcCommand extends ArcBaseCommand implements CommandExecutor {

    /**
     * Initialize
     */
    public ArcCommand() {
        // initialize sub commands
        addSubCommand("violations", new ToggleViolationsSubCommand());
        addSubCommand("reload", new ReloadConfigSubCommand());
        addSubCommand("timings", new TimingsSubCommand());
        addSubCommand("cancelban", new CancelBanSubCommand());

        // initialize help message.
        final String prefix = Arc.arc().configuration().prefix();
        helpLine(prefix + ChatColor.DARK_AQUA + " /arc - " + ChatColor.GRAY + "Opens the inventory UI if you are a player.");
        helpLine(prefix + ChatColor.DARK_AQUA + " /arc help - " + ChatColor.GRAY + "Shows this message");
        helpLine(Permissions.ARC_COMMANDS_TOGGLE_VIOLATIONS, prefix + ChatColor.DARK_AQUA + " /arc violations - " + ChatColor.GRAY + "Toggle violations on or off.");
        helpLine(Permissions.ARC_COMMANDS_RELOAD_CONFIG, prefix + ChatColor.DARK_AQUA + " /arc reload - " + ChatColor.GRAY + "Reloads the configuration.");
        helpLine(Permissions.ARC_COMMANDS_TIMINGS, prefix + ChatColor.DARK_AQUA + " /arc timings - " + ChatColor.GRAY + "View timings and TPS information.");
        helpLine(Permissions.ARC_COMMANDS_CANCEL_BAN, prefix + ChatColor.DARK_AQUA + " /arc cancelban <player> - " + ChatColor.GRAY + "Cancel a pending player ban.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!checkBasePermissions(sender)) return true;
        final boolean isPlayer = sender instanceof Player;
        if (args.length == 0 && isPlayer) {
            // display inventory
            return displayInventoryUI((Player) sender);
        } else if (args.length == 0) {
            // display help
            return printHelpLines(sender);
        }

        final String argument = args[0];
        // execute help command
        if (help(argument)) return printHelpLines(sender);
        // execute sub-commands
        if (isSubCommand(argument)) {
            return executeSubCommand(sender, argument, (String[]) ArrayUtils.remove(args, 0));
        } else {
            // not found, print help.
            return printHelpLines(sender);
        }
    }

    /**
     * Display player inventory UI.
     *
     * @param player the player
     */
    private boolean displayInventoryUI(Player player) {
        // create an empty item to fill slots with.
        final ItemStack emptySlotItem = new ItemBuilder("STAINED_GLASS_PANE", "BLACK_STAINED_GLASS_PANE", 15)
                .displayName("")
                .build();

        // create toggle violations item
        final boolean violations = Arc.arc().violations().isViolationViewer(player);
        final ItemStack toggleViolationsItem = new ItemBuilder("NETHER_STAR")
                .displayName(ChatColor.RED + "Toggle violations")
                .lore(ChatColor.GRAY + "Violations are currently " + (violations ? ChatColor.GREEN + "on." : ChatColor.RED + "off."))
                .build();

        // create reload config item.
        final ItemStack reloadConfigItem = new ItemBuilder("REDSTONE")
                .displayName(ChatColor.RED + "Reload configuration")
                .lore(ChatColor.GRAY + "All players will be exempt for a few seconds afterwards.")
                .build();

        // create the timings item
        final ItemStack timingsItem = new ItemBuilder("COMPASS")
                .displayName(ChatColor.RED + "Performance")
                .lore(ChatColor.GRAY + "Insight into check timings and TPS.")
                .build();

        // finally, create the inventory and show it.
        final InventoryCreator creator = new InventoryCreator(ChatColor.RED + "Arc " + ChatColor.GREEN + Arc.VERSION_STRING, 45);
        creator.initialIndex(20)
                .item(toggleViolationsItem, 2, item -> executeSubCommandInventory(player, "violations", item, creator))
                .item(reloadConfigItem, 2, item -> executeSubCommand(player, "reload", null))
                .item(timingsItem, 2, item -> executeSubCommand(player, "timings", null))
                .fillEmptySlots(emptySlotItem)
                .show(player);
        return true;
    }

}
