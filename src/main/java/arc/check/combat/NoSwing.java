package arc.check.combat;

import arc.check.compatibility.CheckVersion;
import arc.check.implementations.MultiVersionCheck;
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
public final class NoSwing extends MultiVersionCheck {
    /**
     * The minimum swing time allowed.
     */
    private long swingTime;

    /**
     * The current version to check.
     */
    private CheckVersion<CombatData> version;

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

        registerVersion(Version.VERSION_1_8);
        registerVersion(Version.VERSION_1_12);
        registerVersion(Version.VERSION_1_16);

        addValueToVersion(Version.VERSION_1_8, "swing-time", 100);
        addValueToVersion(Version.VERSION_1_12, "swing-time", 1000);
        addValueToVersion(Version.VERSION_1_16, "swing-time", 1000);
        if (enabled()) load();
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
        return version.check(player, data);
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        version = VERSION == Version.VERSION_1_8 ? this::checkLegacyNoSwing : this::checkNewNoSwing;
        swingTime = getVersionSection().getLong("swing-time");
    }
}
