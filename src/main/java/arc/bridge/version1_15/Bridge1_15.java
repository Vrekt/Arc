package arc.bridge.version1_15;

import arc.bridge.Bridge;
import arc.bridge.MaterialsBridge;
import arc.bridge.Version;

/**
 * Current bridge for 1.16
 */
public final class Bridge1_15 implements Bridge {

    /**
     * Current materials
     */
    public static final Materials1_15 MATERIALS = new Materials1_15();

    @Override
    public Version current() {
        return Version.VERSION_1_15;
    }

    @Override
    public MaterialsBridge materials() {
        return MATERIALS;
    }
}
