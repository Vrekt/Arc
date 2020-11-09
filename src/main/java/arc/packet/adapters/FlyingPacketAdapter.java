package arc.packet.adapters;

import arc.Arc;
import arc.data.moving.MovingData;
import com.comphenix.packetwrapper.WrapperPlayClientFlying;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

/**
 * Handles Flying packets.
 */
public final class FlyingPacketAdapter extends PacketAdapter {

    public FlyingPacketAdapter() {
        super(Arc.plugin(), ListenerPriority.HIGHEST, PacketType.Play.Client.FLYING);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        final var packet = new WrapperPlayClientFlying(event.getPacket());
        final var player = event.getPlayer();
        final var data = MovingData.get(player);
        data.wasClientOnGround(data.clientOnGround());
        data.clientOnGround(packet.getOnGround());
    }
}
