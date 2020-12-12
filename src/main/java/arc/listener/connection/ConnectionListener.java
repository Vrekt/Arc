package arc.listener.connection;

import arc.Arc;
import arc.data.combat.CombatData;
import arc.data.moving.MovingData;
import arc.data.packet.PacketData;
import arc.data.player.PlayerData;
import org.bukkit.entity.Player;
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
        final Player player = event.getPlayer();
        Arc.arc().violations().onPlayerJoin(player);
        Arc.arc().exemptions().onPlayerJoin(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        Arc.arc().violations().onPlayerLeave(player);
        Arc.arc().exemptions().onPlayerLeave(player);

        CombatData.remove(player);
        MovingData.remove(player);
        PacketData.remove(player);
        PlayerData.remove(player);
    }

}
