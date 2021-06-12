package bridge.api;

import bridge.utility.BoundingBox;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * A bridge for the Bukkit API.
 * Contains methods that were deprecated for newer versions.
 */
public interface BukkitApi {

    /**
     * Create an inventory
     *
     * @param title the title
     * @param size  the size
     * @return the new inventory
     */
    Inventory createInventory(String title, int size);

    /**
     * Broadcast a message
     *
     * @param message    the message
     * @param permission the permission
     */
    void broadcast(String message, String permission);


    /**
     * Broadcast a message
     *
     * @param message the message
     */
    void broadcast(String message);

    /**
     * Kick a player
     *
     * @param player  the player
     * @param message the message
     */
    void kickPlayer(Player player, String message);

    /**
     * Send a message to a player
     *
     * @param player  the player
     * @param message the message
     */
    void sendMessage(Player player, String message);


    /**
     * Send a message to a player
     *
     * @param player  the player
     * @param message the message
     */
    void sendMessage(Player player, TextComponent message);

    /**
     * Add the hover event
     *
     * @param component   the component
     * @param information the information
     */
    void addHoverEvent(TextComponent component, String information);

    /**
     * Get a bounding box of an entity
     *
     * @param entity the entity
     * @return the {@link BoundingBox}
     */
    BoundingBox getBoundingBox(Entity entity);

}
