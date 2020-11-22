package arc.command.commands;

import arc.Arc;
import arc.command.ArcSubCommand;
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
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return;
        }

        final var player = (Player) sender;
        final var violations = Arc.arc().violations();
        if (violations.isViolationViewer(player)) {
            player.sendMessage(ChatColor.GREEN + "You will no longer see violations in chat.");
            violations.removeViolationViewer(player);
        } else {
            player.sendMessage(ChatColor.GREEN + "You will now see violations in chat.");
            violations.addViolationViewer(player);
        }

    }
}
