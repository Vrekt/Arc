package arc.command.commands;

import arc.Arc;
import arc.configuration.values.ConfigurationSetting;
import arc.permissions.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Allows the sender to enable or disable debug messages
 */
public final class DebugSubCommand extends ArcSubCommand {

    public DebugSubCommand() {
        super(Permissions.ARC_COMMANDS_DEBUG);

        setCommand("/arc debug");
        setUsage("/arc debug");
        setDescription("Allows you to toggle debug messaging.");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        // reverse state, set local state.
        final boolean state = !Arc.getInstance().getArcConfiguration().enableDebugMessages();
        Arc.getInstance().getArcConfiguration().setDebugMessagesState(state);

        // save config state.
        Arc.getInstance().getArcConfiguration().fileConfiguration().set(ConfigurationSetting.DEBUG_MESSAGES.valueName(), state);
        sendMessage(sender, ChatColor.DARK_AQUA + "Debug messages are now " + (state ? ChatColor.GREEN + "on." : ChatColor.RED + "off."));
    }
}
