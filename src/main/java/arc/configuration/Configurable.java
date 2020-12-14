package arc.configuration;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Basic class for configuration validation
 */
public abstract class Configurable extends ConfigurableReader {

    /**
     * Read the configuration
     *
     * @param configuration the configuration
     */
    public void read(FileConfiguration configuration) {

    }

    /**
     * Reload the configuration
     *
     * @param configuration the configuration
     */
    public void reload(ArcConfiguration configuration) {

    }

}
