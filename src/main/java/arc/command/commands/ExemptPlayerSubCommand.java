package arc.command.commands;

import arc.Arc;
import arc.check.types.CheckType;
import arc.permissions.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A sub command for exempting from checks
 */
public final class ExemptPlayerSubCommand extends ArcSubCommand {

    public ExemptPlayerSubCommand() {
        super(Permissions.ARC_COMMANDS_EXEMPT);

        setCommand("/arc exempt (player) (checkName or all)");
        setUsage("/arc exempt (player) (checkName or all)");
        setDescription("Allows you to exempt players manually.");
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            printUsage(sender);
            return;
        }

        Player who;
        boolean isMyself = false;

        if (arguments.length == 1) {
            if (!isPlayer(sender)) {
                sendErrorMessage(sender, "You must be a player to exempt yourself.");
                return;
            }

            who = (Player) sender;
            isMyself = true;
        } else {
            who = Bukkit.getPlayer(arguments[0]);
            if (who == null) {
                sendErrorMessage(sender, ChatColor.RED + "Player not found.");
                return;
            }
        }

        final String check = arguments.length == 1 ? arguments[0] : arguments[1];
        if (check.equalsIgnoreCase("all")) {
            Arc.getInstance().getExemptionManager().addExemptionPermanently(who, CheckType.values());
            sendMessage(sender, (isMyself ? ChatColor.DARK_AQUA + "You are now exempt from all checks."
                    : ChatColor.GRAY + who.getName() + ChatColor.DARK_AQUA + " is now exempt from all checks."));
        } else {
            final CheckType checkType = CheckType.getCheckTypeByName(check);
            if (checkType == null) {
                sendErrorMessage(sender, "Check not found.");
                return;
            }

            Arc.getInstance().getExemptionManager().addExemptionPermanently(who, checkType);
            sendMessage(sender, (isMyself ? ChatColor.DARK_AQUA + "You are now exempt from " + ChatColor.GRAY + checkType.getPrettyName()
                    : ChatColor.GRAY + who.getName() + ChatColor.DARK_AQUA + " is now exempt from " + ChatColor.GRAY + checkType.getPrettyName()));
        }

        sendMessage(sender, (ChatColor.GOLD + "" + ChatColor.BOLD) + "WARNING: " + ChatColor.YELLOW
                + "This will not persist over a server reload, plugin reload or server restart.");
    }
}
