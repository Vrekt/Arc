package arc.check.moving;

import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import arc.utility.MovingUtil;
import arc.utility.math.MathUtil;
import arc.violation.result.ViolationResult;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Checks if the player is moving too fast.
 */
public final class Speed extends Check {

    /**
     * If large distances should be cancelled.
     */
    private boolean cancelLargeMovements;

    /**
     * Large movements distance to cancel
     * The max setback distance
     */
    private double largeMovementsDistance, maxSetbackDistance;

    /**
     * The default move speed sprinting
     * The default move speed walking
     * The default move speed sneaking
     */
    private double baseMoveSpeedSprinting, baseMoveSpeedWalking, baseMoveSpeedSneaking;

    /**
     * Teleport cooldown.
     */
    private long teleportCooldown;

    public Speed() {
        super(CheckType.SPEED);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("cancel-large-movements", true);
        addConfigurationValue("large-movements-distance", 2);
        addConfigurationValue("teleport-cooldown-ms", 500);
        addConfigurationValue("base-move-speed-sprinting", 0.2873);
        addConfigurationValue("base-move-speed-walking", 0.2166);
        addConfigurationValue("base-move-speed-sneaking", 0.0666);
        addConfigurationValue("max-setback-distance", 10);
        if (enabled()) load();
    }

    /**
     * Check the player
     *
     * @param player the player
     * @param data   the data
     * @param event  the event
     */
    public void check(Player player, MovingData data, PlayerMoveEvent event) {
        if (!enabled() || exempt(player)) return;

        final CheckResult result = new CheckResult();

        final Location from = data.from();
        final Location to = data.to();

        final double thisMove = MathUtil.horizontal(from, to);
        final double baseSpeed = getBaseSpeed(data, player);

        checkLargeDistances(thisMove, data, result);
        checkActions(data, result, thisMove, baseSpeed);

        final ViolationResult violation = checkViolation(player, result);
        if (violation.cancel()) {
            final Location possibleSetback = data.speedSetback();
            if (possibleSetback != null) {
                final double distance = MathUtil.distance(data.to(), possibleSetback);
                if (distance < maxSetbackDistance) {
                    event.setTo(possibleSetback);
                } else {
                    event.setTo(event.getFrom());
                }
            } else {
                event.setTo(event.getFrom());
            }
        }
    }

    /**
     * Check large distances
     *
     * @param distanceMoved the distance moved
     */
    private void checkLargeDistances(double distanceMoved, MovingData data, CheckResult result) {
        if (!cancelLargeMovements) return;

        final long delta = System.currentTimeMillis() - data.lastTeleport();
        if (delta >= teleportCooldown && distanceMoved > largeMovementsDistance) {
            result.setFailed("distanceMoved > max");
            result.parameter("distanceMoved", distanceMoved);
            result.parameter("max", largeMovementsDistance);
        }
    }

    /**
     * Check actions like sneaking/blocking
     *
     * @param data      the data
     * @param result    the result
     * @param thisMove  the move
     * @param baseSpeed the base speed
     *                  TODO
     */
    private void checkActions(MovingData data, CheckResult result, double thisMove, double baseSpeed) {
    }

    /**
     * Get the base player speed
     *
     * @param player the player
     * @return their speed
     */
    private double getBaseSpeed(MovingData data, Player player) {
        double speed = data.sneaking() ? baseMoveSpeedSneaking : data.sprinting() ? baseMoveSpeedSprinting : baseMoveSpeedWalking;

        for (PotionEffect ef : player.getActivePotionEffects()) {
            if (ef.getType().equals(PotionEffectType.SPEED)) {
                final double modifier = speed * (0.2 * (ef.getAmplifier() + 1));
                speed += modifier;
            }
        }

        return speed;
    }

    @Override
    public void reloadConfig() {
        unload();
        if (enabled()) load();
    }

    @Override
    public void load() {
        cancelLargeMovements = configuration.getBoolean("cancel-large-movements");
        largeMovementsDistance = configuration.getDouble("large-movements-distance");
        teleportCooldown = configuration.getLong("teleport-cooldown-ms");
        baseMoveSpeedSprinting = configuration.getDouble("base-move-speed-sprinting");
        baseMoveSpeedWalking = configuration.getDouble("base-move-speed-walking");
        baseMoveSpeedSneaking = configuration.getDouble("base-move-speed-sneaking");
        maxSetbackDistance = configuration.getDouble("max-setback-distance");
    }
}
