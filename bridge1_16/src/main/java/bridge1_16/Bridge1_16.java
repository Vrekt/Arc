package bridge1_16;

import bridge.Bridge;
import bridge.Version;
import bridge.api.BukkitApi;
import bridge.material.MaterialApi;

/**
 * Current bridge for 1.16
 */
public final class Bridge1_16 implements Bridge {

    /**
     * MaterialApi for 1.16
     */
    private static final MaterialApi MATERIAL_API = new bridge1_16.materials.MaterialApi();

    /**
     * Bukkit API for 1.16
     */
    private static final BukkitApi BUKKIT_API = new bridge1_16.api.BukkitApi();

    @Override
    public Version version() {
        return Version.VERSION_1_16;
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
