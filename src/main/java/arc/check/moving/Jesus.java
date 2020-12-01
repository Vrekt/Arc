package arc.check.moving;

import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import arc.utility.math.MathUtil;
import arc.utility.MovingUtil;
import arc.violation.result.ViolationResult;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Checks if the player is walking on water.
 * TODO: Lag checking
 */
public final class Jesus extends Check {

    /**
     * Max similar vertical allowed
     * Max setback distance allowed
     */
    private int maxSimilarVerticalAllowed, maxSetbackDistance;

    /**
     * Difference allowed
     */
    private double difference;

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

        addConfigurationValue("max-similar-vertical-allowed", 5);
        addConfigurationValue("difference", 0.03);
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

        if (!data.onGround() && MovingUtil.isInOrOnLiquid(data.to())) {
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
                final double vertical = data.vertical();
                final double last = data.lastVerticalDistance();
                int similarVerticalAmount = data.similarVerticalAmountJesus();

                // check when we are descending
                if (data.descending()) {
                    if (Math.abs((vertical - last)) < difference) {
                        similarVerticalAmount++;
                    } else {
                        similarVerticalAmount = similarVerticalAmount <= maxSimilarVerticalAllowed ? 0 : similarVerticalAmount - maxSimilarVerticalAllowed;
                    }
                }

                if (similarVerticalAmount >= maxSimilarVerticalAllowed) {
                    result.setFailed("Client vertical too consistent diff=" + difference + " s=" + similarVerticalAmount + " m=" + maxSimilarVerticalAllowed);
                }

                // the client isn't on ground, check further
                final boolean blockFaceDown2Modifier = data.to().clone().add(0, -1.5, 0).getBlock().isLiquid();
                if (data.vertical() == 0.0
                        && (blockFaceDown || blockFaceDown2Modifier)) {
                    // no vertical but on layers of water.
                    result.setFailed("Client has no vertical while on layers of water.");
                }

                data.similarVerticalAmountJesus(similarVerticalAmount);
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
        maxSimilarVerticalAllowed = getValueInt("max-similar-vertical-allowed");
        difference = getValueDouble("difference");
        maxSetbackDistance = getValueInt("max-setback-distance");
    }
}
