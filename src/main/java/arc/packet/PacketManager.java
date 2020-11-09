package arc.packet;

import arc.packet.adapters.EntityActionPacketAdapter;
import arc.packet.adapters.FlyingPacketAdapter;
import arc.packet.adapters.PositionLookPacketAdapter;
import arc.packet.adapters.PositionPacketAdapter;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

/**
 * Basic packet manager.
 */
public final class PacketManager {

    /**
     * The protocol
     */
    private ProtocolManager protocol;

    /**
     * Register packet listeners.
     */
    public void register() {
        protocol = ProtocolLibrary.getProtocolManager();

        protocol.addPacketListener(new FlyingPacketAdapter());
        protocol.addPacketListener(new EntityActionPacketAdapter());
        protocol.addPacketListener(new PositionPacketAdapter());
        protocol.addPacketListener(new PositionLookPacketAdapter());
    }

}
