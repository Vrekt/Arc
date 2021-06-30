package arc.utility.api;

import arc.Arc;
import bridge.utility.BoundingBox;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Bukkit API compatibility
 */
public final class BukkitAccess {

    /**
     * Access
     */
    private static final bridge.api.BukkitAccess ACCESS = Arc.getBridge().getBukkitAccess();

    /**
     * Create an inventory
     *
     * @param title the title
     * @param size  the size
     * @return the new inventory
     */
    public static Inventory createInventory(String title, int size) {
        return ACCESS.createInventory(title, size);
    }

    /**
     * Broadcast a message
     *
     * @param message    the message
     * @param permission the permission
     */
    public static void broadcast(String message, String permission) {
        ACCESS.broadcast(message, permission);
    }

    /**
     * Broadcast a message
     *
     * @param message the message
     */
    public static void broadcast(String message) {
        ACCESS.broadcast(message);
    }

    /**
     * Kick a player
     *
     * @param player  the player
     * @param message the message
     */
    public static void kickPlayer(Player player, String message) {
        ACCESS.kickPlayer(player, message);
    }

    /**
     * Send a message to a player
     *
     * @param player  the player
     * @param message the message
     */
    public static void sendMessage(Player player, String message) {
        ACCESS.sendMessage(player, message);
    }

    /**
     * Send a message to a player
     *
     * @param player  the player
     * @param message the message
     */
    public static void sendMessage(Player player, TextComponent message) {
        ACCESS.sendMessage(player, message);
    }

    /**
     * Add the hover event
     *
     * @param component   the component
     * @param information the information
     */
    public static void addHoverEvent(TextComponent component, String information) {
        ACCESS.addHoverEvent(component, information);
    }

    /**
     * Get a bounding box of an entity
     *
     * @param entity the entity
     * @return the {@link BoundingBox}
     */
    public static BoundingBox getBoundingBox(Entity entity) {
        return ACCESS.getBoundingBox(entity);
    }

    /**
     * Get a potion effect
     *
     * @param player the player
     * @param type   the type
     * @return the {@link PotionEffect}
     */
    public static PotionEffect getPotionEffect(Player player, PotionEffectType type) {
        return ACCESS.getPotionEffect(player, type);
    }
}
