package arc.check.implementations;

import arc.Arc;
import arc.check.Check;
import arc.check.types.CheckType;
import bridge.Version;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Represents a check that can have multiple different checks for different versions.
 */
public abstract class MultiVersionCheck extends Check {

    /**
     * Version
     */
    protected static final Version VERSION = Arc.getMCVersion();

    protected MultiVersionCheck(CheckType checkType) {
        super(checkType);
    }

    /**
     * Register a version section.
     * This will create a new configuration section within the config.
     * In turn you can have different parameters based on the version.
     *
     * @param version the version
     */
    protected void registerVersion(Version version) {
        configuration.createVersionSection(version);
    }

    /**
     * Add a value to a version section.
     *
     * @param version   the version
     * @param valueName the value name
     * @param value     the value
     */
    protected void addValueToVersion(Version version, String valueName, Object value) {
        configuration.addConfigurationValueFor(version, valueName, value);
    }

    /**
     * Get the version section
     *
     * @return the section
     */
    protected ConfigurationSection getVersionSection() {
        return configuration.getVersion(VERSION);
    }

}
