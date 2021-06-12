package arc.check.combat;

import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Checks if the player is attacking from too far away.
 * The default config is optimized to REDUCE false positives.
 * Its not strict what-so-ever.
 */
public final class Reach extends PacketCheck {

    /**
     * Max survival and creative distances.
     * The default eye height;
     */
    private double maxSurvivalDistance, maxCreativeDistance, defaultEyeHeight;

    /**
     * If the Y axis should be ignored.
     * If the eye height should be subtracted.
     */
    private boolean ignoreVerticalAxis, subtractEyeHeight;

    /**
     * If velocities should be subtracted.
     */
    private boolean subtractPlayerVelocity, subtractEntityVelocity;

    public Reach() {
        super(CheckType.REACH);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("max-survival-distance", 4.0);
        addConfigurationValue("max-creative-distance", 6.5);
        addConfigurationValue("ignore-vertical-axis", true);
        addConfigurationValue("subtract-eye-height", true);
        addConfigurationValue("default-eye-height", 1.75);
        addConfigurationValue("subtract-player-velocity", true);
        addConfigurationValue("subtract-entity-velocity", true);
        if (enabled()) load();
    }

    /**
     * Invoked when we interact with an entity.
     *
     * @param player the player
     * @param entity the entity
     */
    public boolean check(Player player, Entity entity) {
        if (exempt(player)) return false;
        final CheckResult result = new CheckResult();

        // retrieve our recent location and the entity location.
        final Vector location = player.getLocation().clone().toVector();
        final Vector entityLocation = entity.getLocation().clone().toVector();

        // if ignore y values, just set them to 0.
        if (ignoreVerticalAxis) {
            location.setY(0);
            entityLocation.setY(0);
        }

        // retrieve the combined subtracted eye height for later.
        final double livingEyeHeight = ((LivingEntity) entity).getEyeHeight();
        final double playerEyeHeight = player.getEyeHeight();
        final double subtractAmount = livingEyeHeight == 0.0 ? defaultEyeHeight : livingEyeHeight == playerEyeHeight ? 0 : livingEyeHeight;
        final double eyeHeight = subtractEyeHeight ? Math.abs(player.getEyeHeight() - subtractAmount) : 0.0;

        // subtract the velocities.
        // TODO Won't be that significant I don't think.
        final Vector velocity = player.getVelocity();
        final Vector entityVelocity = entity.getVelocity();
        if (subtractPlayerVelocity) location.subtract(velocity);
        if (subtractEntityVelocity) entityLocation.subtract(entityVelocity);
        // finally, calculate the distance
        final double distance = location.distance(entityLocation) - eyeHeight;
        // retrieve the allowed amount
        final double allowed = player.getGameMode() == GameMode.CREATIVE ? maxCreativeDistance : maxSurvivalDistance;
        if (distance > allowed) {
            result.setFailed("Distance greater than allowed.");
            result.parameter("distance", distance);
            result.parameter("allowed", allowed);
            result.parameter("ignore-y", ignoreVerticalAxis);
            result.parameter("eyeHeight", eyeHeight);
            result.parameter("vel", velocity);
            result.parameter("entityVel", entityVelocity);
        }

        return checkViolation(player, result).cancel();
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        maxSurvivalDistance = configuration.getDouble("max-survival-distance");
        maxCreativeDistance = configuration.getDouble("max-creative-distance");
        ignoreVerticalAxis = configuration.getBoolean("ignore-vertical-axis");
        subtractEyeHeight = configuration.getBoolean("subtract-eye-height");
        defaultEyeHeight = configuration.getDouble("default-eye-height");
        subtractPlayerVelocity = configuration.getBoolean("subtract-player-velocity");
        subtractEntityVelocity = configuration.getBoolean("subtract-entity-velocity");
    }
}
