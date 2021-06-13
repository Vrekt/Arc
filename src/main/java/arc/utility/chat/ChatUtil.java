package arc.utility.chat;

import arc.Arc;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Basic chat utility for sending messages and broadcasting.
 */
public final class ChatUtil {

    /**
     * Send a message
     *
     * @param player  the player
     * @param message the message
     */
    public static void sendMessage(Player player, String message) {
        Arc.bridge().api().sendMessage(player, Arc.arc().configuration().prefix() + " " + message);
    }

    /**
     * Send a message
     * TODO: Problems?
     *
     * @param sender  the sender
     * @param message the message
     */
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(Arc.arc().configuration().prefix() + " " + message);
    }

}
