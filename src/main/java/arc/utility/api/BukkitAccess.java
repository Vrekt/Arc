package arc.utility.api;

import arc.Arc;
import bridge.utility.BoundingBox;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
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


    /**
     * Check if the player is gliding
     *
     * @param player the player
     * @return {@code true} if so
     */
    public static boolean isFlyingWithElytra(Player player) {
        return ACCESS.isFlyingWithElytra(player);
    }

    /**
     * Check if the player has an item in their hand or off hand
     *
     * @param player   the player
     * @param material the material
     * @return {@code true} if so
     */
    public static boolean hasItemInHand(Player player, Material material) {
        return ACCESS.hasItemInHand(player, material);
    }

    public static long getChunkKey(Chunk chunk) {
        return getChunkKey(chunk.getX(), chunk.getZ());
    }

    /**
     * @param x X Coordinate
     * @param z Z Coordinate
     * @return Chunk coordinates packed into a long
     */
    public static long getChunkKey(int x, int z) {
        return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
    }

    /**
     * Returns this block's coordinates packed into a long value.
     * Computed via: {@code Block.getBlockKey(this.getX(), this.getY(), this.getZ())}
     *
     * @return This block's x, y, and z coordinates packed into a long value
     * @see Block#getBlockKey(int, int, int)
     */
    public static long getBlockKey(Block block) {
        return getBlockKey(block.getX(), block.getY(), block.getZ());
    }

    /**
     * Returns the specified block coordinates packed into a long value
     * <p>
     * The return value can be computed as follows:
     * <br>
     * {@code long value = ((long)x & 0x7FFFFFF) | (((long)z & 0x7FFFFFF) << 27) | ((long)y << 54);}
     * </p>
     *
     * <p>
     * And may be unpacked as follows:
     * <br>
     * {@code int x = (int) ((packed << 37) >> 37);}
     * <br>
     * {@code int y = (int) (packed >>> 54);}
     * <br>
     * {@code int z = (int) ((packed << 10) >> 37);}
     * </p>
     *
     * @return This block's x, y, and z coordinates packed into a long value
     */
    public static long getBlockKey(int x, int y, int z) {
        return ((long) x & 0x7FFFFFF) | (((long) z & 0x7FFFFFF) << 27) | ((long) y << 54);
    }

}
