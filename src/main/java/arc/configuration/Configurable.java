package arc.configuration;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Handles configuration actions like reading and reloading.
 */
public interface Configurable {

    /**
     * Load initial configuration
     *
     * @param configuration the config
     */
    default void loadConfiguration(ArcConfiguration configuration) {
        throw new UnsupportedOperationException("Cannot load");
    }

    /**
     * Read from file configuration
     *
     * @param configuration the config
     */
    default void readFromFile(FileConfiguration configuration) {
        throw new UnsupportedOperationException("Cannot read from file");
    }

    /**
     * Read from arc configuration
     *
     * @param configuration configuration
     */
    default void readFromArc(ArcConfiguration configuration) {
        throw new UnsupportedOperationException("Cannot read from Arc.");
    }

    /**
     * Reload the configuration
     *
     * @param configuration the configuration
     */
    default void reloadConfiguration(ArcConfiguration configuration) {
        throw new UnsupportedOperationException("Cannot reload");
    }

}
