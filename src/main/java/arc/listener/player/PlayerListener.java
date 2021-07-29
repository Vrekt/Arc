package arc.listener.player;

import arc.Arc;
import arc.check.types.CheckType;
import arc.check.player.FastUse;
import arc.check.player.Regeneration;
import arc.data.moving.MovingData;
import arc.data.player.PlayerData;
import arc.exemption.type.ExemptionType;
import arc.world.WorldManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

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

    private long last = System.currentTimeMillis();

    public PlayerListener() {
        regeneration = Arc.getInstance().getCheckManager().getCheck(CheckType.REGENERATION);
        fastUse = Arc.getInstance().getCheckManager().getCheck(CheckType.FAST_USE);
    }

    /**
     * Invoked when the player regains some health.
     * Here we can check for Regeneration.
     *
     * @param event the event
     */
    @EventHandler
    private void onRegain(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        // only check if we have regained health from being satisfied.
        if (event.getRegainReason()
                == EntityRegainHealthEvent.RegainReason.SATIATED) {
            final Player player = (Player) event.getEntity();
            if (!WorldManager.isEnabledInWorld(player)
                    || !regeneration.isEnabled()) return;

            final PlayerData data = PlayerData.get(player);
            if (data.lastHealthRegain() != 0) {
                final boolean check = regeneration.check(player, data);
                event.setCancelled(check);
            }

            data.lastHealthRegain(System.currentTimeMillis());
        }
    }

    /**
     * Invoked when the player shoots a bow.
     * Here we can check for FastBow
     *
     * @param event the event
     */
    @EventHandler
    private void onBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player
                && event.getProjectile() instanceof Arrow) {
            final Player player = (Player) event.getEntity();
            if (!WorldManager.isEnabledInWorld(player) || !fastUse.isEnabled()) return;

            final PlayerData data = PlayerData.get(player);
            if (data.lastBowShoot() == 0) {
                data.lastBowShoot(System.currentTimeMillis());
                return;
            }

            final boolean result = fastUse.checkFastBow(player, data);
            event.setCancelled(result);

            data.lastBowShoot(System.currentTimeMillis());
        }
    }

    /**
     * Invoked when the player eats something.
     * Here we can check for FastConsume
     *
     * @param event the event
     */
    @EventHandler
    private void onConsumeItem(PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();
        final PlayerData data = PlayerData.get(player);
        final boolean result = fastUse.checkFastConsume(player, data);
        event.setCancelled(result);
    }

    /**
     * TODO: We want to exempt during teleports!
     *
     * @param event the event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onTeleport(PlayerTeleportEvent event) {
    }

    /**
     * Monitor when the player dies.
     * Add an exemption.
     * <p>
     * TODO: Maybe timeout if player doesn't respawn.
     *
     * @param event the event
     */
    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        Arc.getInstance().getExemptionManager().addExemption(player, ExemptionType.DEATH);
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        Arc.getInstance().getExemptionManager().removeExemption(player, ExemptionType.DEATH);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onWorldChange(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();

        if (WorldManager.isEnabledWorld(player.getWorld())) {
            WorldManager.setPlayerInEnabledWorld(player);
        } else {
            WorldManager.removePlayerInEnabledWorld(player);
        }
    }

    /**
     * Highest priority, run after everything else, to avoid locked chests and regions.
     *
     * @param event e
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onClick(InventoryClickEvent event) {
        final long delta = System.currentTimeMillis() - last;
        last = System.currentTimeMillis();
    }

    /**
     * Handle changing from creative -> survival for movement checks.
     *
     * @param event the event
     */
    @EventHandler
    private void onGameModeChange(PlayerGameModeChangeEvent event) {
        final Player player = event.getPlayer();

        if (event.getNewGameMode() == GameMode.SURVIVAL
                && player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            final MovingData data = MovingData.get(player);

            data.setInAirTime(0);
        }
    }

}
