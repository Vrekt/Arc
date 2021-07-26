package arc.configuration;

import arc.configuration.types.BanLengthType;
import arc.configuration.values.ConfigurationSetting;
import org.bukkit.BanList;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * A extendable utility for reading values, which will be set if non-existent.
 */
public abstract class ConfigurationSettingReader {

    /**
     * Get a string
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the string
     */
    protected String getString(FileConfiguration configuration, ConfigurationSetting value) {
        final String str = configuration.getString(value.valueName(), value.asString());
        if (!configuration.contains(value.valueName())) {
            configuration.set(value.valueName(), value.asString());
        }
        return str;
    }

    /**
     * Get an integer
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the int
     */
    protected int getInteger(FileConfiguration configuration, ConfigurationSetting value) {
        final int number = configuration.getInt(value.valueName(), value.asInt());
        if (!configuration.contains(value.valueName())) {
            configuration.set(value.valueName(), value.asInt());
        }
        return number;
    }

    /**
     * Get ban list type
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the {@link BanList.Type}
     */
    protected BanList.Type getBanListType(FileConfiguration configuration, ConfigurationSetting value) {
        final String raw = configuration.getString(value.valueName());
        if (raw == null || (!raw.equalsIgnoreCase("IP")
                && !raw.equalsIgnoreCase("NAME"))) {
            configuration.set(value.valueName(), value.asBanListType().name());
            return value.asBanListType();
        }

        return BanList.Type.valueOf(raw);
    }

    /**
     * Get ban length type
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the {@link BanLengthType}
     */
    protected BanLengthType getBanLengthType(FileConfiguration configuration, ConfigurationSetting value) {
        final String raw = configuration.getString(value.valueName());
        if (raw == null) {
            configuration.set(value.valueName(), value.asBanLengthType().name());
            return value.asBanLengthType();
        }
        return BanLengthType.parse(raw);
    }

    /**
     * Get a boolean
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the boolean
     */
    protected boolean getBoolean(FileConfiguration configuration, ConfigurationSetting value) {
        final boolean b = configuration.getBoolean(value.valueName(), value.asBoolean());

        if (!configuration.contains(value.valueName())) {
            configuration.set(value.valueName(), value.asBoolean());
        }
        return b;
    }

    /**
     * Get a string list
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the list
     */
    @SuppressWarnings("unchecked")
    protected List<String> getStringList(FileConfiguration configuration, ConfigurationSetting value) {
        final List<String> list = (List<String>) configuration.getList(value.valueName(), value.asList());
        if (!configuration.contains(value.valueName())) {
            configuration.set(value.valueName(), list);
        }
        return list;
    }

}
