package arc.check.combat;

import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Checks if the player is using criticals while impossible to do so.
 */
public final class Criticals extends PacketCheck {

    /**
     * The minimum distance allowed.
     * The similar movement threshold/distance to count
     */
    private double minimumDistanceAllowed, minSimilarMovementDifference;

    /**
     * The maximum amount of no movement allowed.
     */
    private int maxNoMovementAllowed, maxSimilarMovementAllowed;

    public Criticals() {
        super(CheckType.CRITICALS);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("minimum-distance-allowed", 0.099);
        addConfigurationValue("max-no-movement-allowed", 3);
        addConfigurationValue("max-similar-movement-allowed", 3);
        addConfigurationValue("min-similar-movement-difference", 0.05);

        if (enabled()) load();
    }

    /**
     * Check the player
     *
     * @param player the player
     * @return if the check should cancel.
     */
    public boolean check(Player player) {
        if (exempt(player)) return false;
        final MovingData data = MovingData.get(player);
        final CheckResult result = new CheckResult();

        // first, check if we maybe have a hit.
        // won't always be accurate of-course.
        final boolean isPossibleCritical = isPossibleCritical(player, data);

        // check
        if (isPossibleCritical) {
            // floor our vertical values to cap them
            final double vertical = Math.floor(data.vertical() * 100) / 100;
            final double last = Math.floor(data.lastVertical() * 100) / 100;
            final double difference = Math.floor(Math.abs(vertical - last) * 100) / 100;

            // check if no movement
            if (difference == 0.0 || vertical == 0.0) {
                final int amount = data.noMovementAmount() + 1;
                data.noMovementAmount(amount);

                if (amount >= maxNoMovementAllowed) {
                    result.setFailed("max no movements reached")
                            .withParameter("amount", amount)
                            .withParameter("max", maxNoMovementAllowed);
                }
            } else {
                data.noMovementAmount(data.noMovementAmount() - 1);
            }

            // check basic distance
            if (!result.failed() && (vertical == last) && vertical <= minimumDistanceAllowed) {
                result.setFailed("Vertical less than allowed")
                        .withParameter("vertical", vertical)
                        .withParameter("min", minimumDistanceAllowed);
            }

            // check similar movements based on difference.
            if (!result.failed() && difference >= 0.0 && difference <= minSimilarMovementDifference) {
                final int amount = data.similarMovementAmount() + 1;
                data.similarMovementAmount(amount);

                if (amount >= maxSimilarMovementAllowed) {
                    result.setFailed("max similar movement amount reached")
                            .withParameter("amount", amount)
                            .withParameter("max", maxSimilarMovementAllowed)
                            .withParameter("diff", difference);
                }
            } else {
                data.similarMovementAmount(data.similarMovementAmount() - 1);
            }
        }

        return checkViolation(player, result);
    }

    /**
     * Check if the hit could be a critical.
     *
     * @param player the player
     * @param data   their data
     * @return {@code true} if so
     */
    private boolean isPossibleCritical(Player player, MovingData data) {
        return !data.clientPositionOnGround()
                && !player.isInsideVehicle()
                && !player.hasPotionEffect(PotionEffectType.BLINDNESS)
                && !data.inLiquid()
                && !(data.climbing() || data.hasClimbable());
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        minimumDistanceAllowed = configuration.getDouble("minimum-distance-allowed");
        maxNoMovementAllowed = configuration.getInt("max-no-movement-allowed");
        maxSimilarMovementAllowed = configuration.getInt("max-similar-movement-allowed");
        minSimilarMovementDifference = configuration.getDouble("min-similar-movement-difference");
    }
}
