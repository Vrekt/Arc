package arc.command.commands;

import arc.Arc;
import arc.inventory.InventoryCreator;
import arc.permissions.Permissions;
import arc.utility.chat.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

/**
 * Toggle player violations
 */
public final class ToggleViolationsSubCommand extends ArcSubCommand {

    public ToggleViolationsSubCommand() {
        super(Permissions.ARC_COMMANDS_TOGGLE_VIOLATIONS);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (!isPlayer(sender)) {
            printNoPlayer(sender);
            return;
        }

        final Player player = (Player) sender;
        final boolean state = toggleState(player);
        ChatUtil.sendMessage(player, ChatColor.GRAY + "Violations are now " + (state ? ChatColor.GREEN + "on." : ChatColor.RED + "off."));
    }

    @Override
    public void executeFromInventory(ItemStack item, InventoryCreator inventory, Player player) {
        final ItemStack modified = item.clone();
        final boolean violations = Arc.arc().violations().toggleViolationsViewer(player);

        final ItemMeta meta = modified.getItemMeta();
        if (meta != null) {
            // TODO @Deprecated
            meta.setLore(Collections.singletonList(ChatColor.GRAY + "Violations are currently " + (violations ? ChatColor.GREEN + "on." : ChatColor.RED + "off.")));
        }
        modified.setItemMeta(meta);
        inventory.replace(item, modified);
    }

    /**
     * Toggle state
     *
     * @param player the player
     * @return {@code true} if violations are on.
     */
    private boolean toggleState(Player player) {
        return Arc.arc().violations().toggleViolationsViewer(player);
    }

}
