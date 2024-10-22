package arc.configuration.types;

import arc.Arc;
import arc.check.Check;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Represents a configuration string.
 * Allows easy replacement of placeholders.
 */
public final class ConfigurationString {

    /**
     * The value
     */
    private String value;

    /**
     * Initialize
     *
     * @param value the configuration value
     */
    public ConfigurationString(String value) {
        this.value = value;
    }

    /**
     * Initialize
     *
     * @param other the other
     */
    public ConfigurationString(ConfigurationString other) {
        this.value = other.value;
    }

    /**
     * Replace player value
     *
     * @param player the player
     * @return this
     */
    public ConfigurationString player(Player player) {
        value = StringUtils.replace(value, Placeholders.PLAYER.placeholder(), player.getName());
        return this;
    }

    /**
     * Replace check value
     *
     * @param check the check
     * @return this
     */
    public ConfigurationString check(Check check) {
        value = StringUtils.replace(value, Placeholders.CHECK.placeholder(), check.getPrettyName() + " ");
        return this;
    }

    /**
     * Replace check value
     *
     * @param check   the check
     * @param subType the sub-type
     * @return this
     */
    public ConfigurationString check(Check check, String subType) {
        if (subType != null) {
            value = StringUtils.replace(value, Placeholders.CHECK.placeholder(), check.getPrettyName() + " " + ChatColor.GRAY + subType);
        } else {
            value = StringUtils.replace(value, Placeholders.CHECK.placeholder(), check.getPrettyName() + " ");
        }
        return this;
    }

    /**
     * Replace level value
     *
     * @param level the level
     * @return this
     */
    public ConfigurationString level(int level) {
        value = StringUtils.replace(value, Placeholders.LEVEL.placeholder(), level + "");
        return this;
    }

    /**
     * Replace prefix
     *
     * @return this
     */
    public ConfigurationString prefix() {
        value = StringUtils.replace(value, Placeholders.PREFIX.placeholder(), Arc.getInstance().getArcConfiguration().getPrefix());
        return this;
    }

    /**
     * Replace time
     *
     * @param time the time
     * @return this
     */
    public ConfigurationString time(int time) {
        value = StringUtils.replace(value, Placeholders.TIME.placeholder(), time + "");
        return this;
    }

    /**
     * Replace check sub-type
     * <p>
     * TODO
     *
     * @param subType the type
     * @return this
     */
    public ConfigurationString subType(String subType) {
        return this;
    }

    /**
     * Replace ban type
     *
     * @return this
     */
    public ConfigurationString type() {
        value = StringUtils.replace(value, Placeholders.TYPE.placeholder(), Arc.getInstance().getArcConfiguration().getBanConfiguration().globalBanLengthType().prettyName());
        return this;
    }

    /**
     * Replace a placeholder
     *
     * @param placeholder the placeholder
     * @param value       the value
     * @return this
     */
    public ConfigurationString replace(Placeholders placeholder, String value) {
        this.value = StringUtils.replace(this.value, placeholder.placeholder(), value);
        return this;
    }

    /**
     * @return the value
     */
    public String value() {
        return value;
    }

}
