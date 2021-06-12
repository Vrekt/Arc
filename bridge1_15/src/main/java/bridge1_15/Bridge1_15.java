package bridge1_15;

import bridge.Bridge;
import bridge.Version;
import bridge.api.BukkitApi;
import bridge.material.MaterialApi;

/**
 * Current bridge for 1.15
 */
public final class Bridge1_15 implements Bridge {

    /**
     * MaterialApi for 1.15
     */
    private static final MaterialApi MATERIAL_API = new bridge1_15.materials.MaterialApi();

    /**
     * Bukkit API for 1.15
     */
    private static final BukkitApi BUKKIT_API = new bridge1_15.api.BukkitApi();

    @Override
    public Version version() {
        return Version.VERSION_1_15;
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
