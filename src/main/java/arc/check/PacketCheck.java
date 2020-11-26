package arc.check;

import arc.Arc;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Represents a check that is packet level.
 */
public abstract class PacketCheck extends Check {

    /**
     * The listeners.
     */
    private final Set<PacketListener> listeners = new HashSet<>();

    /**
     * Initialize this check
     *
     * @param checkType the type
     */
    public PacketCheck(CheckType checkType) {
        super(checkType);
    }

    /**
     * Register a packet listener.
     *
     * @param packetType the packet type
     * @param consumer   the consumer
     */
    protected void registerPacketListener(PacketType packetType, Consumer<PacketEvent> consumer) {
        if (packetType.isServer()) {
            final PacketAdapter serverAdapter = new PacketAdapter(Arc.plugin(), ListenerPriority.HIGHEST, packetType) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    if (!exempt(event.getPlayer())) {
                        consumer.accept(event);
                    }
                }
            };

            listeners.add(serverAdapter);
            Arc.arc().protocol().addPacketListener(serverAdapter);
        } else {
            final PacketAdapter clientAdapter = new PacketAdapter(Arc.plugin(), ListenerPriority.HIGHEST, packetType) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    if (!exempt(event.getPlayer())) {
                        consumer.accept(event);
                    }
                }
            };
            listeners.add(clientAdapter);
            Arc.arc().protocol().addPacketListener(clientAdapter);
        }
    }

    /**
     * Unregister all packet listeners.
     */
    protected void unregisterPacketListeners() {
        listeners.forEach(listener -> Arc.arc().protocol().removePacketListener(listener));
        listeners.clear();
    }

}
