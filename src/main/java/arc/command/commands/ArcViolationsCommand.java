package arc.command.commands;

import arc.Arc;
import arc.command.ArcSubCommand;
import arc.utility.ChatUtil;
import arc.violation.ViolationManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles toggling violations
 */
public final class ArcViolationsCommand extends ArcSubCommand {

    @Override
    protected void execute(CommandSender sender, String[] arguments) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendMessage(sender, ChatColor.RED + " You must be a player to use this command.");
            return;
        }

        final Player player = (Player) sender;
        final ViolationManager violations = Arc.arc().violations();
        if (violations.isViolationViewer(player)) {
            ChatUtil.sendMessage(player, ChatColor.GRAY + " Violations are now " + ChatColor.RED + "off.");
            violations.removeViolationViewer(player);
        } else {
            ChatUtil.sendMessage(player, ChatColor.GRAY + " Violations are now " + ChatColor.GREEN + "on.");
            violations.addViolationViewer(player);
        }

    }
}
