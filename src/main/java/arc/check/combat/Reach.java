package arc.check.combat;

import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.utility.entity.Entities;
import arc.utility.math.MathUtil;
import arc.violation.result.ViolationResult;
import bridge.BoundingBox;
import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Checks if the player is attacking from too far away.
 * TODO: Account for lag
 */
public final class Reach extends PacketCheck {

    /**
     * Creative reach distance.
     */
    private static final double CREATIVE_REACH_DISTANCE = 6D;

    /**
     * The max distance allowed.
     * The max velocity to subtract from the distance
     * The min velocity required to be subtracted
     */
    private double maxDistance, maxVelocityLength, minVelocityLength, nonLivingEyeHeight;

    /**
     * If the Y axis should be ignored.
     * If velocity should be subtracted
     * If eye heights should be subtracted
     * If bounding boxes should be used.
     */
    private boolean ignoreYAxis, subtractVelocity, subtractEye, useBoundingBoxes;

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

        addConfigurationValue("max-distance", 3.88);
        addConfigurationValue("max-velocity-length", 1.0);
        addConfigurationValue("min-velocity-length", 0.2);
        addConfigurationValue("ignore-y-value", true);
        addConfigurationValue("subtract-velocity", true);
        addConfigurationValue("subtract-eye", true);
        addConfigurationValue("non-living-eye-height", 1.75);
        addConfigurationValue("use-bounding-boxes", true);
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
            // we attacked, get the entity and distance check.
            final Entity entity = packet.getTarget(player.getWorld());
            if (!entity.isDead()) {
                start(player);

                // get our entities Y locations
                final double py = ignoreYAxis ? 1.0 : player.getLocation().getY();
                final double dy = ignoreYAxis ? 1.0 : entity.getLocation().getY();
                // subtract our eye height from the entities.
                final double ey = subtractEye ? player.getEyeHeight() - ((entity instanceof LivingEntity) ? ((LivingEntity) entity).getEyeHeight() : nonLivingEyeHeight) : 0.0;

                // retrieve the clamped entity velocity
                final double entityVel = entity.getVelocity().length();
                final double velocity = subtractVelocity ? entityVel > minVelocityLength ? MathUtil.clamp(entityVel, minVelocityLength, maxVelocityLength) : 0.0 : 0.0;

                // retrieve bounding box
                final BoundingBox entityBB = useBoundingBoxes ? Entities.getBoundingBox(entity) : null;
                // retrieve vectors and set bounding box values
                final Vector playerVec = player.getLocation().clone().toVector();
                final Vector entityVec = entity.getLocation().clone().toVector();
                if (useBoundingBoxes && entityBB != null) {
                    entityVec.setX(entityBB.minX());
                    entityVec.setZ(entityBB.minZ());
                }

                playerVec.setY(py);
                entityVec.setY(dy);

                // calculate distance and subtract velocity/eye if applicable
                double distance = playerVec.subtract(entityVec).length();
                if (subtractVelocity) distance -= velocity;
                if (subtractEye) distance -= ey;
                stop(player);
                // check if we are in creative
                // , if so check against Magic value
                if (player.getGameMode() == GameMode.CREATIVE && distance > CREATIVE_REACH_DISTANCE) {
                    final ViolationResult violation = checkViolation(player, new CheckResult(CheckResult.Result.FAILED, "Creative reach distance >6"));
                    return violation.cancel();
                } else {
                    // otherwise check
                    if (distance > maxDistance) {
                        // too far away, flag.
                        final ViolationResult violation = checkViolation(player, new CheckResult(CheckResult.Result.FAILED, "Attacked from too far away, len=" + distance + " max=" + maxDistance));
                        return violation.cancel();
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void reloadConfig() {
        if (enabled()) load();
    }

    @Override
    public void load() {
        useTimings();
        maxDistance = configuration.getDouble("max-distance");
        maxVelocityLength = configuration.getDouble("max-velocity-length");
        minVelocityLength = configuration.getDouble("min-velocity-length");
        ignoreYAxis = configuration.getBoolean("ignore-y-value");
        subtractVelocity = configuration.getBoolean("subtract-velocity");
        subtractEye = configuration.getBoolean("subtract-eye");
        nonLivingEyeHeight = configuration.getDouble("non-living-eye-height");
        useBoundingBoxes = configuration.getBoolean("use-bounding-boxes");
    }
}
