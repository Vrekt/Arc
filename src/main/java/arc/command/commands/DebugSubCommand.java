package arc.command.commands;

import arc.Arc;
import arc.configuration.values.ConfigurationValues;
import arc.permissions.Permissions;
import arc.utility.chat.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Allows the sender to enable or disable debug messages
 */
public final class DebugSubCommand extends ArcSubCommand {

    public DebugSubCommand() {
        super(Permissions.ARC_COMMANDS_DEBUG);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        // reverse state, set local state.
        final boolean state = !Arc.arc().configuration().enableDebugMessages();
        Arc.arc().configuration().setDebugMessagesState(state);

        // save config state.
        Arc.arc().configuration().fileConfiguration().set(ConfigurationValues.DEBUG_MESSAGES.valueName(), state);
        ChatUtil.sendMessage(sender, ChatColor.GRAY + "Debug messages are now " + (state ? ChatColor.GREEN + "on." : ChatColor.RED + "off."));
    }
}
