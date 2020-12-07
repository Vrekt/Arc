package arc.check.moving;

import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import arc.utility.MovingUtil;
import arc.utility.math.MathUtil;
import arc.violation.result.ViolationResult;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Checks if the player is walking on water.
 * TODO: Needs more work.
 */
public final class Jesus extends Check {


    /**
     * Max similar vertical allowed
     * Max setback distance allowed
     */
    private int maxSetbackDistance;


    public Jesus() {
        super(CheckType.JESUS);
        enabled(true).
                cancel(true).
                cancelLevel(0).
                notify(true).
                notifyEvery(1).
                ban(false).
                kick(false).
                build();

        addConfigurationValue("max-setback-distance", 4);
        if (enabled()) load();
    }

    /**
     * Check this player
     *
     * @param player the player
     * @param data   their data
     * @param event  the event
     */
    public void check(Player player, MovingData data, PlayerMoveEvent event) {
        if (exempt(player) || !enabled()) return;

        start(player);
        boolean inWater = MovingUtil.isInOrOnLiquid(data.to());
        // reset data if not in water.
        if (!inWater) {
            data.inWaterTime(0);
            data.averageInWaterDifferences(null);
            stop(player);
        }

        if (!data.onGround() && inWater && !player.isInsideVehicle()) {
            final boolean blockFaceDown = data.to().getBlock().getRelative(BlockFace.DOWN).isLiquid();
            final boolean blockFaceDown2 = data.to().getBlock().getRelative(0, -2, 0).isLiquid();
            final CheckResult result = new CheckResult();

            // we are on ground while on a liquid, check
            if (data.clientOnGround()) {
                if (data.vertical() == 0.0) {
                    // not possible, i don't think
                    result.setFailed("Client on ground and vertical 0.0 while in or on liquid.");
                }

                // on ground and at-least a decent amount of liquid below us.
                if (blockFaceDown || blockFaceDown2) {
                    result.setFailed("Client on ground while on layers of water.");
                }
            } else {

                final boolean blockFaceDown2Modifier = data.to().clone().add(0, -1.5, 0).getBlock().isLiquid();
                if (data.vertical() == 0.0
                        && (blockFaceDown || blockFaceDown2Modifier)) {
                    // no vertical but on layers of water.
                    result.setFailed("Client has no vertical while on layers of water.");
                }
            }

            stop(player);
            final ViolationResult violation = checkViolation(player, result);
            if (violation.cancel()) {
                // set the player back in the event.
                if (data.hasGround() && MathUtil.distance(data.ground(), data.to())
                        <= maxSetbackDistance) {
                    event.setTo(data.ground());
                } else {
                    event.setTo(event.getFrom().add(0, -0.01, 0));
                }
            }
        }
    }

    @Override
    public void reloadConfig() {
        if (enabled()) load();
    }

    @Override
    public void load() {
        useTimings();
        maxSetbackDistance = configuration.getInt("max-setback-distance");
    }
}
