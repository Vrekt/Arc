package arc.configuration;

import arc.Arc;
import arc.configuration.ban.BanConfiguration;
import arc.configuration.kick.KickConfiguration;
import arc.configuration.types.ConfigurationString;
import arc.configuration.values.ConfigurationValues;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The arc configuration
 */
public final class ArcConfiguration extends Configurable {

    /**
     * Handles ban configuration values
     */
    private final BanConfiguration banConfiguration = new BanConfiguration();
    /**
     * Handles kick configuration values
     */
    private final KickConfiguration kickConfiguration = new KickConfiguration();

    /**
     * If check timings should be enabled.
     * If the TPS helper should be enabled.
     * If the event API should be enabled.
     */
    private boolean enableCheckTimings, enableTpsHelper, enableEventApi;

    /**
     * TPS helper limit
     * The time after leaving violation data times out
     */
    private int tpsHelperLimit, violationDataTimeout;

    /**
     * Violation notify message
     */
    private ConfigurationString violationNotifyMessage;

    /**
     * Command no permission message
     */
    private String commandNoPermissionMessage;

    /**
     * Prefix
     */
    private String prefix;

    @Override
    public void read(FileConfiguration configuration) {
        kickConfiguration.read(configuration);
        banConfiguration.read(configuration);

        enableCheckTimings = bool(configuration, ConfigurationValues.ENABLE_CHECK_TIMINGS);
        enableTpsHelper = bool(configuration, ConfigurationValues.ENABLE_TPS_HELPER);
        tpsHelperLimit = integer(configuration, ConfigurationValues.TPS_HELPER_LIMIT);
        violationNotifyMessage = new ConfigurationString(ChatColor.translateAlternateColorCodes('&', string(configuration, ConfigurationValues.VIOLATION_NOTIFY_MESSAGE)));
        commandNoPermissionMessage = ChatColor.translateAlternateColorCodes('&', string(configuration, ConfigurationValues.ARC_COMMAND_NO_PERMISSION_MESSAGE));
        prefix = ChatColor.translateAlternateColorCodes('&', string(configuration, ConfigurationValues.ARC_PREFIX));
        violationDataTimeout = integer(configuration, ConfigurationValues.VIOLATION_DATA_TIMEOUT);
        enableEventApi = bool(configuration, ConfigurationValues.ENABLE_EVENT_API);
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
     * @return if check timings are enabled
     */
    public boolean enableCheckTimings() {
        return enableCheckTimings;
    }

    /**
     * @return if TPS helper is enabled.
     */
    public boolean enableTpsHelper() {
        return enableTpsHelper;
    }

    /**
     * @return if event API is enabled.
     */
    public boolean enableEventApi() {
        return enableEventApi;
    }

    /**
     * @return TPS helper limit
     */
    public int tpsHelperLimit() {
        return tpsHelperLimit;
    }

    /**
     * @return violation data timeout
     */
    public int violationDataTimeout() {
        return violationDataTimeout;
    }

    /**
     * @return violation notify message
     */
    public ConfigurationString violationNotifyMessage() {
        return new ConfigurationString(violationNotifyMessage);
    }

    /**
     * @return command no permission message
     */
    public String commandNoPermissionMessage() {
        return commandNoPermissionMessage;
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
        Arc.plugin().reloadConfig();

        final FileConfiguration configuration = Arc.plugin().getConfig();
        read(configuration);

        Arc.arc().checks().reload(this);
        Arc.arc().violations().reload(this);
        Arc.arc().punishment().reload(this);
    }

}
