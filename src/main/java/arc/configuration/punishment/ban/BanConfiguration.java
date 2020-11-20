package arc.configuration.punishment.ban;

import org.bukkit.BanList;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The ban configuration
 */
public final class BanConfiguration {

    /**
     * The various messages.
     */
    private final String banMessage, banBroadcastMessage, banMessageToViolations;

    /**
     * The delay and length
     */
    private final int banDelay, banLength;

    /**
     * The ban type
     */
    private final BanList.Type banType;

    /**
     * The ban length type.
     */
    private final BanLengthType banLengthType;

    /**
     * If the ban should be broadcasted.
     */
    private final boolean broadcastBan;

    /**
     * Initialize
     *
     * @param configuration the configuration
     */
    public BanConfiguration(FileConfiguration configuration) {
        banMessage = ChatColor.translateAlternateColorCodes('&', configuration.getString("ban-message"));
        banBroadcastMessage = ChatColor.translateAlternateColorCodes('&', configuration.getString("broadcast-ban-message"));
        banMessageToViolations = ChatColor.translateAlternateColorCodes('&', configuration.getString("ban-message-to-violations"));

        banDelay = configuration.getInt("ban-delay");
        banLength = configuration.getInt("ban-length");

        banType = BanList.Type.valueOf(configuration.getString("ban-type"));
        banLengthType = BanLengthType.parse(configuration.getString("ban-length-type"));
        broadcastBan = configuration.getBoolean("broadcast-ban");
    }

    /**
     * @return the ban message
     */
    public String banMessage() {
        return banMessage;
    }

    /**
     * @return the broadcast message
     */
    public String banBroadcastMessage() {
        return banBroadcastMessage;
    }

    /**
     * @return the message to display to violation viewers.
     */
    public String banMessageToViolations() {
        return banMessageToViolations;
    }

    /**
     * @return the ban delay
     */
    public int banDelay() {
        return banDelay;
    }

    /**
     * @return the ban length
     */
    public int banLength() {
        return banLength;
    }

    /**
     * @return the type of ban
     */
    public BanList.Type banType() {
        return banType;
    }

    /**
     * @return the ban length type
     */
    public BanLengthType banLengthType() {
        return banLengthType;
    }

    /**
     * @return if bans should be broadcasted
     */
    public boolean broadcastBan() {
        return broadcastBan;
    }
}
