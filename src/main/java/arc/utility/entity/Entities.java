package arc.utility.entity;

import arc.Arc;
import arc.utility.math.MathUtil;
import bridge.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Entity util.
 */
public final class Entities {

    /**
     * Get the bounding box of an entity
     *
     * @param entity the entity
     * @return the {@link BoundingBox}
     */
    public static BoundingBox getBoundingBox(Entity entity) {
        return Arc.bridge().entities().getBoundingBox(entity);
    }

    /**
     * Damage the player sync
     *
     * @param player the player
     * @param damage the damage
     */
    public static void damageSync(Player player, double damage) {
        Bukkit.getScheduler().runTask(Arc.plugin(), () -> {
            if (player.isDead()) return;
            player.damage(damage);
        });
    }

    /**
     * Teleport a player sync
     *
     * @param player the player
     * @param to     the location
     * @param cause  the cause
     */
    public static void teleportSync(Player player, Location to, PlayerTeleportEvent.TeleportCause cause) {
        Bukkit.getScheduler().runTask(Arc.plugin(), () -> player.teleport(to, cause));
    }

    /**
     * Get the yaw value required to face the entity.
     *
     * @param player the player
     * @param entity the entity
     * @return the yaw
     */
    public static float getYawToEntity(Location player, float playerYaw, Location entity) {
        final double deltaX = entity.getX() - player.getX();
        final double deltaZ = entity.getZ() - player.getZ();
        double yaw;

        final double atan = (deltaX < 0.0 && deltaZ < 0.0 || deltaX > 0.0 && deltaZ < 0.0) ? Math.atan(deltaZ / deltaX) : -Math.atan(deltaX / deltaZ);
        final double v = Math.toDegrees(atan);

        if (deltaX < 0.0 && deltaZ < 0.0) {
            yaw = 90.0 + v;
        } else if (deltaX > 0.0 && deltaZ < 0.0) {
            yaw = -90.0 + v;
        } else {
            yaw = v;
        }

        return Math.abs(MathUtil.wrapAngle(-(playerYaw - (float) yaw)));
    }

    /**
     * Get the pitch to an entity
     *
     * @param playerLocation the player location
     * @param playerPitch    the players pitch
     * @param entityLocation the entity location
     * @param player         the player
     * @param entity         the entity
     * @return the pitch
     */
    public static float getPitchToEntity(Location playerLocation, float playerPitch, Location entityLocation, Player player, Entity entity) {
        final double deltaX = entityLocation.getX() - playerLocation.getX();
        final double deltaY = entityLocation.getY() - playerLocation.getY();
        final double deltaZ = entityLocation.getZ() - playerLocation.getZ();
        final double horizontal = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        final double pitch = -Math.toDegrees(Math.atan(deltaY / horizontal));
        return Math.abs(MathUtil.wrapAngle(playerPitch - (float) pitch));
    }

}
