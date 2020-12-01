package arc.data.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Combat data1
 */
public final class CombatData {

    /**
     * The register
     */
    private static final Map<UUID, CombatData> REGISTER = new ConcurrentHashMap<>();

    /**
     * Get data
     *
     * @param player the player
     * @return the data
     */
    public static CombatData get(Player player) {
        return REGISTER.computeIfAbsent(player.getUniqueId(), uuid -> new CombatData());
    }

    /**
     * Remove data
     *
     * @param player the player
     */
    public static void remove(Player player) {
        REGISTER.remove(player.getUniqueId());
    }

    /**
     * Last time the player swung their arm
     * The last attack time
     */
    private long lastSwingTime, lastAttackTime;

    /**
     * last attacked entity
     */
    private Entity lastAttackedEntity;

    /**
     * When we last attacked this entity
     */
    private long lastAttackedEntityTime;

    /**
     * The last angle.
     */
    private double lastAngle;

    public long lastSwingTime() {
        return lastSwingTime;
    }

    public void lastSwingTime(long lastSwingTime) {
        this.lastSwingTime = lastSwingTime;
    }

    public long lastAttackTime() {
        return lastAttackTime;
    }

    public void lastAttackTime(long lastAttackTime) {
        this.lastAttackTime = lastAttackTime;
    }

    public double lastAngle() {
        return lastAngle;
    }

    public void lastAngle(double lastAngle) {
        this.lastAngle = lastAngle;
    }

    public Entity lastAttackedEntity() {
        return lastAttackedEntity;
    }

    public void lastAttackedEntity(Entity lastAttackedEntity) {
        this.lastAttackedEntity = lastAttackedEntity;
    }

    public long lastAttackedEntityTime() {
        return lastAttackedEntityTime;
    }

    public void lastAttackedEntityTime(long lastAttackedEntityTime) {
        this.lastAttackedEntityTime = lastAttackedEntityTime;
    }
}
