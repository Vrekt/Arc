package arc.configuration;

import arc.Arc;
import arc.configuration.ban.BanConfiguration;
import arc.configuration.kick.KickConfiguration;
import arc.configuration.types.ConfigurationString;
import arc.configuration.values.ConfigurationSetting;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * The arc configuration
 */
public final class ArcConfiguration extends ConfigurationSettingReader implements Configurable {

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

    /**
     * List of worlds enabled in
     */
    private final List<String> worldsEnabledIn = new ArrayList<>();

    @Override
    public void readFromFile(FileConfiguration configuration) {
        kickConfiguration.readFromFile(configuration);
        banConfiguration.readFromFile(configuration);

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
        worldsEnabledIn.addAll(getStringList(configuration, ConfigurationSetting.ENABLED_WORLDS));
    }

    /**
     * @return the ban configuration
     */
    public BanConfiguration getBanConfiguration() {
        return banConfiguration;
    }

    /**
     * @return the kick configuration
     */
    public KickConfiguration getKickConfiguration() {
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
    public ConfigurationString getViolationMessage() {
        return new ConfigurationString(violationNotifyMessage);
    }

    /**
     * @return command no permission message
     */
    public String getNoPermissionMessage() {
        return commandNoPermissionMessage;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return get worlds list
     */
    public List<String> getWorldsEnabledIn() {
        return worldsEnabledIn;
    }

    /**
     * Reload the configuration and all configurable components.
     */
    public void reloadConfigurationAndComponents() {
        Arc.getPlugin().reloadConfig();

        this.readFromFile(Arc.getPlugin().getConfig());
        Arc.getInstance().getCheckManager().reloadConfiguration(this);
        Arc.getInstance().getViolationManager().reloadConfiguration(this);
        Arc.getInstance().getExemptionManager().reloadConfiguration(this);
        Arc.getInstance().getPunishmentManager().reloadConfiguration(this);
    }

}
