package arc.check.combat;

import arc.check.CheckSubType;
import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.combat.CombatData;
import arc.utility.entity.Entities;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Checks multiple fight related things
 * <p>
 * Current configuration is relaxed.
 */
public final class KillAura extends PacketCheck {

    /**
     * Max yaw and pitch difference allowed.
     */
    private float maxYawDifference, maxPitchDifference;

    /**
     * The max amount of attacks per second
     */
    private int maxAttacksPerSecond;

    /**
     * The min amount of time between attacks allowed.
     */
    private long minAttackDelta;

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
        addConfigurationValue(CheckSubType.KILL_AURA_DIRECTION, "max-yaw-difference", 75);
        addConfigurationValue(CheckSubType.KILL_AURA_DIRECTION, "max-pitch-difference", 75);

        createSubTypeSections(CheckSubType.KILL_AURA_ATTACK_SPEED);
        addConfigurationValue(CheckSubType.KILL_AURA_ATTACK_SPEED, "max-attacks-per-second", 20);
        addConfigurationValue(CheckSubType.KILL_AURA_ATTACK_SPEED, "min-attack-delta", 35);

        if (enabled()) load();
    }

    /**
     * Invoked when the player attacks
     *
     * @param player the player
     * @param entity the entity
     */
    public boolean check(Player player, Entity entity) {
        if (exempt(player)) return false;

        // grab a new result, our entity and player data.
        final CheckResult result = new CheckResult();
        final CombatData data = CombatData.get(player);

        // check direction
        direction(player, entity, result);
        // check speed
        attackSpeed(player, data, result);

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

        final Location playerLocation = player.getLocation();
        final Location entityLocation = entity.getLocation();

        final float yawToEntity = Entities.getYawToEntity(playerLocation, playerLocation.getYaw(), entityLocation);
        final float pitchToEntity = Entities.getPitchToEntity(playerLocation, playerLocation.getPitch(), entityLocation, player, entity);

        if (yawToEntity >= maxYawDifference) {
            result.setFailed(CheckSubType.KILL_AURA_DIRECTION, "Yaw difference greater than allowed.");
            result.parameter("yawToEntity", yawToEntity);
            result.parameter("maxYawDiff", maxYawDifference);
        }

        if (pitchToEntity >= maxPitchDifference) {
            result.setFailed(CheckSubType.KILL_AURA_DIRECTION, "Pitch difference greater than allowed.");
            result.parameter("pitchToEntity", pitchToEntity);
            result.parameter("maxPitchDiff", maxPitchDifference);
        }
    }

    /**
     * Check for attack speed
     *
     * @param player the player
     * @param data   the player data
     * @param result the result
     */
    private void attackSpeed(Player player, CombatData data, CheckResult result) {
        if (exempt(player, CheckSubType.KILL_AURA_ATTACK_SPEED)) return;

        final int attacks = data.totalAttacks() + 1;
        data.totalAttacks(attacks);

        // check attack deltas
        final long delta = System.currentTimeMillis() - data.lastAttack();
        data.lastAttack(System.currentTimeMillis());
        if (delta <= minAttackDelta) {
            result.setFailed(CheckSubType.KILL_AURA_ATTACK_SPEED, "Attack delta below min");
            result.parameter("delta", delta);
            result.parameter("min", minAttackDelta);
        }

        // check if we have reached 1 or more seconds.
        if (System.currentTimeMillis() - data.lastAttackReset() >= 1000) {
            // reset data
            data.lastAttackReset(System.currentTimeMillis());
            data.totalAttacks(0);

            // check attacks against max.
            final boolean maxAttacks = attacks >= maxAttacksPerSecond;
            if (maxAttacks && !result.failed()) {
                result.setFailed(CheckSubType.KILL_AURA_ATTACK_SPEED, "Too many attacks per second.");
                result.parameter("attacks", attacks);
                result.parameter("max", maxAttacksPerSecond);
                data.cancelAttacks(true);
            }

            // reset our data if we have not reached max.
            if (!maxAttacks) data.cancelAttacks(false);
        }

        // finally, check in general if we should be cancelling.
        if (data.cancelAttacks() && !result.failed()) {
            result.setFailed(CheckSubType.KILL_AURA_ATTACK_SPEED, "Cancelling attacks due to cooldown.");
            result.parameter("timeLeft", (System.currentTimeMillis() - data.lastAttackReset()));
        }
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        final ConfigurationSection directionSection = configuration.subTypeSection(CheckSubType.KILL_AURA_DIRECTION);
        maxYawDifference = (float) directionSection.getDouble("max-yaw-difference");
        maxPitchDifference = (float) directionSection.getDouble("max-pitch-difference");
        final ConfigurationSection speedSection = configuration.subTypeSection(CheckSubType.KILL_AURA_ATTACK_SPEED);
        maxAttacksPerSecond = speedSection.getInt("max-attacks-per-second");
        minAttackDelta = speedSection.getLong("min-attack-delta");
    }
}
