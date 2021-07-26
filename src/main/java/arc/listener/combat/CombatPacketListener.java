package arc.listener.combat;

import arc.Arc;
import arc.check.types.CheckType;
import arc.check.combat.Criticals;
import arc.check.combat.KillAura;
import arc.check.combat.NoSwing;
import arc.check.combat.AttackReach;
import arc.data.combat.CombatData;
import arc.data.packet.PacketData;
import arc.listener.AbstractPacketListener;
import arc.world.WorldManager;
import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Listens for combat related events
 */
public final class CombatPacketListener extends AbstractPacketListener {

    /**
     * Criticals
     */
    private final Criticals criticals;

    /**
     * BlockBreakReach
     */
    private final AttackReach reach;

    /**
     * No swing
     */
    private final NoSwing noSwing;

    /**
     * KillAura
     */
    private final KillAura killAura;

    @Override
    public void register(ProtocolManager protocol) {
        listener(protocol, PacketType.Play.Client.USE_ENTITY, this::onUseEntity);
        listener(protocol, PacketType.Play.Client.ARM_ANIMATION, this::onArmSwing);
    }

    public CombatPacketListener() {
        criticals = Arc.getInstance().getCheckManager().getCheck(CheckType.CRITICALS);
        reach = Arc.getInstance().getCheckManager().getCheck(CheckType.ATTACK_REACH);
        noSwing = Arc.getInstance().getCheckManager().getCheck(CheckType.NO_SWING);
        killAura = Arc.getInstance().getCheckManager().getCheck(CheckType.KILL_AURA);
    }

    /**
     * Invoked when the player tries to use an entity.
     *
     * @param event the event
     */
    private void onUseEntity(PacketEvent event) {
        final WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());
        if (packet.getType() == EnumWrappers.EntityUseAction.ATTACK) {
            // the player attacked an entity, run checks.
            final Player player = event.getPlayer();
            if (!WorldManager.isEnabledInWorld(player)) return;

            final Entity entity = packet.getTarget(player.getWorld());
            if (entity instanceof LivingEntity) {

                boolean checkKillAura = false, checkCriticals = false, checkReach = false, checkNoSwing = false;
                final CombatData data = CombatData.get(player);

                if (killAura.isEnabled()) checkKillAura = killAura.check(player, entity, data);
                if (reach.isEnabled()) checkReach = reach.check(player, entity);
                if (noSwing.isEnabled()) checkNoSwing = noSwing.check(player, data);
                if (criticals.isEnabled()) checkCriticals = criticals.check(player);

                if (checkKillAura || checkCriticals || checkReach || checkNoSwing) event.setCancelled(true);
            }
        }
    }

    /**
     * Invoked when the player swings their arm
     *
     * @param event the event
     */
    private void onArmSwing(PacketEvent event) {
        final Player player = event.getPlayer();
        final PacketData data = PacketData.get(player);
        data.incrementSwingPacketCount();

        if (data.cancelSwingPackets()) {
            event.setCancelled(true);
            return;
        }

        final CombatData combatData = CombatData.get(player);
        combatData.lastSwingTime(System.currentTimeMillis());
    }

}
