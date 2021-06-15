package arc.configuration.kick;

import arc.configuration.ArcConfiguration;
import arc.configuration.Configurable;
import arc.configuration.values.ConfigurationValues;
import arc.configuration.types.ConfigurationString;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The kick configuration
 */
public final class KickConfiguration extends Configurable {

    /**
     * The global kick message.
     */
    private ConfigurationString globalKickMessage;

    /**
     * The global violations kick message
     */
    private ConfigurationString globalViolationsKickMessage;

    /**
     * The global kick delay.
     */
    private int globalKickDelay;

    @Override
    public void read(FileConfiguration configuration) {
        this.globalKickMessage = new ConfigurationString(ChatColor.translateAlternateColorCodes('&', getString(configuration, ConfigurationValues.GLOBAL_KICK_MESSAGE)));
        this.globalViolationsKickMessage = new ConfigurationString(ChatColor.translateAlternateColorCodes('&', getString(configuration, ConfigurationValues.GLOBAL_VIOLATIONS_KICK_MESSAGE)));
        this.globalKickDelay = getInteger(configuration, ConfigurationValues.GLOBAL_KICK_DELAY);
    }

    @Override
    public void reload(ArcConfiguration configuration) {
        read(configuration.fileConfiguration());
    }

    /**
     * @return the global kick message
     */
    public ConfigurationString globalKickMessage() {
        return globalKickMessage;
    }

    /**
     * @return The global violations kick message
     */
    public ConfigurationString globalViolationsKickMessage() {
        return globalViolationsKickMessage;
    }

    /**
     * @return the global kick delay
     */
    public int globalKickDelay() {
        return globalKickDelay;
    }
}
