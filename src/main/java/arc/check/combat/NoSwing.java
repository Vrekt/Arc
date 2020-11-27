package arc.check.combat;

import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.combat.CombatData;
import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
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
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .write();

        addConfigurationValue("swing-time", 100);

        if (enabled()) load();
    }

    /**
     * Invoked when we use an entity
     *
     * @param event the event
     */
    private void onUseEntity(PacketEvent event) {
        final WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());
        if (packet.getType() == EnumWrappers.EntityUseAction.ATTACK) {
            final Player player = event.getPlayer();
            final long delta = (System.currentTimeMillis()) - CombatData.get(player).lastSwingTime();
            if (delta > swingTime) {
                final CheckResult result = new CheckResult(CheckResult.Result.FAILED, "No swing animation, delta=" + delta + " min=" + swingTime);
                event.setCancelled(result(player, result).cancel());
            }
        }
    }

    /**
     * Invoked when the player swings their arm
     *
     * @param event the event
     */
    private void onArmAnimation(PacketEvent event) {
        CombatData.get(event.getPlayer()).lastSwingTime(System.currentTimeMillis());
    }

    @Override
    public void reloadConfig() {
        unload();
        if (enabled()) load();
    }

    @Override
    public void load() {
        swingTime = getValueLong("swing-time");
        registerPacketListener(PacketType.Play.Client.USE_ENTITY, this::onUseEntity);
        registerPacketListener(PacketType.Play.Client.ARM_ANIMATION, this::onArmAnimation);
    }

    @Override
    public void unload() {
        unregisterPacketListeners();
    }
}
