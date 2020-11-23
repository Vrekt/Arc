package arc.listener.network;

import arc.Arc;
import arc.data.moving.MovingData;
import com.comphenix.packetwrapper.WrapperPlayClientFlying;
import com.comphenix.packetwrapper.WrapperPlayClientPosition;
import com.comphenix.packetwrapper.WrapperPlayClientPositionLook;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

/**
 * Listens for moving packets.
 */
public final class MovingPacketListener {

    /**
     * Initialize and register the listeners
     * TODO: Track LOOK packet for OnGround?
     *
     * @param protocol the protocol manager
     */
    public MovingPacketListener(ProtocolManager protocol) {
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
     * Invoked when the client sends FLYING
     *
     * @param event the event
     */
    private void onFlying(PacketEvent event) {
        final var packet = new WrapperPlayClientFlying(event.getPacket());
        final var player = event.getPlayer();
        final var data = MovingData.get(player);
        final var packets = data.packets();

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
        final var packet = new WrapperPlayClientPosition(event.getPacket());
        final var player = event.getPlayer();
        final var data = MovingData.get(player);
        final var packets = data.packets();

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
        final var packet = new WrapperPlayClientPositionLook(event.getPacket());
        final var player = event.getPlayer();
        final var data = MovingData.get(player);
        final var packets = data.packets();

        data.wasClientOnGround(data.clientOnGround());
        data.clientOnGround(packet.getOnGround());

        packets.positionPackets(packets.positionPackets() + 1);
        if (packets.cancelPositionPackets()) event.setCancelled(true);
    }

}
