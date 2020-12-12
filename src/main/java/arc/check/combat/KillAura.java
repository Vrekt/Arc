package arc.check.combat;

import arc.Arc;
import arc.check.CheckSubType;
import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.combat.CombatData;
import arc.utility.entity.Entities;
import bridge.BoundingBox;
import bridge.Version;
import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Checks multiple fight related things
 * direction-use-bounding-boxes=true slightly more accurate
 * direction-max-angle-difference<=1.25 strict
 */
public final class KillAura extends PacketCheck {

    /**
     * If bounding boxes should be used
     */
    private boolean directionUseBoundingBoxes;

    /**
     * Max angle difference for direction checking
     */
    private double directionMaxAngleDifference;

    public KillAura() {
        super(CheckType.KILL_AURA);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        createSubTypeSections(CheckSubType.KILL_AURA_DIRECTION);
        addConfigurationValue(CheckSubType.KILL_AURA_DIRECTION, "use-bounding-boxes", true);
        addConfigurationValue(CheckSubType.KILL_AURA_DIRECTION, "max-angle-difference", 1.2);

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

        // grab a new result, our entity and player data.
        final CheckResult result = new CheckResult();
        final Entity entity = packet.getTarget(player.getWorld());
        final CombatData data = CombatData.get(player);
        // check direction
        direction(player, entity, result);

        // return result.
        return checkViolation(player, result).cancel();
    }

    /**
     * Check for direction
     *
     * @param player player
     * @param entity entity
     * @param result result
     */
    private void direction(Player player, Entity entity, CheckResult result) {
        if (exempt(player, CheckSubType.KILL_AURA_DIRECTION)) return;

        // grab angle.
        final double angle = getAngle(directionUseBoundingBoxes, entity, player);
        if (angle > directionMaxAngleDifference) {
            result.setFailed(CheckSubType.KILL_AURA_DIRECTION, "Angle difference over max");
            result.parameter("angle", angle);
            result.parameter("max", directionMaxAngleDifference);
            result.parameter("bb", directionUseBoundingBoxes);
        }
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
        final boolean compatibility = Arc.version().isOlderThan(Version.VERSION_1_16);
        final Vector eye = player.getEyeLocation().clone().toVector();
        final BoundingBox bb = directionUseBoundingBoxes ? Entities.getBoundingBox(entity) : null;
        final Vector entityVec = entity.getLocation().clone().toVector();

        if (useBoundingBoxes && compatibility && bb != null) {
            final double midpointX = (bb.minX() + bb.maxX()) / 2;
            final double midpointY = (bb.minY() + bb.maxY()) / 2;
            final double midpointZ = (bb.minZ() + bb.maxZ()) / 2;
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
        final ConfigurationSection directionSection = configuration.subTypeSection(CheckSubType.KILL_AURA_DIRECTION);
        directionUseBoundingBoxes = directionSection.getBoolean("use-bounding-boxes");
        directionMaxAngleDifference = directionSection.getDouble("max-angle-difference");
    }
}
