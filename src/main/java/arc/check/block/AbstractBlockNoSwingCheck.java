package arc.check.block;

import arc.Arc;
import arc.check.Check;
import arc.check.result.CheckResult;
import arc.check.types.CheckType;
import arc.data.combat.CombatData;
import arc.data.player.PlayerData;
import bridge.Version;
import org.bukkit.entity.Player;

/**
 * Provides a general base for block no swing checks.
 */
public abstract class AbstractBlockNoSwingCheck extends Check {

    /**
     * The minimum swing time allowed.
     */
    private long swingTime;

    public AbstractBlockNoSwingCheck(CheckType checkType) {
        super(checkType);

        buildCheckConfiguration();

        addConfigurationValue("swing-time-old", 100);
        addConfigurationValue("swing-time-new", 1000);
        if (isEnabled()) load();
    }

    /**
     * Build the check configuration for whatever check this is.
     */
    protected abstract void buildCheckConfiguration();

    /**
     * Checks for no swing in legacy mode.
     *
     * @param player the player
     * @param data   their data
     * @return the result
     */
    private boolean checkLegacyNoSwing(Player player, CombatData data) {
        final long delta = System.currentTimeMillis() - data.lastSwingTime();
        if (delta > swingTime) {
            // ensure we are not consuming.
            final PlayerData playerData = PlayerData.get(player);
            if (playerData.isConsuming()) return false;

            final CheckResult result = new CheckResult();
            result.setFailed("No swing animation within time")
                    .withParameter("delta", delta)
                    .withParameter("min", swingTime);
            return checkViolation(player, result);
        }
        return false;
    }

    /**
     * Checks for no swing in newer versions.
     *
     * @param player the player
     * @param data   their data
     * @return the result
     */
    private boolean checkNewNoSwing(Player player, CombatData data) {
        final long now = System.currentTimeMillis();
        final long delta = now - data.getLastNoSwingOther();
        final long swingDelta = now - data.lastSwingTime();

        data.setLastNoSwingOther(now);
        // we have attacked within the last second.
        if (delta <= 1000 && swingDelta >= swingTime) {
            // ensure we are not consuming.
            final PlayerData playerData = PlayerData.get(player);
            if (playerData.isConsuming()) return false;

            final CheckResult result = new CheckResult();
            result.setFailed("No swing animation within time")
                    .withParameter("delta", delta)
                    .withParameter("min", swingDelta);
            return checkViolation(player, result);
        }

        return false;
    }

    /**
     * Check this player for NoSwing
     *
     * @param player the player
     */
    public boolean check(Player player) {
        if (!isEnabled() || exempt(player)) return false;
        final CombatData data = CombatData.get(player);
        return Arc.getMCVersion() == Version.VERSION_1_8 ? checkLegacyNoSwing(player, data) : checkNewNoSwing(player, data);
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        swingTime = Arc.getMCVersion() == Version.VERSION_1_8 ?
                configuration.getLong("swing-time-old") : configuration.getLong("swing-time-new");
    }
}
