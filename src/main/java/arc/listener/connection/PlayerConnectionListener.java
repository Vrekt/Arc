package arc.listener.connection;

import arc.Arc;
import arc.data.Data;
import arc.data.moving.MovingData;
import arc.utility.MovingUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens for player disconnects/connects
 */
public final class PlayerConnectionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Arc.getInstance().getViolationManager().onPlayerJoin(player);
        Arc.getInstance().getExemptionManager().onPlayerJoin(player);

        initializePlayerData(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        Arc.getInstance().getViolationManager().onPlayerLeave(player);
        Arc.getInstance().getExemptionManager().onPlayerLeave(player);
        Data.removeAll(player);
    }

    /**
     * Initialize player data.
     *
     * @param player the player
     */
    private void initializePlayerData(Player player) {
        final MovingData data = MovingData.get(player);
        MovingUtil.calculateMovement(data, player.getLocation(), player.getLocation());
    }

}
