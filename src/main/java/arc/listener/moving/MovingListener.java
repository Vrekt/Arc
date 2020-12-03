package arc.listener.moving;

import arc.Arc;
import arc.check.CheckType;
import arc.check.moving.Flight;
import arc.check.moving.Jesus;
import arc.check.moving.NoFall;
import arc.data.moving.MovingData;
import arc.permissions.Permissions;
import arc.utility.MovingUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Listens for player movement
 */
public final class MovingListener implements Listener {

    /**
     * The NoFall check
     */
    private final NoFall noFall;

    /**
     * The Jesus check
     */
    private final Jesus jesus;

    /**
     * The flight check
     */
    private final Flight flight;

    /**
     * Initialize and add all the checks we need.
     */
    public MovingListener() {
        noFall = (NoFall) Arc.arc().checks().getCheck(CheckType.NOFALL);
        jesus = (Jesus) Arc.arc().checks().getCheck(CheckType.JESUS);
        flight = (Flight) Arc.arc().checks().getCheck(CheckType.FLIGHT);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onMove(PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (to == null) return;

        // first, check some things before grabbing moving data and all that
        // to avoid wasting resources/time
        if (from.getWorld() != to.getWorld()) return;

        // check if we have moved, if not return
        final boolean moved = from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ();
        if (!moved) return;

        final Player player = event.getPlayer();
        // finally, check permission exemption.
        if (Permissions.canBypassChecks(player)) return;

        // check if we have moved but only from block to another block.
        boolean hasMovedByBlock =
                from.getBlockX() != to.getBlockX()
                        || from.getBlockY() != to.getBlockY()
                        || from.getBlockZ() != to.getBlockZ();

        // retrieve data and calculate what we need
        final MovingData data = MovingData.get(player);
        MovingUtil.updateMovingPlayer(data, from, to);

        // Check players for Flight.
        flight.check(player, data, event);

        // Check players for NoFall.
        noFall.check(player, data);

        if (hasMovedByBlock) {
            // check players for Jesus.
            jesus.check(player, data, event);
        }
    }
}
