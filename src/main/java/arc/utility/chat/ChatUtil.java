package arc.utility.chat;

import arc.Arc;
import arc.utility.api.BukkitAccess;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Basic chat utility for sending messages and broadcasting.
 */
public final class ChatUtil {

    /**
     * Prefix.
     */
    private static final String PREFIX = Arc.arc().configuration().prefix() + " ";

    /**
     * Send a message
     *
     * @param player  the player
     * @param message the message
     */
    public static void sendMessage(Player player, String message) {
        BukkitAccess.sendMessage(player, PREFIX + message);
    }

    /**
     * Send a message
     * TODO: Problems?
     *
     * @param sender  the sender
     * @param message the message
     */
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + message);
    }

}
