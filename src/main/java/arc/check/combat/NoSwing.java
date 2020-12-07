package arc.check.combat;

import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.combat.CombatData;
import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.wrappers.EnumWrappers;
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
     * Invoked when we interact with an entity.
     *
     * @param player the player
     * @param packet the packet
     */
    public boolean onAttack(Player player, WrapperPlayClientUseEntity packet) {
        if (!enabled() || exempt(player)) return false;

        if (packet.getType() == EnumWrappers.EntityUseAction.ATTACK) {
            final long delta = (System.currentTimeMillis()) - CombatData.get(player).lastSwingTime();
            if (delta > swingTime) {
                final CheckResult result = new CheckResult(CheckResult.Result.FAILED, "No swing animation, delta=" + delta + " min=" + swingTime);
                return checkViolation(player, result).cancel();
            }
        }
        return false;
    }

    @Override
    public void reloadConfig() {
        if (enabled()) load();
    }

    @Override
    public void load() {
        swingTime = configuration.getLong("swing-time");
    }
}
