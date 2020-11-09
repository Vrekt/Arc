package arc.data;

import arc.data.moving.MovingData;
import org.bukkit.entity.Player;

/**
 * A simple utility to register/unregister data
 */
public final class DataUtility {

    /**
     * Remove all data for this player
     *
     * @param player the player
     */
    public static void removeAll(Player player) {
        MovingData.remove(player);
    }

}
