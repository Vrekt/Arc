package arc.command.commands;

import arc.Arc;
import arc.check.timing.CheckTimings;
import arc.inventory.InventoryCreator;
import arc.permissions.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Allows viewing of timings
 */
public final class TimingsSubCommand extends ArcSubCommand {

    public TimingsSubCommand() {
        super(Permissions.ARC_COMMANDS_TIMINGS);

        setCommand("/arc timings");
        setUsage("/arc timings");
        setDescription("Allows you to view timings information.");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (!Arc.getInstance().getArcConfiguration().enableCheckTimings()) {
            sendErrorMessage(sender, "Check timings are disabled in the configuration.");
            return;
        }

        final StringBuilder builder = new StringBuilder();
        CheckTimings.getAllTimings()
                .keySet()
                .forEach(check -> {
                    final double avg = Math.floor((CheckTimings.getAverageTiming(check)) * 1000) / 1000;
                    final double toMs = Math.floor((avg / 1e+6) * 1000) / 1000;
                    sendMessage(sender, ChatColor.GRAY
                            + check.getPrettyName()
                            + ChatColor.DARK_AQUA + " took on average "
                            + ChatColor.DARK_GRAY + "("
                            + ChatColor.GRAY + avg
                            + ChatColor.DARK_GRAY + ")"
                            + ChatColor.DARK_AQUA + " ns or "
                            + ChatColor.DARK_GRAY + "("
                            + ChatColor.GRAY + toMs
                            + ChatColor.DARK_GRAY + ")"
                            + ChatColor.DARK_AQUA + " ms.");
                });
        ;
    }

    @Override
    public void executeInventory(ItemStack item, InventoryCreator inventory, Player player) {
        execute(player, null);
    }
}
