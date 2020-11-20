package arc.listener.player;

import arc.Arc;
import arc.check.CheckType;
import arc.check.player.Regeneration;
import arc.data.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

/**
 * Listens for player related events
 */
public final class PlayerListener implements Listener {

    /**
     * The regeneration check
     */
    private final Regeneration regeneration;

    public PlayerListener() {
        regeneration = (Regeneration) Arc.arc().checks().getCheck(CheckType.REGENERATION);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onRegain(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        // only check if we have regained health from being satisfied.
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            final var player = (Player) event.getEntity();
            final var data = PlayerData.get(player);
            if (data.lastHealthRegain() != 0) {
                regeneration.check(player, data, (violation) -> event.setCancelled(violation.cancel()));
            }

            data.lastHealthRegain(System.currentTimeMillis());
        }
    }
}
