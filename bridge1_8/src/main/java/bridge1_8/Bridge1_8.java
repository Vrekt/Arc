package bridge1_8;

import bridge.Bridge;
import bridge.chat.ChatBridge;
import bridge.materials.MaterialsBridge;
import bridge.Version;

/**
 * Legacy bridge for 1.8
 */
public final class Bridge1_8 implements Bridge {

    /**
     * Materials for 1.8
     */
    public static final Materials1_8 MATERIALS = new Materials1_8();

    /**
     * Chat for 1.8
     */
    public static final Chat1_8 CHAT_1_8 = new Chat1_8();

    @Override
    public Version version() {
        return Version.VERSION_1_8;
    }

    @Override
    public MaterialsBridge materials() {
        return MATERIALS;
    }

    @Override
    public ChatBridge chat() {
        return CHAT_1_8;
    }
}
