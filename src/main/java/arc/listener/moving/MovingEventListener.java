package arc.listener.moving;

import arc.Arc;
import arc.check.moving.Flight;
import arc.check.moving.Jesus;
import arc.check.moving.NoFall;
import arc.check.moving.Speed;
import arc.check.types.CheckType;
import arc.data.moving.MovingData;
import arc.utility.MovingAccess;
import arc.world.WorldManager;
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
        flight = Arc.getInstance().getCheckManager().getCheck(CheckType.FLIGHT);
        jesus = Arc.getInstance().getCheckManager().getCheck(CheckType.JESUS);
        noFall = Arc.getInstance().getCheckManager().getCheck(CheckType.NOFALL);
        speed = Arc.getInstance().getCheckManager().getCheck(CheckType.SPEED);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onMoveLowest(PlayerMoveEvent event) {
        if (event.getFrom().getWorld() != event.getTo().getWorld()) return;

        Player player = event.getPlayer();
        if (!WorldManager.isEnabledInWorld(player)) return;

        MovingData data = MovingData.get(player);

        Location from = event.getFrom();
        Location to = event.getTo();

        // check if we have moved.
        boolean hasMoved = from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ();
        if (hasMoved) {
            MovingAccess.updatePlayerMovingData(data, from, to);
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
        if (flight.isEnabled()) {
            flight.check(player, data);
        }

        if (noFall.isEnabled()) {
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
        if (jesus.isEnabled()) jesus.check(player, data);
    }
}
