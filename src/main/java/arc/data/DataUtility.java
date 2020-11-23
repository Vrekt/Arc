package arc.data;

import arc.data.moving.MovingData;
import arc.data.packet.PacketData;
import arc.data.player.PlayerData;
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
        PacketData.remove(player);
        PlayerData.remove(player);
    }

}
