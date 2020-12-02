package arc.check.combat;

import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.combat.CombatData;
import arc.utility.entity.Entities;
import arc.utility.math.MathUtil;
import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.inventivetalent.boundingbox.BoundingBox;

/**
 * Checks multiple fight related things
 * direction-use-bounding-boxes=true slightly more accurate
 * direction-max-angle-difference<=1.25 strict
 * TODO: Avoid calculating angle multiple times
 */
public final class KillAura extends PacketCheck {

    /**
     * If bounding boxes should be used
     */
    private boolean directionUseBoundingBoxes, angleUseBoundingBoxes;

    /**
     * Max angle difference for direction checking
     * Max angle difference for angle checking
     * Max distance allowed to check for angle
     */
    private double directionMaxAngleDifference, angleMaxAngleDifference, angleMaxAngleDifferenceDistance;

    /**
     * The min attack time required between entities
     */
    private long angleMinAttackTime;

    public KillAura() {
        super(CheckType.KILL_AURA);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .write();

        addConfigurationValue("direction-use-bounding-boxes", true);
        addConfigurationValue("direction-max-angle-difference", 1.2);
        addConfigurationValue("angle-use-bounding-boxes", true);
        addConfigurationValue("angle-max-angle-difference", 1.0);
        addConfigurationValue("angle-max-angle-difference-distance", 6.0);
        addConfigurationValue("angle-min-attack-time", 100);
        if (enabled()) load();
    }

    /**
     * Invoked when the player attacks
     *
     * @param player the player
     * @param packet the packet
     */
    public boolean onAttack(Player player, WrapperPlayClientUseEntity packet) {
        if (!enabled() || exempt(player)) return false;
        final CheckResult result = new CheckResult();
        final Entity entity = packet.getTarget(player.getWorld());
        final CombatData data = CombatData.get(player);
        direction(player, entity, result);
        angle(player, entity, result, data);

        return result(player, result).cancel();
    }

    /**
     * Check for direction
     *
     * @param player player
     * @param entity entity
     * @param result result
     */
    private void direction(Player player, Entity entity, CheckResult result) {
        if (exemptSubType(player, "direction")) return;

        // grab angle.
        final double angle = getAngle(directionUseBoundingBoxes, entity, player);
        if (angle > directionMaxAngleDifference) {
            result.information("Angle difference over max, angle=" + angle + ", max=" + directionMaxAngleDifference, "(Direction)");
            result.setFailed();
        }
    }

    /**
     * Check for angle
     * TODO: Doesn't work (!) Needs a REWORK.
     *
     * @param player the player
     * @param entity the entity
     * @param result the result
     */
    private void angle(Player player, Entity entity, CheckResult result, CombatData data) {
        if (exemptSubType(player, "angle")) return;
        // check if we have switched different targets
        if (data.lastAttackedEntity() != null
                && data.lastAttackedEntity().getEntityId() != entity.getEntityId()) {
            final double currentAngle = getAngle(angleUseBoundingBoxes, entity, player);
            final double lastAngle = getAngle(angleUseBoundingBoxes, data.lastAttackedEntity(), player);
            final double difference = Math.abs(currentAngle - lastAngle);
            final double distance = MathUtil.distance(data.lastAttackedEntity().getLocation(), entity.getLocation());
            if (difference > angleMaxAngleDifference && distance <= angleMaxAngleDifferenceDistance
                    && (System.currentTimeMillis() - data.lastAttackedEntityTime() <= angleMinAttackTime)) {
                result.information("dif > angleMax dif=" + difference + " max=" + angleMaxAngleDifference + " dist=" + distance + " max=" + angleMaxAngleDifferenceDistance, "(Angle)");
                result.setFailed();
            }
        }

        data.lastAttackedEntityTime(System.currentTimeMillis());
        data.lastAttackedEntity(entity);
    }

    /**
     * Get an angle
     *
     * @param useBoundingBoxes if bounding boxes should be used
     * @param entity           the entity
     * @param player           the player
     * @return the angle
     */
    private double getAngle(boolean useBoundingBoxes, Entity entity, Player player) {
        final Vector eye = player.getEyeLocation().clone().toVector();
        final BoundingBox bb = directionUseBoundingBoxes ? Entities.getBoundingBox(entity) : null;
        final Vector entityVec = entity.getLocation().clone().toVector();

        if (useBoundingBoxes && bb != null) {
            final double midpointX = (bb.minX + bb.maxX) / 2;
            final double midpointY = (bb.minY + bb.maxY) / 2;
            final double midpointZ = (bb.minZ + bb.maxZ) / 2;
            entityVec.setX(midpointX).setY(midpointY).setZ(midpointZ);
        }

        return entityVec.subtract(eye).angle(player.getLocation().getDirection());
    }

    @Override
    public void reloadConfig() {
        if (enabled()) load();
    }

    @Override
    public void load() {
        directionUseBoundingBoxes = getValueBoolean("direction-use-bounding-boxes");
        directionMaxAngleDifference = getValueDouble("direction-max-angle-difference");
        angleUseBoundingBoxes = getValueBoolean("angle-use-bounding-boxes");
        angleMaxAngleDifference = getValueDouble("angle-max-angle-difference");
        angleMaxAngleDifferenceDistance = getValueDouble("angle-max-angle-difference-distance");
        angleMinAttackTime = getValueLong("angle-min-attack-time");
    }
}
