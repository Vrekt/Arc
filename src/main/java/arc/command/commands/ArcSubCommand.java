package arc.command.commands;

import arc.inventory.InventoryCreator;
import arc.utility.chat.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A basic sub command.
 */
public abstract class ArcSubCommand {

    /**
     * Permission required
     */
    private final String permission;

    /**
     * The permission required.
     *
     * @param permission permission
     */
    public ArcSubCommand(String permission) {
        this.permission = permission;
    }

    /**
     * Check if the provided {@code sender} has the required {@code permission}
     *
     * @param sender the sender
     * @return {@code true} if so
     */
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(permission);
    }

    /**
     * Execute this sub-command
     *
     * @param sender    the sender
     * @param arguments the arguments
     */
    public abstract void execute(CommandSender sender, String[] arguments);

    /**
     * Execute this command from an inventory interface.
     *
     * @param item      the item
     * @param inventory the inventory
     * @param player    the player
     */
    public void executeFromInventory(ItemStack item, InventoryCreator inventory, Player player) {

    }

    /**
     * Check if the sender is a player
     *
     * @param sender the sender
     * @return {@code true} if so
     */
    protected boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    /**
     * Print no player
     *
     * @param sender the sender
     */
    protected void printNoPlayer(CommandSender sender) {
        ChatUtil.sendMessage(sender, ChatColor.RED + "You must be a player to do this.");
    }

}
