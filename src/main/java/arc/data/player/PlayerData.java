package arc.data.player;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores player related check data
 */
public final class PlayerData {

    /**
     * The register
     */
    private static final Map<UUID, PlayerData> REGISTER = new ConcurrentHashMap<>();

    /**
     * Get data
     *
     * @param player the player
     * @return the data
     */
    public static PlayerData get(Player player) {
        return REGISTER.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData());
    }

    /**
     * Remove data
     *
     * @param player the player
     */
    public static void remove(Player player) {
        REGISTER.remove(player.getUniqueId());
    }

    /**
     * When the player last regained health.
     */
    private long lastHealthRegain;

    public long lastHealthRegain() {
        return lastHealthRegain;
    }

    public void lastHealthRegain(long lastHealthRegain) {
        this.lastHealthRegain = lastHealthRegain;
    }
}
