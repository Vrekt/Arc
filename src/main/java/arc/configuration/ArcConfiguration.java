package arc.configuration;

import arc.Arc;
import arc.configuration.punishment.ban.BanConfiguration;
import arc.configuration.punishment.kick.KickConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The arc configuration
 */
public final class ArcConfiguration {

    /**
     * Handles ban configuration values
     */
    private BanConfiguration banConfiguration;
    /**
     * Handles kick configuration values
     */
    private KickConfiguration kickConfiguration;

    /**
     * If TPS should be watched
     * If the event API should be enabled.
     */
    private boolean watchTps, enableEventApi;

    /**
     * The lower limit of when to optimize checks to up the TPS.
     * The violation data timeout
     */
    private int tpsLowerLimit, violationDataTimeout;

    /**
     * The violation message
     * The no permissions message
     * The prefix
     */
    private String violationMessage, noPermissionMessage, prefix;

    /**
     * Read
     * TODO: Invalid configuration
     *
     * @param configuration the configuration
     */
    public void read(FileConfiguration configuration) {
        banConfiguration = new BanConfiguration(configuration);
        kickConfiguration = new KickConfiguration(configuration);

        watchTps = configuration.getBoolean("tps-helper");
        tpsLowerLimit = configuration.getInt("tps-lower-limit");
        enableEventApi = configuration.getBoolean("enable-event-api");
        violationDataTimeout = configuration.getInt("violation-data-timeout");
        violationMessage = ChatColor.translateAlternateColorCodes('&', configuration.getString("violation-notify-message"));
        noPermissionMessage = ChatColor.translateAlternateColorCodes('&', configuration.getString("arc-command-no-permission-message"));
        prefix = ChatColor.translateAlternateColorCodes('&', configuration.getString("arc-prefix"));
    }

    /**
     * @return the ban configuration
     */
    public BanConfiguration banConfiguration() {
        return banConfiguration;
    }

    /**
     * @return the kick configuration
     */
    public KickConfiguration kickConfiguration() {
        return kickConfiguration;
    }

    /**
     * @return if TPS should be watched
     */
    public boolean watchTps() {
        return watchTps;
    }

    /**
     * @return if the event API should be enabled.
     */
    public boolean enableEventApi() {
        return enableEventApi;
    }

    /**
     * @return the tps lower limit
     */
    public int tpsLowerLimit() {
        return tpsLowerLimit;
    }

    /**
     * @return the violation data timeout
     */
    public int violationDataTimeout() {
        return violationDataTimeout;
    }

    /**
     * @return the violation message.
     */
    public String violationMessage() {
        return violationMessage;
    }

    /**
     * @return the no permission message for the /arc command
     */
    public String noPermissionMessage() {
        return noPermissionMessage;
    }

    /**
     * @return the prefix
     */
    public String prefix() {
        return prefix;
    }

    /**
     * @return the {@link FileConfiguration} from {@link Arc}
     */
    public FileConfiguration fileConfiguration() {
        return Arc.arc().getConfig();
    }

    /**
     * Reload the configuration
     */
    public void reloadConfiguration() {
        Arc.arc().reloadConfig();
        final FileConfiguration config = Arc.arc().getConfig();

        read(config);
        Arc.arc().checks().reloadConfiguration(this);
        Arc.arc().violations().reloadConfiguration(this);
    }

}
