package arc.check.player;

import arc.Arc;
import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CheckResult;
import arc.data.player.PlayerData;
import bridge.Version;
import org.bukkit.entity.Player;

/**
 * Checks if the player is regenerating health too fast.
 */
public final class Regeneration extends Check {

    /**
     * The min time it takes to regain health (1.8)
     * The min time it takes to regain health (>1.8)
     */
    private long legacyRegenerationTime, newRegenerationTime;

    /**
     * IF 1.8
     */
    private boolean useLegacy;

    public Regeneration() {
        super(CheckType.REGENERATION);

        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("legacy-regeneration-time-ms", 3400);
        addConfigurationValue("new-regeneration-time-ms", 450);
        if (enabled()) load();
    }

    /**
     * Check for regeneration
     *
     * @param player the player
     * @param data   their data
     * @return {@code true} if cancel.
     */
    public boolean check(Player player, PlayerData data) {
        if (exempt(player)) return false;

        // the time from now to the last regain event.
        final long time = System.currentTimeMillis() - data.lastHealthRegain();
        final boolean failed = useLegacy ? time < legacyRegenerationTime : time < newRegenerationTime;

        // if its less than the minimum then flag.
        if (failed) {
            final CheckResult result = new CheckResult();
            result.setFailed("Regaining health too fast.")
                    .withParameter("time", time)
                    .withParameter("min", useLegacy ? legacyRegenerationTime : newRegenerationTime);
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
        useLegacy = Arc.version() == Version.VERSION_1_8;
        legacyRegenerationTime = configuration.getLong("legacy-regeneration-time-ms");
        newRegenerationTime = configuration.getLong("new-regeneration-time-ms");
    }
}
