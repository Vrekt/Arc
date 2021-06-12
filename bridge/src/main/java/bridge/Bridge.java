package bridge;

import bridge.api.BukkitApi;
import bridge.material.MaterialApi;

/**
 * Represents a bridge between different API versions
 */
public interface Bridge {

    /**
     * The current version
     *
     * @return the version
     */
    Version version();

    /**
     * @return the material API.
     */
    MaterialApi material();

    /**
     * @return the Bukkit API.
     */
    BukkitApi api();

}
