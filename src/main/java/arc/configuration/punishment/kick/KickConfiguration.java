package arc.configuration.punishment.kick;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The kick configuration
 */
public final class KickConfiguration {

    /**
     * The kick message
     */
    private final String kickMessage;

    /**
     * The kick delay.
     */
    private final int kickDelay;

    /**
     * Initialize
     *
     * @param configuration the configuration
     */
    public KickConfiguration(FileConfiguration configuration) {
        kickMessage = ChatColor.translateAlternateColorCodes('&', configuration.getString("kick-message"));
        kickDelay = configuration.getInt("kick-delay");
    }

    /**
     * @return the kick message
     */
    public String kickMessage() {
        return kickMessage;
    }

    /**
     * @return the kick delay
     */
    public int kickDelay() {
        return kickDelay;
    }
}
