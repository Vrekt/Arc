package bridge.api;

import bridge.utility.BoundingBox;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * A bridge for the Bukkit API.
 * Contains methods that were deprecated for newer versions.
 */
public interface BukkitAccess {

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

    /**
     * Get a potion effect
     *
     * @param player the player
     * @param type   the type
     * @return the {@link PotionEffect}
     */
    PotionEffect getPotionEffect(Player player, PotionEffectType type);

    /**
     * Check if the player is flying with Elytra.
     *
     * @param player the player
     * @return {@code true} if so
     */
    boolean isFlyingWithElytra(Player player);

    /**
     * Check if the player has an item in their hand or off hand
     *
     * @param player   the player
     * @param material the material
     * @return {@code true} if so
     */
    boolean hasItemInHand(Player player, Material material);

    /**
     * Check if a player has slow-falling
     *
     * @param player the player
     * @return {@code true} if so
     */
    boolean hasSlowFalling(Player player);

    /**
     * Check if a player has levitation
     *
     * @param player the player
     * @return {@code true} if so
     */
    boolean hasLevitation(Player player);

}
