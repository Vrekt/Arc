package bridge1_16;

import bridge.Bridge;
import bridge.chat.ChatBridge;
import bridge.entities.EntitiesBridge;
import bridge.materials.MaterialsBridge;
import bridge.Version;
import bridge1_16.chat.Chat;
import bridge1_16.entities.Entities;
import bridge1_16.materials.Materials;

/**
 * Current bridge for 1.16
 */
public final class Bridge116 implements Bridge {

    /**
     * Materials for 1.16
     */
    public static final Materials MATERIALS = new Materials();

    /**
     * Chat for 1.16
     */
    public static final Chat CHAT = new Chat();

    /**
     * Entities for 1.16
     */
    public static final Entities ENTITIES = new Entities();

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
        return CHAT;
    }

    @Override
    public EntitiesBridge entities() {
        return ENTITIES;
    }
}
