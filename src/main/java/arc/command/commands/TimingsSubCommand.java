package arc.command.commands;

import arc.Arc;
import arc.check.Check;
import arc.permissions.Permissions;
import arc.utility.chat.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Allows viewing of timings
 */
public final class TimingsSubCommand extends ArcSubCommand {

    public TimingsSubCommand() {
        super(Permissions.ARC_COMMANDS_TIMINGS);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        Arc.arc().checks().getAllChecks()
                .stream()
                .filter(Check::hasAnyTimings)
                .forEach(check -> ChatUtil.sendMessage(sender, ChatColor.RED + "Check " + ChatColor.BLUE + check.getName() +
                        ChatColor.RED + " had an average time of " + ChatColor.GREEN + check.timing().average() + "ms."));
    }
}
