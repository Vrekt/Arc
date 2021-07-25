package arc.configuration;

import arc.Arc;
import arc.configuration.ban.BanConfiguration;
import arc.configuration.kick.KickConfiguration;
import arc.configuration.types.ConfigurationString;
import arc.configuration.values.ConfigurationSetting;
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
     * If debug messages are enabled.
     */
    private boolean enableCheckTimings, enableTpsHelper, enableEventApi, debugMessages;

    /**
     * Ban plugin support
     */
    private boolean useLiteBans;

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

        enableCheckTimings = getBoolean(configuration, ConfigurationSetting.ENABLE_CHECK_TIMINGS);
        enableTpsHelper = getBoolean(configuration, ConfigurationSetting.ENABLE_TPS_HELPER);
        tpsHelperLimit = getInteger(configuration, ConfigurationSetting.TPS_HELPER_LIMIT);
        violationNotifyMessage = new ConfigurationString(ChatColor.translateAlternateColorCodes('&', getString(configuration, ConfigurationSetting.VIOLATION_NOTIFY_MESSAGE)));
        commandNoPermissionMessage = ChatColor.translateAlternateColorCodes('&', getString(configuration, ConfigurationSetting.ARC_COMMAND_NO_PERMISSION_MESSAGE));
        prefix = ChatColor.translateAlternateColorCodes('&', getString(configuration, ConfigurationSetting.ARC_PREFIX));
        violationDataTimeout = getInteger(configuration, ConfigurationSetting.VIOLATION_DATA_TIMEOUT);
        enableEventApi = getBoolean(configuration, ConfigurationSetting.ENABLE_EVENT_API);
        debugMessages = getBoolean(configuration, ConfigurationSetting.DEBUG_MESSAGES);
        useLiteBans = getBoolean(configuration, ConfigurationSetting.USE_LITE_BANS);

        if(useLiteBans) {
            Arc.getPlugin().getLogger().info("Arc will be using LiteBans to punish players.");
        }
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
     * @return if enabled debug messages
     */
    public boolean enableDebugMessages() {
        return debugMessages;
    }

    /**
     * Set the debug messages state.
     *
     * @param debugMessages the state
     */
    public void setDebugMessagesState(boolean debugMessages) {
        this.debugMessages = debugMessages;
    }

    /**
     * @return if the LiteBans plugin should be used for punishment.
     */
    public boolean useLiteBans() {
        return useLiteBans;
    }

    /**
     * Set use lite bans
     *
     * @param useLiteBans the lite bans
     */
    public void setUseLiteBans(boolean useLiteBans) {
        this.useLiteBans = useLiteBans;
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
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return the {@link FileConfiguration} from {@link Arc}
     */
    public FileConfiguration fileConfiguration() {
        return Arc.getPlugin().getConfig();
    }

    /**
     * Reload the configuration
     */
    public void reloadConfiguration() {
        Arc.getPlugin().reloadConfig();

        final FileConfiguration configuration = Arc.getPlugin().getConfig();
        read(configuration);

        Arc.getInstance().getCheckManager().reload(this);
        Arc.getInstance().getViolationManager().reload(this);
        Arc.getInstance().getExemptionManager().reload(this);
        Arc.getInstance().getPunishmentManager().reload(this);
    }

}
