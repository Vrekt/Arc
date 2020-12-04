package bridge.chat;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Basic chat builder.
 */
public abstract class ChatBuilder {

    /**
     * List of players
     */
    private Set<Player> players;

    /**
     * The component
     */
    protected TextComponent component;

    /**
     * Set for players
     *
     * @param players the players
     * @return this
     */
    public ChatBuilder forPlayers(Set<Player> players) {
        this.players = players;
        return this;
    }

    /**
     * Set the text component
     *
     * @param text the text
     * @return this
     */
    public ChatBuilder setText(String text) {
        component = new TextComponent(text);
        return this;
    }

    /**
     * Set the information
     *
     * @param text the text
     * @return this
     */
    public abstract ChatBuilder setInformation(String text);

    /**
     * Send to all
     */
    public void sendToAll() {
        players.forEach(player -> player.spigot().sendMessage(component));
        players.clear();
        players = null;
    }

}
