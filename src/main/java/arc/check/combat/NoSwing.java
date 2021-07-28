package arc.check.combat;

import arc.Arc;
import arc.check.Check;
import arc.check.result.CheckResult;
import arc.check.types.CheckType;
import arc.data.combat.CombatData;
import bridge.Version;
import org.bukkit.entity.Player;

/**
 * Combat check for NoSwing
 * <p>
 * TODO: Can be improved.
 * TODO: Schedule timer after an attack to check if we received a swing packet then.
 * TODO: But depending on attack speed, could be bypassed, so maybe check swing packets == attack packets
 */
public final class NoSwing extends Check {

    /**
     * The max swing time allowed.
     */
    private long swingTime;

    public NoSwing() {
        super(CheckType.NO_SWING);

        isEnabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("max-swing-time-old", 100);
        addConfigurationValue("max-swing-time-new", 1000);
        if (isEnabled()) load();
    }

    /**
     * Checks for no swing in legacy mode.
     *
     * @param player the player
     * @param data   their data
     * @return the result
     */
    private boolean checkLegacyNoSwing(Player player, CombatData data) {
        final long delta = System.currentTimeMillis() - data.lastSwingTime();
        if (delta >= swingTime) {
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
        final long delta = now - data.lastAttackNoSwing();
        final long swingDelta = now - data.lastSwingTime();

        data.lastAttackNoSwing(now);
        // we have attacked within the last second.
        if (delta <= 1000 && swingDelta >= swingTime) {
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
     * @param data   their data
     */
    public boolean check(Player player, CombatData data) {
        if (exempt(player)) return false;
        return Arc.getMCVersion() == Version.VERSION_1_8 ? checkLegacyNoSwing(player, data)
                : checkNewNoSwing(player, data);
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        swingTime = Arc.getMCVersion() == Version.VERSION_1_8 ? configuration.getLong("max-swing-time-old")
                : configuration.getLong("max-swing-time-new");
    }
}
