package bridge1_16;

import bridge.Bridge;
import bridge.chat.ChatBridge;
import bridge.materials.MaterialsBridge;
import bridge.Version;

/**
 * Current bridge for 1.16
 */
public final class Bridge1_16 implements Bridge {

    /**
     * Materials for 1.16
     */
    public static final Materials1_16 MATERIALS = new Materials1_16();

    /**
     * Chat for 1.16
     */
    public static final Chat1_16 CHAT_1_16 = new Chat1_16();

    @Override
    public Version version() {
        return Version.VERSION_1_16;
    }

    @Override
    public MaterialsBridge materials() {
        return MATERIALS;
    }

    @Override
    public ChatBridge chat() {
        return CHAT_1_16;
    }
}
