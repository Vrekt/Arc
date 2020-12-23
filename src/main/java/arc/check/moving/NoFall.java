package arc.check.moving;

import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import arc.exemption.type.ExemptionType;
import arc.utility.entity.Entities;
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
     */
    private double expectedFallDistanceTolerance;

    /**
     * The times allowed where we can be on ground but the client can't be.
     * 50 is a little generous.
     */
    private int invalidGroundMovesAllowed;

    public NoFall() {
        super(CheckType.NOFALL);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("expected-fall-distance-tolerance", 1.0);
        addConfigurationValue("invalid-ground-moves-allowed", 50);
        if (enabled()) load();
    }

    /**
     * Check the player for NoFall
     *
     * @param player the player
     * @param data   the data
     */
    public void check(Player player, MovingData data) {
        if (exempt(player) || exempt(player, ExemptionType.DEATH)) return;
        if (data.onGround()) checkGround(player, data);
        if (data.inLiquid()) {
            data.descendingLocation(null);
            data.validFallingLocation(null);
        }

        // ensure we are descending, not on ground, not climbing, no vehicle and not in liquid.
        if (data.descending() && !data.onGround() && !data.climbing() && !player.isInsideVehicle() && !data.inLiquid()) {
            // retrieve our fall distance check location
            final Location descending = data.descendingLocation() == null ? data.from() : data.descendingLocation();
            data.descendingLocation(descending);

            final Location valid = data.validFallingLocation();
            final Location fallDistanceCheck = valid == null ? descending : valid;

            final double distanceFallen = MathUtil.vertical(fallDistanceCheck, data.from());
            // make sure we have fallen
            if (distanceFallen > 2) {
                final CheckResult result = new CheckResult();
                final boolean clientGround = data.clientOnGround();
                final double fallDistance = player.getFallDistance();
                if (data.validFallingLocation() == null) data.validFallingLocation(data.from());

                // patch other types of NoFall with incorrect fall distances.
                final double difference = distanceFallen - fallDistance;
                if (difference > expectedFallDistanceTolerance) {
                    result.setFailed("Client fall distance not expected.");
                    result.parameter("fallDistance", fallDistance);
                    result.parameter("expected", distanceFallen);
                    result.parameter("difference", difference);
                    result.parameter("tolerance", expectedFallDistanceTolerance);
                    data.failedNoFall(checkViolation(player, result).cancel());
                } else {
                    // patch basic types of NoFall.
                    if (clientGround || fallDistance == 0.0) {
                        result.setFailed("Client on ground or fall distance is 0.0");
                        result.parameter("fallDistance", fallDistance);
                        result.parameter("clientGround", clientGround);
                        data.failedNoFall(checkViolation(player, result).cancel());
                    }
                }
            }
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
                result.setFailed("Invalid ground moves more than allowed");
                result.parameter("count", count);
                result.parameter("max", invalidGroundMovesAllowed);
                data.failedNoFall(checkViolation(player, result).cancel());
            }

            // check if we have failed no-fall.
            if (data.failedNoFall()) {
                data.failedNoFall(false);
                // cancel the player by setting damage
                final double damage = MathUtil.vertical(data.validFallingLocation(), data.to());
                Entities.damageSync(player, damage);
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
    }
}
