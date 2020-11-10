package arc.packet.adapters;

import arc.Arc;
import arc.data.moving.MovingData;
import com.comphenix.packetwrapper.WrapperPlayClientPosition;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public final class PositionPacketAdapter extends PacketAdapter {

    public PositionPacketAdapter() {
        super(Arc.plugin(), ListenerPriority.HIGHEST, PacketType.Play.Client.POSITION);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        final var packet = new WrapperPlayClientPosition(event.getPacket());
        final var player = event.getPlayer();
        final var data = MovingData.get(player);
        final var packets = data.packets();

        data.wasClientOnGround(data.clientOnGround());
        data.clientOnGround(packet.getOnGround());
        packets.positionPackets(packets.positionPackets() + 1);
        packets.lastPositionPacket(System.currentTimeMillis());

        if (packets.cancelPositionPackets()) event.setCancelled(true);
    }
}
