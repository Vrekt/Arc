package arc.check.combat;

import arc.Arc;
import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
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
public final class NoSwing extends PacketCheck {
    /**
     * The (legacy) minimum time allowed to receive a swing packet.
     * The new minimum time allowed to receive a swing packet.
     */
    private long swingTime, newSwingTime;

    /**
     * If the check is legacy.
     */
    private boolean isLegacy;

    public NoSwing() {
        super(CheckType.NO_SWING);

        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("legacy-swing-time", 100);
        addConfigurationValue("new-swing-time", 1000);
        if (enabled()) load();
    }

    /**
     * Check this player for NoSwing
     *
     * @param player the player
     * @param data   their data
     */
    public boolean check(Player player, CombatData data) {
        if (exempt(player)) return false;
        return isLegacy ? legacyCheck(player, data) : newCheck(player, data);
    }

    /**
     * The 1.8 legacy check.
     *
     * @param player the player
     * @param data   their data
     * @return the result
     */
    public boolean legacyCheck(Player player, CombatData data) {
        final long delta = System.currentTimeMillis() - data.lastSwingTime();
        if (delta > swingTime) {
            final CheckResult result = new CheckResult();
            result.setFailed("No swing animation within time")
                    .withParameter("delta", delta)
                    .withParameter("min", swingTime);
            return checkViolation(player, result);
        }
        return false;
    }

    /**
     * The new (>1.8) check
     *
     * @param player the player
     * @param data   their data
     * @return the result
     */
    public boolean newCheck(Player player, CombatData data) {
        final long now = System.currentTimeMillis();
        final long delta = now - data.lastAttackNoSwing();
        final long swingDelta = now - data.lastSwingTime();

        data.lastAttackNoSwing(now);
        // we have attacked within the last second.
        if (delta <= 1000 && swingDelta >= newSwingTime) {
            final CheckResult result = new CheckResult();
            result.setFailed("No swing animation within time")
                    .withParameter("delta", delta)
                    .withParameter("min", swingDelta);
            return checkViolation(player, result);
        }

        return false;
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        swingTime = configuration.getLong("legacy-swing-time");
        newSwingTime = configuration.getLong("new-swing-time");
        isLegacy = Arc.version() == Version.VERSION_1_8;
    }
}
