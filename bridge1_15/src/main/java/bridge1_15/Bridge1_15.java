package bridge1_15;

import bridge.Bridge;
import bridge.chat.ChatBridge;
import bridge.materials.MaterialsBridge;
import bridge.Version;

/**
 * Current bridge for 1.16
 */
public final class Bridge1_15 implements Bridge {

    /**
     * Materials for 1.15
     */
    public static final Materials1_15 MATERIALS = new Materials1_15();

    /**
     * Chat for 1.15
     */
    public static final Chat1_15 CHAT1_15 = new Chat1_15();

    @Override
    public Version version() {
        return Version.VERSION_1_15;
    }

    @Override
    public MaterialsBridge materials() {
        return MATERIALS;
    }

    @Override
    public ChatBridge chat() {
        return CHAT1_15;
    }
}
