package arc.data;

import arc.data.block.BlockData;
import arc.data.combat.CombatData;
import arc.data.moving.MovingData;
import arc.data.packet.PacketData;
import arc.data.player.PlayerData;
import org.bukkit.entity.Player;

/**
 * A basic data interface
 */
public interface Data {

    /**
     * Unregister all data for the provided {@code player}
     *
     * @param player the player
     */
    static void removeAll(Player player) {
        CombatData.remove(player);
        MovingData.remove(player);
        PacketData.remove(player);
        PlayerData.remove(player);
        BlockData.remove(player);
    }

}
