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
            result.setFailed("Yaw difference greater than allowed.");
            result.parameter("yawToEntity", yawToEntity);
            result.parameter("maxYawDiff", maxYawDifference);
        }

        if (pitchToEntity >= maxPitchDifference) {
            result.setFailed("Pitch difference greater than allowed.");
            result.parameter("pitchToEntity", pitchToEntity);
            result.parameter("maxPitchDiff", maxPitchDifference);
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
    }
}
