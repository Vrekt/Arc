package bridge1_8;

import bridge.Bridge;
import bridge.chat.ChatBridge;
import bridge.entities.EntitiesBridge;
import bridge.materials.MaterialsBridge;
import bridge.Version;
import bridge1_8.chat.Chat;
import bridge1_8.entities.Entities;
import bridge1_8.materials.Materials;

/**
 * Legacy bridge for 1.8
 */
public final class Bridge18 implements Bridge {

    /**
     * Materials for 1.8
     */
    public static final Materials MATERIALS = new Materials();

    /**
     * Chat for 1.8
     */
    public static final Chat CHAT = new Chat();

    /**
     * Entities for 1.8
     */
    public static final Entities ENTITIES = new Entities();

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
        return CHAT;
    }

    @Override
    public EntitiesBridge entities() {
        return ENTITIES;
    }
}
