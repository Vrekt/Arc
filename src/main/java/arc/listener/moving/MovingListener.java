package arc.listener.moving;

import arc.Arc;
import arc.check.moving.NoFall;
import arc.data.moving.MovingData;
import arc.location.Locations;
import arc.utility.MathUtil;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Listens for player movement
 */
public final class MovingListener implements Listener {

    /**
     * The no fall check
     */
    private final NoFall noFall = new NoFall();

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
        // TODO: Is debug
        if (Arc.arc().exemptions().isPlayerExempt(player, true)) return;

        // check if we have moved but only from block to another block.
        boolean hasMovedByBlock =
                from.getBlockX() != to.getBlockX()
                        || from.getBlockY() != to.getBlockY()
                        || from.getBlockZ() != to.getBlockZ();

        // retrieve data and calculate what we need
        final var movingData = MovingData.get(player);
        calculateGround(movingData, event.getFrom(), event.getTo());

        noFall.check(player, movingData);
    }

    /**
     * Calculate on-ground, ascending/descending states, vertical dist, etc
     *
     * @param data the moving data
     * @param from the previous location
     * @param to   the current location
     */
    private void calculateGround(MovingData data, Location from, Location to) {
        data.from(from);
        data.to(to);

        // calculate ground
        final var wasOnGround = data.onGround();
        final var onGround = Locations.onGround(to);
        data.onGround(onGround);
        data.wasOnGround(wasOnGround);

        if (onGround) {
            data.ground(to);
            data.onGroundTime(data.onGroundTime() + 1);
        } else {
            data.onGroundTime(0);
        }

        // calculate vertical distance
        final var distance = MathUtil.distance(from, to);
        data.lastVerticalDistance(data.lastVerticalDistance());
        data.verticalDistance(distance);

        // set ascending/descending states
        data.ascending(to.getY() > from.getY() && distance > 0.0);
        data.descending(from.getY() > to.getY() && distance > 0.0);
        data.climbing(distance > 0.0 && Locations.hasClimbable(to));
    }

}
