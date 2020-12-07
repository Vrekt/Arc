package bridge;

import bridge.chat.ChatBridge;
import bridge.entities.EntitiesBridge;
import bridge.materials.MaterialsBridge;

/**
 * A basic bridge.
 */
public interface Bridge {

    /**
     * The current version
     *
     * @return the version
     */
    Version version();

    /**
     * The bridge materials data
     *
     * @return the {@link MaterialsBridge}
     */
    MaterialsBridge materials();

    /**
     * The bridge for chat
     *
     * @return the {@link ChatBridge}
     */
    ChatBridge chat();

    /**
     * The bridge for entities
     *
     * @return the {@link EntitiesBridge}
     */
    EntitiesBridge entities();

}
