package arc.utility;

import arc.permissions.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * A basic chat utility
 */
public final class ChatUtil {

    private static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Arc" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE;

    /**
     * Broadcast to the violations permission
     *
     * @param message the message
     */
    public static void broadcastToViolations(String message) {
        Bukkit.broadcast(PREFIX + message, Permissions.ARC_VIOLATIONS);
    }

}
