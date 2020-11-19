package arc.listener.moving;

import arc.Arc;
import arc.check.moving.NoFall;
import arc.data.moving.MovingData;
import arc.listener.moving.tasks.MovingTask;
import arc.utility.MovingUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Listens for player movement
 */
public final class MovingListener implements Listener {

    /**
     * The NoFall task
     */
    private final NoFall noFall;

    /**
     * The moving task.
     * TODO: Reference probably not needed.
     */
    private final MovingTask task = new MovingTask();

    /**
     * Initialize and add all the checks we need.
     */
    public MovingListener() {
        noFall = (NoFall) Arc.arc().checks().getMovingCheck("NoFall");
        Bukkit.getScheduler().runTaskTimer(Arc.plugin(), task, 20, 20);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onMove(PlayerMoveEvent event) {

        final var from = event.getFrom();
        final var to = event.getTo();

        // first, check some things before grabbing moving data and all that
        // to avoid wasting resources/time
        if (from.getWorld() != to.getWorld()) return;

        // check if we have moved, if not return
        final var moved = from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ();
        if (!moved) return;

        final var player = event.getPlayer();
        // finally, check permission exemption.
        if (Arc.arc().permissions().canBypassChecks(player)) return;

        // check if we have moved but only from block to another block.
        boolean hasMovedByBlock =
                from.getBlockX() != to.getBlockX()
                        || from.getBlockY() != to.getBlockY()
                        || from.getBlockZ() != to.getBlockZ();

        // retrieve data and calculate what we need
        final var data = MovingData.get(player);
        MovingUtil.updateMovingPlayer(data, from, to);

        // Check players for NoFall.
        noFall.check(player, data);

        if (hasMovedByBlock) {
            // TODO:
        }

    }

}
