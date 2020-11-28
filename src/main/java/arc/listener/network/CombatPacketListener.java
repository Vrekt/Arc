package arc.listener.network;

import arc.Arc;
import arc.check.CheckType;
import arc.check.combat.Criticals;
import arc.check.combat.NoSwing;
import arc.check.combat.Reach;
import arc.data.combat.CombatData;
import arc.data.packet.PacketData;
import arc.listener.IPacketListener;
import com.comphenix.packetwrapper.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Player;

/**
 * Listens for combat related events
 */
public final class CombatPacketListener implements IPacketListener {

    /**
     * Criticals
     */
    private final Criticals criticals;

    /**
     * Reach
     */
    private final Reach reach;

    /**
     * No swing
     */
    private final NoSwing noSwing;

    public CombatPacketListener() {
        criticals = (Criticals) Arc.arc().checks().getCheck(CheckType.CRITICALS);
        reach = (Reach) Arc.arc().checks().getCheck(CheckType.REACH);
        noSwing = (NoSwing) Arc.arc().checks().getCheck(CheckType.NO_SWING);
    }

    @Override
    public void createPacketListeners(ProtocolManager protocol) {
        protocol.addPacketListener(new PacketAdapter(Arc.plugin(), ListenerPriority.HIGHEST, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                onUseEntity(event);
            }
        });

        protocol.addPacketListener(new PacketAdapter(Arc.plugin(), ListenerPriority.HIGHEST, PacketType.Play.Client.ARM_ANIMATION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                onArmSwing(event);
            }
        });

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

            boolean criticalsCheck = criticals.onAttack(player, packet);
            boolean reachCheck = reach.onAttack(player, packet);
            boolean noSwingCheck = noSwing.onAttack(player, packet);

            if (criticalsCheck || reachCheck || noSwingCheck) event.setCancelled(true);
        }
    }

    /**
     * Update arm swings here.
     *
     * @param event the event
     */
    private void onArmSwing(PacketEvent event) {
        final Player player = event.getPlayer();

        // SwingFrequency
        final PacketData data = PacketData.get(player);
        if (data.cancelSwingPackets()) {
            event.setCancelled(true);
        }
        data.incrementSwingPacketCount();

        // NoSwing
        CombatData.get(event.getPlayer()).lastSwingTime(System.currentTimeMillis());
    }

}
