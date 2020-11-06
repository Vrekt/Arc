package arc.check;

import arc.Arc;
import arc.check.result.CheckResult;
import arc.configuration.check.CheckConfiguration;
import arc.violation.result.ViolationResult;
import org.bukkit.entity.Player;

/**
 * Represents a check.
 */
public abstract class Check {

    /**
     * The check name
     */
    private final String name;

    /**
     * The check configuration
     */
    private final CheckConfiguration configuration;

    protected Check(String name) {
        this.name = name;

        configuration = new CheckConfiguration(Arc.plugin().getConfig().getConfigurationSection(name.toLowerCase()));
    }

    /**
     * Add a configuration value
     *
     * @param valueName the value name
     * @param value     the value
     */
    protected void addConfigurationValue(String valueName, Object value) {
        if (containsValue(valueName)) return;
        Arc.plugin().getConfig().set(name + "." + valueName, value);
    }

    /**
     * Check if a value exists.
     *
     * @param valueName the value name
     * @return {@code true} if so.
     */
    protected boolean containsValue(String valueName) {
        return Arc.plugin().getConfig().contains(name + "." + valueName);
    }

    /**
     * Get a value
     *
     * @param valueName the value name
     * @return the value
     */
    protected int getValueInt(String valueName) {
        return Arc.plugin().getConfig().getInt(name + "." + valueName);
    }

    /**
     * Get a value
     *
     * @param valueName the value name
     * @return the value
     */
    protected double getValueDouble(String valueName) {
        return Arc.plugin().getConfig().getDouble(name + "." + valueName);
    }

    /**
     * Get a value
     *
     * @param valueName the value name
     * @return the value
     */
    protected boolean getValueBoolean(String valueName) {
        return Arc.plugin().getConfig().getBoolean(name + "." + valueName);
    }

    /**
     * Process the check result.
     *
     * @param result the result
     */
    protected ViolationResult processResult(Player player, CheckResult result) {
        if (result.failed()) {
            return Arc.arc().violations().violation(player, this, result);
        }
        return ViolationResult.EMPTY;
    }

    /**
     * @return the check name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the check configuration
     */
    public CheckConfiguration configuration() {
        return configuration;
    }
}
