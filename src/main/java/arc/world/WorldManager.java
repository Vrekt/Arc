package arc.world;

import arc.Arc;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages per world context things.
 */
public final class WorldManager {

    /**
     * Set of players in a compatible world.
     */
    private static final Set<UUID> PLAYERS_IN_ENABLED_WORLD = ConcurrentHashMap.newKeySet();

    /**
     * Check if the world is enabled
     *
     * @param world the world
     * @return {@code true} if so
     */
    public static boolean isEnabledWorld(World world) {
        return Arc.getInstance().getArcConfiguration().getWorldsEnabledIn().contains(world.getName());
    }

    /**
     * Check if the provided {@code player} is in an enabled world.
     *
     * @param player the player
     * @return {@code true} if so
     */
    public static boolean isEnabledInWorld(Player player) {
        return PLAYERS_IN_ENABLED_WORLD.contains(player.getUniqueId());
    }

    /**
     * Set that the player is in an enabled world.
     *
     * @param player the player
     */
    public static void setPlayerInEnabledWorld(Player player) {
        if (PLAYERS_IN_ENABLED_WORLD.contains(player.getUniqueId())) return;
        PLAYERS_IN_ENABLED_WORLD.add(player.getUniqueId());
    }

    /**
     * Remove that the player is in an enabled world
     *
     * @param player the player
     */
    public static void removePlayerInEnabledWorld(Player player) {
        PLAYERS_IN_ENABLED_WORLD.remove(player.getUniqueId());
    }

}
