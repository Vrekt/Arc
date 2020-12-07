package arc.command;

import arc.Arc;
import arc.check.Check;
import arc.inventory.InventoryCreator;
import arc.inventory.ItemBuilder;
import arc.permissions.Permissions;
import arc.utility.chat.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

/**
 * The base command for /arc
 */
public final class ArcCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // check sender permissions.
        if (!sender.hasPermission(Permissions.ARC_COMMANDS_BASE)
                || !sender.hasPermission(Permissions.ARC_COMMANDS_ALL)) {
            sender.sendMessage(Arc.arc().configuration().noPermissionMessage());
            return true;
        }

        // check sender is a player.
        // TODO: For now.
        if (!(sender instanceof Player)) {
            ChatUtil.sendMessage(sender, ChatColor.RED + "You must be a player to do this.");
            return true;
        }

        // cast our player and start building the inventory
        final Player player = (Player) sender;

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
                .item(toggleViolationsItem, 2, item -> toggleViolations(item, player, creator))
                .item(reloadConfigItem, 2, item -> reloadConfig(player))
                .item(timingsItem, 2, item -> viewTimings(player))
                .fillEmptySlots(emptySlotItem)
                .show(player);
        return true;
    }

    /**
     * Toggle violations
     *
     * @param item    the item
     * @param player  the player
     * @param creator the creator
     */
    private void toggleViolations(ItemStack item, Player player, InventoryCreator creator) {
        if (!Permissions.canExecuteAction(player, Permissions.ARC_COMMANDS_TOGGLE_VIOLATIONS)) {
            ChatUtil.sendMessage(player, ChatColor.RED + "You do not have permission to do this.");
            player.closeInventory();
            return;
        }

        final ItemStack modified = item.clone();
        final boolean violations = Arc.arc().violations().toggleViolationsViewer(player);

        final ItemMeta meta = modified.getItemMeta();
        if (meta != null) {
            meta.setLore(Collections.singletonList(ChatColor.GRAY + "Violations are currently " + (violations ? ChatColor.GREEN + "on." : ChatColor.RED + "off.")));
        }
        modified.setItemMeta(meta);
        creator.replace(item, modified);
    }

    /**
     * Reload the config
     *
     * @param player the player
     */
    private void reloadConfig(Player player) {
        if (!Permissions.canExecuteAction(player, Permissions.ARC_COMMANDS_RELOAD_CONFIG)) {
            ChatUtil.sendMessage(player, ChatColor.RED + "You do not have permission to do this.");
            player.closeInventory();
            return;
        }

        ChatUtil.sendMessage(player, ChatColor.RED + "Reloading....");
        try {
            Arc.arc().configuration().reloadConfiguration();
            ChatUtil.sendMessage(player, ChatColor.GREEN + "Configuration reloaded.");
        } catch (Exception any) {
            any.printStackTrace();
            ChatUtil.sendMessage(player, ChatColor.RED + "Any internal error occurred, it has been printed to console.");
        }
    }

    /**
     * View timings.
     * TODO very basic for right now.
     *
     * @param player the player
     */
    private void viewTimings(Player player) {
        Arc.arc().checks().getAllChecks()
                .stream()
                .filter(Check::hasAnyTimings)
                .forEach(check -> ChatUtil.sendMessage(player, ChatColor.RED + "Check " + ChatColor.BLUE + check.getName() +
                        ChatColor.RED + " had an average time of " + ChatColor.GREEN + check.timing().average() + "ms."));
        player.closeInventory();
    }

}
