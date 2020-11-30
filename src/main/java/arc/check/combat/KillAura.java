package arc.check.combat;

import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.utility.AxisAlignedBB;
import arc.utility.Entities;
import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
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
    private boolean useBoundingBoxes;

    /**
     * Max angle difference for direction checking
     */
    private double maxAngleDifference;

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
        direction(player, entity, result);

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

        // grab player direction and bounding box
        final Vector eye = player.getEyeLocation().clone().toVector();
        final AxisAlignedBB bb = useBoundingBoxes ? Entities.getBoundingBox(entity) : null;
        final Vector entityVec = entity.getLocation().clone().toVector();

        if (useBoundingBoxes && bb != null) {
            final double midpointX = (bb.minX() + bb.maxX()) / 2;
            final double midpointY = (bb.minY() + bb.maxY()) / 2;
            final double midpointZ = (bb.minZ() + bb.maxZ()) / 2;
            entityVec.setX(midpointX).setY(midpointY).setZ(midpointZ);
        }

        final double angle = entityVec.subtract(eye).angle(player.getLocation().getDirection());
        if (angle > maxAngleDifference) {
            result.information("Angle difference over max, angle=" + angle + ", max=" + maxAngleDifference, "(Direction)");
            result.setFailed();
        }
    }

    @Override
    public void reloadConfig() {
        if (enabled()) load();
    }

    @Override
    public void load() {
        useBoundingBoxes = getValueBoolean("direction-use-bounding-boxes");
        maxAngleDifference = getValueDouble("direction-max-angle-difference");
    }
}
