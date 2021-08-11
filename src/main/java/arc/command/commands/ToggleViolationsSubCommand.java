package arc.command.commands;

import arc.Arc;
import arc.permissions.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Toggle player violations
 */
public final class ToggleViolationsSubCommand extends ArcSubCommand {

    public ToggleViolationsSubCommand() {
        super(Permissions.ARC_COMMANDS_TOGGLE_VIOLATIONS);

        setCommand("/arc violations");
        setUsage("/arc violations");
        setDescription("Allows you to toggle violation messages.");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (!isPlayer(sender)) {
            printNoPlayer(sender);
            return;
        }

        final Player player = (Player) sender;
        final boolean state = toggleState(player);
        sendMessage(player, ChatColor.DARK_AQUA + "Violations are now " + (state ? ChatColor.GREEN + "on." : ChatColor.RED + "off."));
    }

    /**
     * Toggle state
     *
     * @param player the player
     * @return {@code true} if violations are on.
     */
    private boolean toggleState(Player player) {
        return Arc.getInstance().getViolationManager().toggleViolationsViewer(player);
    }

}
