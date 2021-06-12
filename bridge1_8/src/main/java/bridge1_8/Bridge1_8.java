package bridge1_8;

import bridge.Bridge;
import bridge.Version;
import bridge.api.BukkitApi;
import bridge.material.MaterialApi;

/**
 * Legacy bridge for 1.8
 */
public final class Bridge1_8 implements Bridge {

    /**
     * MaterialApi for 1.8
     */
    private static final MaterialApi MATERIAL_API = new bridge1_8.materials.MaterialApi();

    /**
     * Bukkit API for 1.8
     */
    private static final BukkitApi BUKKIT_API = new bridge1_8.api.BukkitApi();

    @Override
    public Version version() {
        return Version.VERSION_1_8;
    }

    @Override
    public MaterialApi material() {
        return MATERIAL_API;
    }

    @Override
    public BukkitApi api() {
        return BUKKIT_API;
    }
}
