package arc.command.commands;

import arc.Arc;
import arc.check.types.CheckType;
import arc.permissions.Permissions;
import arc.utility.chat.ColoredChat;
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


        Player toExempt;
        boolean isMyself = false;
        if (arguments.length == 1) {
            if (!isPlayer(sender)) {
                sendErrorMessage(sender, "You must be a player to exempt yourself.");
                return;
            }

            toExempt = (Player) sender;
            isMyself = true;
        } else {
            toExempt = Bukkit.getPlayer(arguments[0]);
            if (toExempt == null) {
                sendErrorMessage(sender, "That player must be online to be exempted.");
                return;
            }
        }

        final String check = arguments.length == 1 ? arguments[0] : arguments[1];
        if (check.equalsIgnoreCase("all")) {
            Arc.getInstance().getExemptionManager().addExemptionPermanently(toExempt, CheckType.values());

            ColoredChat.forRecipient(sender)
                    .setMainColor(ChatColor.DARK_AQUA)
                    .setParameterColor(ChatColor.GRAY)
                    .messageIf(isMyself, "You are now exempt from all checks.")
                    .parameterIf(!isMyself, toExempt.getName())
                    .messageIf(!isMyself, " is now exempt from all checks.")
                    .send();
        } else {
            final CheckType checkType = CheckType.getCheckTypeByName(check);
            if (checkType == null) {
                sendErrorMessage(sender, "Check not found.");
                return;
            }

            Arc.getInstance().getExemptionManager().addExemptionPermanently(toExempt, checkType);

            ColoredChat.forRecipient(sender)
                    .setMainColor(ChatColor.DARK_AQUA)
                    .setParameterColor(ChatColor.GRAY)
                    .messageIf(isMyself, "You are now exempt from ")
                    .parameterIf(isMyself, checkType.getPrettyName())
                    .parameterIf(!isMyself, toExempt.getName())
                    .messageIf(!isMyself, " is now exempt from ")
                    .parameterIf(!isMyself, checkType.getPrettyName())
                    .send();
        }

        ColoredChat.forRecipient(sender)
                .message("WARNING: ", ChatColor.GOLD, ChatColor.BOLD)
                .message("Exemptions added via commands will not persist over a sever reload, plugin reload or restart.", ChatColor.YELLOW)
                .send();
    }

    /**
     * Exempt a player.
     *
     * @param sender
     * @param player
     * @param isSender
     * @param checks
     */
    private void exemptPlayer(CommandSender sender, Player player, boolean isSender, CheckType... checks) {

    }

}
