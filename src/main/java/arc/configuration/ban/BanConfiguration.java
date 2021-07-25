package arc.configuration.ban;

import arc.configuration.ArcConfiguration;
import arc.configuration.Configurable;
import arc.configuration.types.BanLengthType;
import arc.configuration.types.ConfigurationString;
import arc.configuration.values.ConfigurationSetting;
import arc.punishment.litebans.LiteBansCommand;
import org.bukkit.BanList;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The ban configuration
 */
public final class BanConfiguration extends Configurable {

    /**
     * The global ban message
     */
    private ConfigurationString globalBanMessage;

    /**
     * The ban delay
     */
    private int globalBanDelay;

    /**
     * The ban type
     */
    private BanList.Type globalBanType;

    /**
     * The length type
     */
    private BanLengthType globalBanLengthType;

    /**
     * The length
     */
    private int globalBanLength;

    /**
     * If the ban should be broadcasted
     */
    private boolean globalBroadcastBan;

    /**
     * The message to broadcast
     */
    private ConfigurationString globalBroadcastBanMessage;

    /**
     * The message to send to violations
     */
    private ConfigurationString globalViolationsBanMessage;

    /**
     * Lite bans command
     */
    private String liteBansCommand;

    @Override
    public void read(FileConfiguration configuration) {
        globalBanMessage = new ConfigurationString(ChatColor.translateAlternateColorCodes('&', getString(configuration, ConfigurationSetting.GLOBAL_BAN_MESSAGE)));
        globalBanDelay = getInteger(configuration, ConfigurationSetting.GLOBAL_BAN_DELAY);
        globalBanType = getBanListType(configuration, ConfigurationSetting.GLOBAL_BAN_TYPE);
        globalBanLengthType = getBanLengthType(configuration, ConfigurationSetting.GLOBAL_BAN_LENGTH_TYPE);
        globalBanLength = getInteger(configuration, ConfigurationSetting.GLOBAL_BAN_LENGTH);
        globalBroadcastBan = getBoolean(configuration, ConfigurationSetting.GLOBAL_BROADCAST_BAN);
        globalBroadcastBanMessage = new ConfigurationString(ChatColor.translateAlternateColorCodes('&', getString(configuration, ConfigurationSetting.GLOBAL_BROADCAST_BAN_MESSAGE)));
        globalViolationsBanMessage = new ConfigurationString(ChatColor.translateAlternateColorCodes('&', getString(configuration, ConfigurationSetting.GLOBAL_VIOLATIONS_BAN_MESSAGE)));
        liteBansCommand = getString(configuration, ConfigurationSetting.LITE_BANS_COMMAND);
    }

    @Override
    public void reload(ArcConfiguration configuration) {
        read(configuration.fileConfiguration());
    }

    /**
     * @return a new {@link ConfigurationString}
     */
    public ConfigurationString globalBanMessage() {
        return new ConfigurationString(globalBanMessage);
    }

    /**
     * @return the delay
     */
    public int globalBanDelay() {
        return globalBanDelay;
    }

    /**
     * @return the type
     */
    public BanList.Type globalBanType() {
        return globalBanType;
    }

    /**
     * @return the ban length type
     */
    public BanLengthType globalBanLengthType() {
        return globalBanLengthType;
    }

    /**
     * @return the length
     */
    public int globalBanLength() {
        return globalBanLength;
    }

    /**
     * @return if bans should be broadcasted
     */
    public boolean globalBroadcastBan() {
        return globalBroadcastBan;
    }

    /**
     * @return a new {@link ConfigurationString}
     */
    public ConfigurationString globalBroadcastBanMessage() {
        return new ConfigurationString(globalBroadcastBanMessage);
    }

    /**
     * @return a new {@link ConfigurationString}
     */
    public ConfigurationString globalViolationsBanMessage() {
        return new ConfigurationString(globalViolationsBanMessage);
    }

    /**
     * @return the lite ban command
     */
    public LiteBansCommand getLiteBansCommand() {
        return new LiteBansCommand(liteBansCommand);
    }

}
