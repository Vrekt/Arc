package arc.check.player;

import arc.Arc;
import arc.check.Check;
import arc.check.result.CheckResult;
import arc.check.types.CheckType;
import arc.data.player.PlayerData;
import bridge.Version;
import org.bukkit.entity.Player;

/**
 * Checks if the player is regenerating health too fast.
 */
public final class Regeneration extends Check {

    /**
     * The minimum time it takes to regenerate.
     */
    private long regenerationTime;

    public Regeneration() {
        super(CheckType.REGENERATION);

        isEnabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("regeneration-time-minimum-old", 3400);
        addConfigurationValue("regeneration-time-minimum-new", 450);
        if (isEnabled()) load();
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

        // if its less than the minimum then flag.
        final long time = System.currentTimeMillis() - data.lastHealthRegain();
        if (time < regenerationTime) {
            final CheckResult result = new CheckResult();
            result.setFailed("Regaining health too fast.")
                    .withParameter("time", time)
                    .withParameter("min", regenerationTime);
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
        regenerationTime = Arc.getMCVersion() == Version.VERSION_1_8 ? configuration.getLong("regeneration-time-minimum-old")
                : configuration.getLong("regeneration-time-minimum-new");
    }
}
