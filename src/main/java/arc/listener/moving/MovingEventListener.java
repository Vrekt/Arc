package arc.listener.moving;

import arc.Arc;
import arc.check.CheckType;
import arc.check.moving.Flight;
import arc.check.moving.Jesus;
import arc.check.moving.NoFall;
import arc.check.moving.Speed;
import arc.data.moving.MovingData;
import arc.utility.MovingUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Listens and handles the {@link org.bukkit.event.player.PlayerMoveEvent}
 */
public final class MovingEventListener implements Listener {

    /**
     * The flight check
     */
    private final Flight flight;

    /**
     * The jesus check
     */
    private final Jesus jesus;

    /**
     * The NoFall check
     */
    private final NoFall noFall;

    /**
     * The speed check
     */
    private final Speed speed;

    public MovingEventListener() {
        flight = (Flight) Arc.arc().checks().getCheck(CheckType.FLIGHT);
        jesus = (Jesus) Arc.arc().checks().getCheck(CheckType.JESUS);
        noFall = (NoFall) Arc.arc().checks().getCheck(CheckType.NOFALL);
        speed = (Speed) Arc.arc().checks().getCheck(CheckType.SPEED);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onMoveLowest(PlayerMoveEvent event) {
        if (event.getFrom().getWorld() != event.getTo().getWorld()) return;

        Player player = event.getPlayer();
        MovingData data = MovingData.get(player);

        Location from = event.getFrom();
        Location to = event.getTo();

        // check if we have moved.
        boolean hasMoved = from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ();
        if (hasMoved) {
            MovingUtil.calculateMovement(data, from, to);
            // run checks
            runChecks(player, data);
        }


        // check if we have moved but only from block to another block.
        boolean hasMovedByBlock = from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ();
        if (hasMovedByBlock) {
            runBlockChecks(player, data);
        }
    }

    /**
     * Run movement related checks
     *
     * @param player the player
     * @param data   their data
     */
    private void runChecks(Player player, MovingData data) {
        if (flight.enabled()) {
            flight.check(player, data);
        }

        if (noFall.enabled()) {
            noFall.check(player, data);
        }
    }

    /**
     * Run movement - but block restricted checks
     *
     * @param player the player
     * @param data   their data
     */
    private void runBlockChecks(Player player, MovingData data) {
        if (jesus.enabled()) jesus.check(player, data);
    }
}
