package bridge1_16;

import bridge.Bridge;
import bridge.MaterialsBridge;
import bridge.Version;

/**
 * Current bridge for 1.16
 */
public final class Bridge1_16 implements Bridge {

    /**
     * Current materials
     */
    public static final Materials1_16 MATERIALS = new Materials1_16();

    @Override
    public Version current() {
        return Version.VERSION_1_16;
    }

    @Override
    public MaterialsBridge materials() {
        return MATERIALS;
    }
}
