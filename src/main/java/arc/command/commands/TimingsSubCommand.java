package arc.command.commands;

import arc.Arc;
import arc.check.timing.CheckTimings;
import arc.permissions.Permissions;
import arc.utility.chat.ColoredChat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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

                    ColoredChat.forRecipient(sender)
                            .setMainColor(ChatColor.DARK_AQUA)
                            .setParameterColor(ChatColor.GRAY)
                            .parameter(check.getPrettyName())
                            .message(" took on average ")
                            .parameter(avg + "")
                            .message(" ns or ")
                            .parameter(toMs + "")
                            .message(" ms.")
                            .send();
                });
        ;
    }
}
