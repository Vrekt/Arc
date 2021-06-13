package bridge1_12;

import bridge.Bridge;
import bridge.Version;
import bridge.api.BukkitApi;
import bridge.material.MaterialApi;

/**
 * Current bridge for 1.12
 */
public final class Bridge1_12 implements Bridge {

    /**
     * MaterialApi for 1.12
     */
    private static final MaterialApi MATERIAL_API = new bridge1_12.materials.MaterialApi();

    /**
     * Bukkit API for 1.12
     */
    private static final BukkitApi BUKKIT_API = new bridge1_12.api.BukkitApi();

    @Override
    public Version version() {
        return Version.VERSION_1_12;
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
