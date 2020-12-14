package arc.configuration;

import arc.configuration.types.BanLengthType;
import arc.configuration.values.ConfigurationValues;
import org.bukkit.BanList;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Allows easy reading and setting missing values
 */
public abstract class ConfigurableReader {

    /**
     * Get a string
     *
     * @param configuration the configuration
     * @param value         the value
     * @return the string
     */
    protected String string(FileConfiguration configuration, ConfigurationValues value) {
        final String str = configuration.getString(value.valueName(), value.stringValue());
        if (!configuration.contains(value.valueName())) {
            configuration.set(value.valueName(), value.stringValue());
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
    protected int integer(FileConfiguration configuration, ConfigurationValues value) {
        final int number = configuration.getInt(value.valueName(), value.intValue());
        if (!configuration.contains(value.valueName())) {
            configuration.set(value.valueName(), value.intValue());
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
    protected BanList.Type banListType(FileConfiguration configuration, ConfigurationValues value) {
        final String raw = configuration.getString(value.valueName());
        if (raw == null || (!raw.equalsIgnoreCase("IP")
                && !raw.equalsIgnoreCase("NAME"))) {
            configuration.set(value.valueName(), value.banListTypeValue().name());
            return value.banListTypeValue();
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
    protected BanLengthType banLengthType(FileConfiguration configuration, ConfigurationValues value) {
        final String raw = configuration.getString(value.valueName());
        if (raw == null) {
            configuration.set(value.valueName(), value.banLengthTypeValue().name());
            return value.banLengthTypeValue();
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
    protected boolean bool(FileConfiguration configuration, ConfigurationValues value) {
        final boolean b = configuration.getBoolean(value.valueName(), value.booleanValue());
        if (!configuration.contains(value.valueName())) {
            configuration.set(value.valueName(), value.booleanValue());
        }
        return b;
    }

}
