package arc.configuration;

/**
 * Configuration reloadable
 */
public interface Reloadable {

    /**
     * Reload the configuration
     * @param configuration the configuration
     */
    void reloadConfiguration(ArcConfiguration configuration);

}
