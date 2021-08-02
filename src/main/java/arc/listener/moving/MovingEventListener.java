package arc.listener.moving;

import arc.Arc;
import arc.check.moving.Flight;
import arc.check.moving.Jesus;
import arc.check.moving.NoFall;
import arc.check.moving.Speed;
import arc.check.types.CheckType;
import arc.data.moving.MovingData;
import arc.utility.MovingAccess;
import arc.utility.api.BukkitAccess;
import arc.utility.material.MaterialAccess;
import arc.world.WorldManager;
import bridge.Version;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

    /**
     * Legacy flag.
     */
    private final boolean legacy;

    /**
     * Firework rocket
     */
    private final Material rocket;

    public MovingEventListener() {
        flight = Arc.getInstance().getCheckManager().getCheck(CheckType.FLIGHT);
        jesus = Arc.getInstance().getCheckManager().getCheck(CheckType.JESUS);
        noFall = Arc.getInstance().getCheckManager().getCheck(CheckType.NOFALL);
        speed = Arc.getInstance().getCheckManager().getCheck(CheckType.SPEED);

        legacy = Arc.getMCVersion() == Version.VERSION_1_8;

        rocket = Arc.getMCVersion() == Version.VERSION_1_8
                || Arc.getMCVersion() == Version.VERSION_1_12 ? MaterialAccess.getMaterial("FIREWORK")
                : MaterialAccess.getMaterial("FIREWORK_ROCKET");
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
     * Listens for elytra related actions.
     * <p>
     * Do not set {@code ignoreCancelled} to {@code true}
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    private void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR
                && !legacy) {
            final Player player = event.getPlayer();

            // player is using rockets, and has elytra.
            if (BukkitAccess.hasItemInHand(player, rocket)
                    && player.getInventory().getChestplate() != null
                    && player.getInventory().getChestplate().getType() == Material.ELYTRA) {
                final MovingData data = MovingData.get(player);

                data.setLastRocketUse(System.currentTimeMillis());

                // update checking buffer.
                if (!data.descending()) {
                    data.setRocketDistanceBuffer(data.getRocketDistanceBuffer() + 64);
                }
            }
        }
    }

    /**
     * Tests if the player is pushed by a piston
     *
     * @param event the event
     */
    @EventHandler(ignoreCancelled = true)
    private void onPistonPush(BlockPistonExtendEvent event) {
        if (!WorldManager.isEnabledWorld(event.getBlock().getWorld())) return;
        if (event.getBlocks().size() == 0) return;

        // retrieve the LAST block pushed by the piston.
        // this is for location checking in flight.
        final Block last = event.getBlocks().get(event.getBlocks().size() - 1);
        flight.recordPistonEvent(last);
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
