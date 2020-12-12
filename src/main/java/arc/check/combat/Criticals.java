package arc.check.combat;

import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import arc.utility.MovingUtil;
import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Checks if the player is using criticals while impossible to do so.
 * distance>=0.10: Stricter
 * difference=>0.1: Strict
 * max-similar-vertical-allowed=<5: Strict
 * max-no-vertical-allowed>=5: Relaxed
 */
public final class Criticals extends PacketCheck {

    /**
     * The min distance allowed when checking for a critical hit.
     * The min distance allowed when checking between the current vertical and last.
     */
    private double distance, difference;

    /**
     * The max time allowed before flagging when the vertical doesn't change.
     * The max amount of times the vertical can be 0
     */
    private int maxSimilarVerticalAllowed, maxNoVerticalAllowed;

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

        addConfigurationValue("distance", 0.09);
        addConfigurationValue("difference", 0.05);
        addConfigurationValue("max-similar-vertical-allowed", 5);
        addConfigurationValue("max-no-vertical-allowed", 3);

        if (enabled()) load();
    }

    /**
     * Invoked when we interact with an entity.
     *
     * @param player the player
     * @param packet the packet
     */
    public boolean onAttack(Player player, WrapperPlayClientUseEntity packet) {
        if (!enabled() || exempt(player)) return false;
        if (packet.getType() == EnumWrappers.EntityUseAction.ATTACK) {
            final MovingData data = MovingData.get(player);
            // If it was a possible critical hit and we are on-ground lets check.
            if (isPossibleCriticalHit(player, data) && data.onGround()) {
                final CheckResult result = new CheckResult();
                final double vertical = Math.floor(data.vertical() * 100) / 100;
                final double last = Math.floor(data.lastVerticalDistance() * 100) / 100;
                int similarVerticalAmount = data.similarVerticalAmount();
                int noVerticalAmount = data.noVerticalAmount();
                // Check if the difference between the last vertical and now is less than expected.
                // If so, increment the amount.
                if (Math.abs((vertical - last)) < difference) {
                    similarVerticalAmount++;
                } else {
                    similarVerticalAmount = similarVerticalAmount <= maxSimilarVerticalAllowed ? 0 : similarVerticalAmount - maxSimilarVerticalAllowed;
                }

                // First, check for similar vertical amounts.
                if (similarVerticalAmount >= maxSimilarVerticalAllowed) {
                    result.setFailed("Vertical not changed overtime");
                    result.parameter("vertical", vertical);
                    result.parameter("amount", similarVerticalAmount);
                    result.parameter("max", maxSimilarVerticalAllowed);
                }

                // Second, check the vertical distance
                if (vertical == 0.0 && !result.failed()) {
                    noVerticalAmount++;
                    if (noVerticalAmount >= maxNoVerticalAllowed) {
                        result.setFailed("Max no vertical distance reached.");
                        result.parameter("distance", 0.0);
                        result.parameter("amount", noVerticalAmount);
                        result.parameter("max", maxNoVerticalAllowed);
                    }
                } else if (vertical > 0.0 && !result.failed()) {
                    noVerticalAmount = noVerticalAmount == 0 ? 0 : noVerticalAmount - 1;

                    // Finally, check distance against config.
                    if (vertical <= distance) {
                        result.setFailed("Vertical too small.");
                        result.parameter("distance", vertical);
                        result.parameter("min", distance);
                    }
                }

                // update data
                data.noVerticalAmount(noVerticalAmount);
                data.similarVerticalAmount(similarVerticalAmount);

                // violation
                return checkViolation(player, result).cancel();
            }
        }
        return false;
    }

    /**
     * Check if the hit could be a critical one.
     * TODO: Cooldown checking somehow.
     *
     * @param player the player
     * @param data   data
     * @return {@code true} if so
     */
    private boolean isPossibleCriticalHit(Player player, MovingData data) {
        return !data.clientOnGround()
                && !player.isInsideVehicle()
                && !player.hasPotionEffect(PotionEffectType.BLINDNESS)
                && !MovingUtil.hasClimbable(player.getLocation())
                && !MovingUtil.isInOrOnLiquid(player.getLocation());
    }

    @Override
    public void reloadConfig() {
        if (enabled()) load();
    }

    @Override
    public void load() {
        distance = configuration.getDouble("distance");
        difference = configuration.getDouble("difference");
        maxSimilarVerticalAllowed = configuration.getInt("max-similar-vertical-allowed");
        maxNoVerticalAllowed = configuration.getInt("max-no-vertical-allowed");
    }
}
