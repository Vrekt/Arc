package arc.listener;

import arc.Arc;
import arc.data.DataUtility;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens for player disconnects/connects
 */
public final class ConnectionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent event) {
        final var player = event.getPlayer();
        Arc.arc().violations().onPlayerJoin(player);
        Arc.arc().permissions().onPlayerJoin(player);
        Arc.arc().exemptions().onPlayerJoin(player);
    }

    /**
     * TODO: We don't want to remove violation data
     *
     * @param event e
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerLeave(PlayerQuitEvent event) {
        final var player = event.getPlayer();
        Arc.arc().violations().onPlayerLeave(player);
        Arc.arc().permissions().onPlayerLeave(player);
        Arc.arc().exemptions().onPlayerLeave(player);
        DataUtility.removeAll(player);
    }

}
