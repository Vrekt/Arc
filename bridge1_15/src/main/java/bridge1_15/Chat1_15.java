package bridge1_15;

import bridge.chat.ChatBridge;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * 1.15 chat API.
 */
public final class Chat1_15 implements ChatBridge {

    @Override
    public void addHoverEvent(TextComponent component, String information) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(information).create()));
    }

}
