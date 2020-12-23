package arc.check.player;

import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CheckResult;
import arc.data.player.PlayerData;
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
        if (disableIfNewerThan18()) return;

        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("regeneration-time-ms", 3400);
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
        // if its less than the minimum then flag.
        if (time < regenerationTime) {
            final CheckResult result = new CheckResult();
            result.setFailed("Regaining health too fast.");
            result.parameter("time", time);
            result.parameter("min", regenerationTime);
            return checkViolation(player, result).cancel();
        }
        return false;
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        regenerationTime = configuration.getLong("regeneration-time-ms");
    }
}
