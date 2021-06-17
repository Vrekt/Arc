package arc.data.combat;

import arc.data.Data;
import arc.utility.math.MathUtil;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Combat data1
 */
public final class CombatData implements Data {

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
     * The last attack
     */
    private long lastSwingTime, lastAttackReset, lastAttack, lastAttackNoSwing;

    /**
     * Total amount of attacks
     */
    private int totalAttacks;

    /**
     * If attacks should be cancelled.
     */
    private boolean cancelAttacks;

    public long lastSwingTime() {
        return lastSwingTime;
    }

    public void lastSwingTime(long lastSwingTime) {
        this.lastSwingTime = lastSwingTime;
    }

    public int totalAttacks() {
        return totalAttacks;
    }

    public void totalAttacks(int totalAttacks) {
        this.totalAttacks = MathUtil.clampInt(totalAttacks, 0, 100);
    }

    public long lastAttackReset() {
        return lastAttackReset;
    }

    public void lastAttackReset(long lastAttackReset) {
        this.lastAttackReset = lastAttackReset;
    }

    public boolean cancelAttacks() {
        return cancelAttacks;
    }

    public void cancelAttacks(boolean cancelAttacks) {
        this.cancelAttacks = cancelAttacks;
    }

    public long lastAttack() {
        return lastAttack;
    }

    public void lastAttack(long lastAttack) {
        this.lastAttack = lastAttack;
    }

    public long lastAttackNoSwing() {
        return lastAttackNoSwing;
    }

    public void lastAttackNoSwing(long lastAttackNoSwing) {
        this.lastAttackNoSwing = lastAttackNoSwing;
    }
}
