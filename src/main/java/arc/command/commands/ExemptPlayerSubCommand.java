package arc.command.commands;

import arc.Arc;
import arc.check.types.CheckType;
import arc.permissions.Permissions;
import arc.utility.chat.ChatUtil;
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
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if (arguments.length == 0) {
            ChatUtil.sendMessage(sender, ChatColor.RED + "Usage: /arc exempt <player> <check|all>");
            return;
        }

        Player who;
        boolean isMyself = false;

        if (arguments.length == 1) {
            if (!isPlayer(sender)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to exempt yourself.");
                return;
            }

            who = (Player) sender;
            isMyself = true;
        } else {
            who = Bukkit.getPlayer(arguments[0]);
            if (who == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return;
            }
        }

        final String check = arguments.length == 1 ? arguments[0] : arguments[1];
        if (check.equalsIgnoreCase("all")) {
            Arc.getInstance().getExemptionManager().addExemptionPermanently(who, CheckType.values());
            sender.sendMessage(ChatColor.GREEN + (isMyself ? "You are " : "The player " + ChatColor.RED + who.getName() + ChatColor.GREEN + "is ") + "now exempt from all checks.");
        } else {
            final CheckType checkType = CheckType.getCheckTypeByName(check);
            if (checkType == null) {
                sender.sendMessage(ChatColor.RED + "Check not found.");
                return;
            }
            Arc.getInstance().getExemptionManager().addExemptionPermanently(who, checkType);
            sender.sendMessage(ChatColor.GREEN + (isMyself ? "You are " : "The player " + ChatColor.RED + who.getName() + ChatColor.GREEN + "is ") + " now exempt from " + ChatColor.RED + checkType.name());
        }
    }
}
