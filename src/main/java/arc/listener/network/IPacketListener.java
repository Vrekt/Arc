package arc.listener.network;

import com.comphenix.protocol.ProtocolManager;

/**
 * Basic interface for a packet listener.
 */
public interface IPacketListener {

    /**
     * Create packet listeners
     *
     * @param protocol the protocol
     */
    void createPacketListeners(ProtocolManager protocol);

}
