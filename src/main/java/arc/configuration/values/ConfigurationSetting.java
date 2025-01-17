package arc.configuration.values;

import arc.configuration.types.BanLengthType;
import org.bukkit.BanList;

import java.util.Arrays;
import java.util.List;

/**
 * A map of all default configuration settings with the Arc configuration.
 *
 * Does not include anything related to checks
 */
public enum ConfigurationSetting {
    GLOBAL_KICK_MESSAGE(new ConfigValue<>("global-kick-message", "&cYou have been kicked for %check%")),
    GLOBAL_KICK_DELAY(new ConfigValue<>("global-kick-delay", 0)),
    GLOBAL_VIOLATIONS_KICK_MESSAGE(new ConfigValue<>("global-violations-kick-message", "%prefix% &9%player%&f will be kicked for &c%check%&f in &c%time%&f seconds.")),
    GLOBAL_BAN_MESSAGE(new ConfigValue<>("global-ban-message", "&cYou have been banned for %check%")),
    GLOBAL_BAN_DELAY(new ConfigValue<>("global-ban-delay", 0)),
    GLOBAL_BAN_TYPE(new ConfigValue<>("global-ban-type", BanList.Type.NAME)),
    GLOBAL_BAN_LENGTH_TYPE(new ConfigValue<>("global-ban-length-type", BanLengthType.DAYS)),
    GLOBAL_BAN_LENGTH(new ConfigValue<>("global-ban-length", 30)),
    GLOBAL_BROADCAST_BAN(new ConfigValue<>("global-broadcast-ban", false)),
    GLOBAL_BROADCAST_BAN_MESSAGE(new ConfigValue<>("global-broadcast-ban-message", "&c%player% was banned for %check% for %time% %type%")),
    GLOBAL_VIOLATIONS_BAN_MESSAGE(new ConfigValue<>("global-violations-ban-message", "%prefix% &9%player%&f will be banned for &c%check%&f in &c%time%&f seconds.")),
    ENABLE_CHECK_TIMINGS(new ConfigValue<>("enable-check-timings", true)),
    ENABLE_TPS_HELPER(new ConfigValue<>("enable-tps-helper", true)),
    TPS_HELPER_LIMIT(new ConfigValue<>("tps-helper-limit", 17)),
    VIOLATION_NOTIFY_MESSAGE(new ConfigValue<>("violation-notify-message", "%prefix% &9%player%&f has violated check &c%check%&8(&c%level%&8)&7")),
    ARC_COMMAND_NO_PERMISSION_MESSAGE(new ConfigValue<>("arc-command-no-permission-message", "Unknown command. Type /help for help.")),
    ARC_PREFIX(new ConfigValue<>("arc-prefix", "&8[&cArc&8]")),
    VIOLATION_DATA_TIMEOUT(new ConfigValue<>("violation-data-timeout", 30)),
    ENABLE_EVENT_API(new ConfigValue<>("enable-event-api", true)),
    DEBUG_MESSAGES(new ConfigValue<>("debug-messages", false)),
    USE_LITE_BANS(new ConfigValue<>("use-lite-bans", false)),
    LITE_BANS_COMMAND(new ConfigValue<>("lite-bans-command", "%player% -s %length% %reason%")),
    ENABLED_WORLDS(new ConfigValue<>("enabled-worlds", Arrays.asList("world", "world_the_nether", "world_the_end")));

    /**
     * The value
     */
    private final ConfigValue<?> value;

    ConfigurationSetting(ConfigValue<?> type) {
        this.value = type;
    }

    /**
     * @return the value name
     */
    public String valueName() {
        return value.name();
    }

    /**
     * @return the string value
     */
    public String asString() {
        return value.asString();
    }

    /**
     * @return the boolean value
     */
    public boolean asBoolean() {
        return value.asBoolean();
    }

    /**
     * @return the int value
     */
    public int asInt() {
        return value.asInt();
    }

    /**
     * @return the ban list type value
     */
    public BanList.Type asBanListType() {
        return value.asBanListType();
    }

    /**
     * @return the ban length type value
     */
    public BanLengthType asBanLengthType() {
        return value.asBanLengthType();
    }

    /**
     * @return the list type value
     */
    public List<String> asList() {
        return value.asList();
    }

}
