package arc.configuration.kick;

import arc.Arc;
import arc.configuration.ArcConfiguration;
import arc.configuration.Configurable;
import arc.configuration.ConfigurationSettingReader;
import arc.configuration.values.ConfigurationSetting;
import arc.configuration.types.ConfigurationString;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The kick configuration
 */
public final class KickConfiguration extends ConfigurationSettingReader implements Configurable {

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
    public void readFromFile(FileConfiguration configuration) {
        this.globalKickMessage = new ConfigurationString(ChatColor.translateAlternateColorCodes('&', getString(configuration, ConfigurationSetting.GLOBAL_KICK_MESSAGE)));
        this.globalViolationsKickMessage = new ConfigurationString(ChatColor.translateAlternateColorCodes('&', getString(configuration, ConfigurationSetting.GLOBAL_VIOLATIONS_KICK_MESSAGE)));
        this.globalKickDelay = getInteger(configuration, ConfigurationSetting.GLOBAL_KICK_DELAY);
    }

    @Override
    public void reloadConfiguration(ArcConfiguration configuration) {
        readFromFile(Arc.getInstance().getConfig());
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
