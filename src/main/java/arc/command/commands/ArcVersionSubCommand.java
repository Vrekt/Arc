package arc.command.commands;

import arc.Arc;
import arc.permissions.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Simple version command.
 */
public final class ArcVersionSubCommand extends ArcSubCommand {

    public ArcVersionSubCommand() {
        super(Permissions.ARC_COMMANDS_BASE);

        setCommand("/arc version");
        setDescription("Allows you view the current version of Arc.");
        setUsage("/arc version");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        sendMessage(sender, ChatColor.DARK_AQUA + "Current version of Arc is: " + ChatColor.GRAY + Arc.VERSION_STRING);
    }
}
