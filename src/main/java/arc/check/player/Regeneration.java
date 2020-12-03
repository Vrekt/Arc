package arc.check.player;

import bridge.Version;
import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CheckCallback;
import arc.check.result.CheckResult;
import arc.data.player.PlayerData;
import arc.violation.result.ViolationResult;
import org.bukkit.entity.Player;

/**
 * Checks if the player is regenerating health too fast.
 */
public final class Regeneration extends Check {

    /**
     * The min time it takes to regain health.
     */
    private long regenerationTime;

    public Regeneration() {
        super(CheckType.REGENERATION);
        if (disableIfNewerThan(Version.VERSION_1_8)) return;

        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .write();

        addConfigurationValue("regeneration-time-ms", 3400);
        load();
    }

    /**
     * Check for regeneration
     *
     * @param player   the player
     * @param data     their data
     * @param callback the action callback
     */
    public void check(Player player, PlayerData data, CheckCallback callback) {
        if (exempt(player)) return;

        // the time from now to the last regain event.
        final long time = System.currentTimeMillis() - data.lastHealthRegain();
        // if its less than the minimum then flag.
        if (time < regenerationTime) {
            final CheckResult result = new CheckResult(CheckResult.Result.FAILED);
            result.information("Regaining health too fast, time=" + time + ", min=" + regenerationTime);
            final ViolationResult violation = result(player, result);
            callback.onResult(violation);
        }
    }

    @Override
    public void reloadConfig() {
        if (enabled()) load();
    }

    @Override
    public void load() {
        regenerationTime = getValueLong("regeneration-time-ms");
    }
}
