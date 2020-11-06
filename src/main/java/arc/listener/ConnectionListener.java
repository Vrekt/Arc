package arc.listener;

import arc.Arc;
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
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerLeave(PlayerQuitEvent event) {
        final var player = event.getPlayer();
        Arc.arc().violations().onPlayerLeave(player);

        Arc.arc().permissions().onPlayerLeave(player);
    }

}
