package arc.bridge.version1_8;

import arc.bridge.Bridge;
import arc.bridge.MaterialsBridge;
import arc.bridge.Version;

/**
 * Legacy bridge for 1.8
 */
public final class Bridge1_8 implements Bridge {

    /**
     * Legacy materials
     */
    public static final Materials1_8 MATERIALS = new Materials1_8();

    @Override
    public Version current() {
        return Version.VERSION_1_8;
    }

    @Override
    public MaterialsBridge materials() {
        return MATERIALS;
    }
}
