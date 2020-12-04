package bridge.chat;

import net.md_5.bungee.api.chat.TextComponent;

/**
 * Bridge for ChatComponent API.
 */
public interface ChatBridge {

    /**
     * Add the hover event
     *
     * @param component   the component
     * @param information the information
     */
    void addHoverEvent(TextComponent component, String information);

}
