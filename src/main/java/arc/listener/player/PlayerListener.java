package arc.listener.player;

import arc.Arc;
import arc.check.CheckType;
import arc.check.player.FastUse;
import arc.check.player.Regeneration;
import arc.data.player.PlayerData;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

/**
 * Listens for player related events
 */
public final class PlayerListener implements Listener {

    /**
     * The regeneration check
     */
    private final Regeneration regeneration;

    /**
     * The fast use check
     */
    private final FastUse fastUse;

    public PlayerListener() {
        regeneration = (Regeneration) Arc.arc().checks().getCheck(CheckType.REGENERATION);
        fastUse = (FastUse) Arc.arc().checks().getCheck(CheckType.FAST_USE);
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

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player
                && event.getProjectile() instanceof Arrow) {
            final var player = (Player) event.getEntity();
            final var data = PlayerData.get(player);
            if (data.lastBowShoot() == 0) {
                data.lastBowShoot(System.currentTimeMillis());
                return;
            }

            final var result = fastUse.checkFastBow(player, data);
            event.setCancelled(result.cancel());

            data.lastBowShoot(System.currentTimeMillis());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onConsumeItem(PlayerItemConsumeEvent event) {
        final var player = event.getPlayer();
        final var data = PlayerData.get(player);
        final var result = fastUse.checkFastConsume(player, data);
        event.setCancelled(result.cancel());
    }

}
