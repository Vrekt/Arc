package bridge;

import bridge.api.BukkitAccess;
import bridge.block.BlockAccess;
import bridge.material.MaterialAccess;

/**
 * Represents a bridge between different API versions
 */
public interface Bridge {

    /**
     * The current version
     *
     * @return the version
     */
    Version getVersion();

    /**
     * @return the block access
     */
    BlockAccess getBlockAccess();

    /**
     * @return the material access
     */
    MaterialAccess getMaterialAccess();

    /**
     * @return the Bukkit access
     */
    BukkitAccess getBukkitAccess();

}
