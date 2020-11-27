package arc.check.combat;

import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.violation.result.ViolationResult;
import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Checks if the player is attacking from too far away.
 * TODO: Account for lag
 */
public final class Reach extends PacketCheck {

    /**
     * The max distance allowed.
     */
    private double maxDistance;

    public Reach() {
        super(CheckType.REACH);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .write();

        addConfigurationValue("max-distance", 3.88);
        if (enabled()) load();
    }

    /**
     * Invoked when we interact with an entity.
     *
     * @param player the player
     * @param packet the packet
     */
    public boolean onAttack(Player player, WrapperPlayClientUseEntity packet) {
        if (packet.getType() == EnumWrappers.EntityUseAction.ATTACK) {
            // we attacked, get the entity and distance check.
            final Entity entity = packet.getTarget(player.getWorld());
            if (!entity.isDead()) {
                final double py = player.getLocation().getY() + player.getEyeHeight();
                final double dy = entity.getLocation().getY() + ((entity instanceof LivingEntity) ? ((LivingEntity) entity).getEyeHeight() : 1.0);

                // set the respective Y values and then subtract.
                final double length = entity.getLocation().toVector().setY(dy).subtract(player.getLocation().toVector().setY(py)).length();
                if (length > maxDistance) {
                    // too far away, flag.
                    final ViolationResult violation = result(player, new CheckResult(CheckResult.Result.FAILED, "Attacked from too far away, len=" + length + " max=" + maxDistance));
                    return violation.cancel();
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
        maxDistance = getValueDouble("max-distance");
    }
}
