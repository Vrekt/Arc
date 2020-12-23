package arc.check.combat;

import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.combat.CombatData;
import org.bukkit.entity.Player;

/**
 * Combat check for NoSwing
 */
public final class NoSwing extends PacketCheck {

    /**
     * The minimum time allowed to receive a swing packet.
     */
    private long swingTime;

    public NoSwing() {
        super(CheckType.NO_SWING);
        if (disableIfNewerThan18()) return;

        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("swing-time", 100);
        if (enabled()) load();
    }

    /**
     * Check this player for NoSwing
     *
     * @param player the player
     */
    public boolean check(Player player) {
        if (exempt(player)) return false;

        final long delta = (System.currentTimeMillis()) - CombatData.get(player).lastSwingTime();
        if (delta > swingTime) {
            final CheckResult result = new CheckResult();
            result.setFailed("No swing animation within time");
            result.parameter("delta", delta);
            result.parameter("min", swingTime);
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
        swingTime = configuration.getLong("swing-time");
    }
}
