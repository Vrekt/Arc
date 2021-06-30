package arc.listener;

import arc.Arc;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import java.util.function.Consumer;

/**
 * Represents a basic packet listener.
 */
public abstract class AbstractPacketListener {

    /**
     * Register this packet listener.
     */
    public abstract void register(ProtocolManager protocol);

    /**
     * Register a new listener
     *
     * @param consumer the consumer
     */
    protected void listener(ProtocolManager protocol, PacketType packetType, Consumer<PacketEvent> consumer) {
        protocol.addPacketListener(new PacketAdapter(Arc.getPlugin(), ListenerPriority.HIGHEST, packetType) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                consumer.accept(event);
            }
        });
    }

}
