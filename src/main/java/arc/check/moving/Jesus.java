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

import java.util.ArrayList;
import java.util.List;

/**
 * Checks if the player is walking on water.
 * TODO: Improve this overtime.
 * if do-diff-checking=true will make check possibly detect other types of Jesus but could cause false positives
 * not tested (!)
 */
public final class Jesus extends Check {

    /**
     * If differences should be monitored.
     */
    private boolean doDiffChecking;

    /**
     * Max similar vertical allowed
     * Max setback distance allowed
     */
    private int minInWaterTime, maxInWaterTime, maxSetbackDistance;

    /**
     * Min avg value
     */
    private double inWaterDiffMinAvg;

    public Jesus() {
        super(CheckType.JESUS);
        enabled(true).
                cancel(true).
                cancelLevel(0).
                notify(true).
                notifyEvery(1).
                ban(false).
                kick(false).
                write();

        addConfigurationValue("do-diff-checking", false);
        addConfigurationValue("min-in-water-time", 5);
        addConfigurationValue("max-in-water-time", 100);
        addConfigurationValue("in-water-diff-min-avg", 0.01);
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

        boolean inWater = MovingUtil.isInOrOnLiquid(data.to());
        // reset data if not in water.
        if (!inWater) {
            data.inWaterTime(0);
            data.averageInWaterDifferences(null);
        }

        if (!data.onGround() && inWater && !player.isInsideVehicle()) {
            final boolean blockFaceDown = data.to().getBlock().getRelative(BlockFace.DOWN).isLiquid();
            final boolean blockFaceDown2 = data.to().getBlock().getRelative(0, -2, 0).isLiquid();
            final CheckResult result = new CheckResult();
            final int inWaterTime = data.inWaterTime() + 1;

            List<Double> averages = data.averageInWaterDifferences();
            if (doDiffChecking) {
                // get averages, purge if entries are > 50
                if (averages == null || averages.size() > 50) averages = new ArrayList<>();
            }

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
/*                // the client isn't on ground, check further
                if (doDiffChecking && data.descending()) {
                    // calculate difference and add it to a list of averages.
                    final double vertical = data.vertical();
                    final double last = data.lastVerticalDistance();
                    final double diff = vertical - last;
                    averages.add(diff);

                    // make sure we have a decent sample size.
                    if (averages.size() > 5 && inWaterTime > minInWaterTime && inWaterTime < maxInWaterTime) {
                        // grab the avg
                        final double average = averages.stream().mapToDouble(a -> a).average().orElse(-1);
                        if (average != 1 && average < inWaterDiffMinAvg) {
                            result.setFailed("inWaterDiffMinAvg too small, v=" + average + " min=" + inWaterDiffMinAvg);
                        }
                    }

                    data.averageInWaterDifferences(averages);
                    data.inWaterTime(inWaterTime);
                }*/

                final boolean blockFaceDown2Modifier = data.to().clone().add(0, -1.5, 0).getBlock().isLiquid();
                if (data.vertical() == 0.0
                        && (blockFaceDown || blockFaceDown2Modifier)) {
                    // no vertical but on layers of water.
                    result.setFailed("Client has no vertical while on layers of water.");
                }
            }

            final ViolationResult violation = result(player, result);
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
        doDiffChecking = getValueBoolean("do-diff-checking");
        minInWaterTime = getValueInt("min-in-water-time");
        maxInWaterTime = getValueInt("max-in-water-time");
        inWaterDiffMinAvg = getValueDouble("in-water-diff-min-avg");
        maxSetbackDistance = getValueInt("max-setback-distance");
    }
}
