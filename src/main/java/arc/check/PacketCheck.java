package arc.check;

import arc.Arc;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import java.util.function.Consumer;

/**
 * Represents a check that is packet level.
 */
public abstract class PacketCheck extends Check {

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
            Arc.arc().protocol().addPacketListener(new PacketAdapter(Arc.plugin(), ListenerPriority.HIGHEST, packetType) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    if (!exempt(event.getPlayer())) {
                        consumer.accept(event);
                    }
                }
            });
        } else {
            Arc.arc().protocol().addPacketListener(new PacketAdapter(Arc.plugin(), ListenerPriority.HIGHEST, packetType) {
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
