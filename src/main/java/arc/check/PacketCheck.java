package arc.check;

import arc.Arc;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import java.util.function.Consumer;

/**
 * Represents a check that is packet level.
 */
public abstract class PacketCheck extends Check {

    /**
     * Protocol
     */
    protected final ProtocolManager protocol;

    /**
     * Initialize this check
     *
     * @param name      the name
     * @param checkType the type
     * @param category the check category
     */
    public PacketCheck(String name, CheckType checkType, CheckCategory category) {
        super(name, checkType, category);

        protocol = ProtocolLibrary.getProtocolManager();
    }

    /**
     * Register a packet listener.
     *
     * @param packetType the packet type
     * @param consumer   the consumer
     */
    protected void registerPacketListener(PacketType packetType, Consumer<PacketEvent> consumer) {
        if (packetType.isServer()) {
            protocol.addPacketListener(new PacketAdapter(Arc.plugin(), ListenerPriority.HIGHEST, packetType) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    if (!exempt(event.getPlayer())) {
                        consumer.accept(event);
                    }
                }
            });
        } else {
            protocol.addPacketListener(new PacketAdapter(Arc.plugin(), ListenerPriority.HIGHEST, packetType) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    if (!exempt(event.getPlayer())) {
                        consumer.accept(event);
                    }
                }
            });
        }
    }

}
