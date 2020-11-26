package arc.listener.network;

import arc.Arc;
import arc.data.moving.MovingData;
import arc.data.moving.packets.MovingPacketData;
import com.comphenix.packetwrapper.WrapperPlayClientFlying;
import com.comphenix.packetwrapper.WrapperPlayClientPosition;
import com.comphenix.packetwrapper.WrapperPlayClientPositionLook;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;

/**
 * Listens for moving packets.
 */
public final class MovingPacketListener {

    /**
     * Register all packet listeners.
     *
     * @param protocol the protocol
     */
    public void createPacketListeners(ProtocolManager protocol) {
        // flying
        protocol.addPacketListener(new PacketAdapter(Arc.plugin(), ListenerPriority.HIGHEST, PacketType.Play.Client.FLYING) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                onFlying(event);
            }
        });

        // position
        protocol.addPacketListener(new PacketAdapter(Arc.plugin(), ListenerPriority.HIGHEST, PacketType.Play.Client.POSITION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                onPosition(event);
            }
        });

        // position look
        protocol.addPacketListener(new PacketAdapter(Arc.plugin(), ListenerPriority.HIGHEST, PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                onPositionLook(event);
            }
        });

    }

    /**
     * Remove all packet listeners.
     *
     * @param protocol the protocol.
     */
    public void removePacketListeners(ProtocolManager protocol) {

    }

    /**
     * Invoked when the client sends FLYING
     *
     * @param event the event
     */
    private void onFlying(PacketEvent event) {
        final WrapperPlayClientFlying packet = new WrapperPlayClientFlying(event.getPacket());
        final Player player = event.getPlayer();
        final MovingData data = MovingData.get(player);
        final MovingPacketData packets = data.packets();

        data.wasClientOnGround(data.clientOnGround());
        data.clientOnGround(packet.getOnGround());

        packets.flyingPackets(packets.flyingPackets() + 1);
        if (packets.cancelFlyingPackets()) event.setCancelled(true);
    }

    /**
     * Invoked when the client sends POSITION
     *
     * @param event the event
     */
    private void onPosition(PacketEvent event) {
        final WrapperPlayClientPosition packet = new WrapperPlayClientPosition(event.getPacket());
        final Player player = event.getPlayer();
        final MovingData data = MovingData.get(player);
        final MovingPacketData packets = data.packets();

        data.wasClientOnGround(data.clientOnGround());
        data.clientOnGround(packet.getOnGround());
        packets.positionPackets(packets.positionPackets() + 1);

        if (packets.cancelPositionPackets()) event.setCancelled(true);
    }

    /**
     * Invoked when the client sends POSITION_LOOK
     *
     * @param event the event
     */
    private void onPositionLook(PacketEvent event) {
        final WrapperPlayClientPositionLook packet = new WrapperPlayClientPositionLook(event.getPacket());
        final Player player = event.getPlayer();
        final MovingData data = MovingData.get(player);
        final MovingPacketData packets = data.packets();

        data.wasClientOnGround(data.clientOnGround());
        data.clientOnGround(packet.getOnGround());

        packets.positionPackets(packets.positionPackets() + 1);
        if (packets.cancelPositionPackets()) event.setCancelled(true);
    }

}
