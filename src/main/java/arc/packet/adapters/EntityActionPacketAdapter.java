package arc.packet.adapters;

import arc.Arc;
import com.comphenix.packetwrapper.WrapperPlayClientEntityAction;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public final class EntityActionPacketAdapter extends PacketAdapter {

    public EntityActionPacketAdapter() {
        super(Arc.plugin(), ListenerPriority.HIGHEST, PacketType.Play.Client.ENTITY_ACTION);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        final var packet = new WrapperPlayClientEntityAction(event.getPacket());
        final var player = event.getPlayer();
    }
}
