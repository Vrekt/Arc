package arc.command;

import arc.Arc;
import arc.command.commands.*;
import arc.inventory.InventoryCreator;
import arc.inventory.ItemBuilder;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
    }

    /**
     * Populate the command help line(s)
     */
    private void populateCommandHelp() {
        addHelpLine(ChatColor.DARK_AQUA + "/arc - " + ChatColor.GRAY + "Opens the inventory UI if you are a player.");
        subCommands.values()
                .forEach((command) ->
                        addHelpLine(command.getPermission(), ChatColor.DARK_AQUA + command.getCommand() + " - "
                                + ChatColor.GRAY + command.getDescription()));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
        final boolean violations = Arc.getInstance().getViolationManager().isViolationViewer(player);
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
                .item(timingsItem, 2, item -> executeSubCommandInventory(player, "timings", item, creator))
                .fillEmptySlots(emptySlotItem)
                .show(player);
        return true;
    }

}
