package arc.check.moving;

import arc.check.Check;
import arc.check.result.CheckResult;
import arc.check.timing.CheckTimings;
import arc.check.types.CheckType;
import arc.data.moving.MovingData;
import arc.exemption.type.ExemptionType;
import arc.utility.api.BukkitAccess;
import arc.utility.math.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Checks if the player is taking no fall damage.
 */
public final class NoFall extends Check {

    /**
     * The inaccuracy/tolerance amount to allow when calculating fall distance.
     * If (e-fallDist < 1.0) = safe
     * If (e-fallDist > 1.0) = flag
     * <p>
     * The threshold to when start checking.
     */
    private double expectedFallDistanceTolerance, distanceFallenThreshold;

    /**
     * The times allowed where we can be on ground but the client can't be.
     * 50 is a little generous.
     */
    private int invalidGroundMovesAllowed;

    public NoFall() {
        super(CheckType.NOFALL);
        isEnabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("expected-fall-distance-tolerance", 1.0);
        addConfigurationValue("invalid-ground-moves-allowed", 50);
        addConfigurationValue("distance-fallen-threshold", 2.5);
        if (isEnabled()) load();
    }

    /**
     * Check the player for NoFall
     *
     * @param player the player
     * @param data   the data
     */
    public void check(Player player, MovingData data) {
        if (exempt(player) || exempt(player, ExemptionType.DEATH) || BukkitAccess.isFlyingWithElytra(player)) return;

        if (data.onGround()) {
            checkGround(player, data);
            return;
        }

        if (data.inLiquid()) {
            data.descendingLocation(null);
            data.validFallingLocation(null);
            return;
        }

        // ensure we are descending, not on ground, not climbing, no vehicle and not in liquid.
        if (data.descending() && !data.climbing() && !player.isInsideVehicle()) {
            startTiming(player);

            // check if we are descending from a ladder first.
            final boolean comingFromLadder = data.ladderLocation() != null && data.to().getY() < data.ladderLocation().getY();

            // retrieve our fall distance check location
            final Location descending = data.descendingLocation() == null ? data.from() : data.descendingLocation();
            data.descendingLocation(descending);

            final Location valid = data.validFallingLocation();
            final Location fallDistanceCheck = comingFromLadder ? data.ladderLocation() : valid == null ? descending : valid;

            final double distanceFallen = MathUtil.vertical(fallDistanceCheck, data.from());
            // make sure we have fallen
            if (distanceFallen > distanceFallenThreshold) {
                final CheckResult result = new CheckResult();
                final boolean clientGround = data.clientOnGround();
                final double fallDistance = player.getFallDistance();
                if (data.validFallingLocation() == null) data.validFallingLocation(data.from());

                // patch other types of NoFall with incorrect fall distances.
                final double difference = distanceFallen - fallDistance;
                if (difference > expectedFallDistanceTolerance) {
                    result.setFailed("Client fall distance not expected.")
                            .withParameter("fallDistance", fallDistance)
                            .withParameter("expected", distanceFallen)
                            .withParameter("difference", difference)
                            .withParameter("tolerance", expectedFallDistanceTolerance);
                    data.failedNoFall(checkViolation(player, result));
                } else {
                    // patch basic types of NoFall.
                    if (clientGround || fallDistance == 0.0 && !data.hasSlimeblock()) {
                        result.setFailed("Client on ground or fall distance is 0.0")
                                .withParameter("fallDistance", fallDistance)
                                .withParameter("clientGround", clientGround);
                        data.failedNoFall(checkViolation(player, result));
                    }
                }
            }
            stopTiming(player);
        }
    }

    /**
     * Check ground
     *
     * @param player the player
     * @param data   their data
     */
    private void checkGround(Player player, MovingData data) {
        if (player.isDead() || exempt(player, ExemptionType.DEATH)) return;

        // check if we just checked.
        if (data.validFallingLocation() != null) {
            // we have, check data.
            final int count = data.invalidGround();
            if (count > invalidGroundMovesAllowed) {
                final CheckResult result = new CheckResult();
                result.setFailed("Invalid ground moves more than allowed")
                        .withParameter("count", count)
                        .withParameter("max", invalidGroundMovesAllowed);
                data.failedNoFall(checkViolation(player, result));
            }

            // check if we have failed no-fall.
            // Hopefully, prevent false damage from slime-blocks. even if the player flags.
            if (data.failedNoFall() && !data.hasSlimeblock()) {
                data.failedNoFall(false);
                // cancel the player by setting damage
                final double damage = MathUtil.vertical(data.validFallingLocation(), data.to());
                player.damage(damage);
            } else if (data.failedNoFall()) {
                data.failedNoFall(false);
            }
        }

        // reset location
        data.descendingLocation(null);
        data.validFallingLocation(null);

        // check if client isn't on ground
        if (!data.clientOnGround()) {
            // if so, increment "invalid" ground
            data.invalidGround(data.invalidGround() + 1);
        } else {
            data.invalidGround(0);
        }
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        expectedFallDistanceTolerance = configuration.getDouble("expected-fall-distance-tolerance");
        invalidGroundMovesAllowed = configuration.getInt("invalid-ground-moves-allowed");
        distanceFallenThreshold = configuration.getDouble("distance-fallen-threshold");

        CheckTimings.registerTiming(checkType);
    }
}
