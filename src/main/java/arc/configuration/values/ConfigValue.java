package arc.configuration.values;

import arc.configuration.types.BanLengthType;
import org.bukkit.BanList;

import java.util.List;

/**
 * A basic configuration value
 */
public final class ConfigValue<T> {

    /**
     * The value name
     */
    private final String name;

    /**
     * The type
     */
    private final T type;

    /**
     * Initialize
     *
     * @param name the name
     * @param type the type
     */
    public ConfigValue(String name, T type) {
        this.name = name;
        this.type = type;
    }

    /**
     * @return the name
     */
    public String name() {
        return name;
    }

    /**
     * @return the type
     */
    public T type() {
        return type;
    }

    /**
     * @return the string value
     */
    public String asString() {
        return (String) type;
    }

    /**
     * @return the boolean value
     */
    public boolean asBoolean() {
        return (Boolean) type;
    }

    /**
     * @return the int value
     */
    public int asInt() {
        return (Integer) type;
    }

    /**
     * @return the ban list type value
     */
    public BanList.Type asBanListType() {
        return (BanList.Type) type;
    }

    /**
     * @return the ban length type value
     */
    public BanLengthType asBanLengthType() {
        return (BanLengthType) type;
    }

    /**
     * @return the list
     */
    public List<String> asList() {
        return (List<String>) type;
    }

}
