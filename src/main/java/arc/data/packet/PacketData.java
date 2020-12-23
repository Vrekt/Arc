package arc.data.packet;

import arc.data.Data;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles storing packet
 */
public final class PacketData implements Data {

    /**
     * The register
     */
    private static final Map<UUID, PacketData> REGISTER = new ConcurrentHashMap<>();

    /**
     * Get data
     *
     * @param player the player
     * @return the data
     */
    public static PacketData get(Player player) {
        return REGISTER.computeIfAbsent(player.getUniqueId(), uuid -> new PacketData());
    }

    /**
     * Remove data
     *
     * @param player the player
     */
    public static void remove(Player player) {
        REGISTER.remove(player.getUniqueId());
    }

    /**
     * Swing packet count
     * Payload packet count
     */
    private int swingPacketCount, payloadPacketCount;

    /**
     * If swing packets should be cancelled.
     * If payload packets should be cancelled.
     */
    private boolean cancelSwingPackets, cancelPayloadPackets;

    public int swingPacketCount() {
        return swingPacketCount;
    }

    public void swingPacketCount(int swingPacketCount) {
        this.swingPacketCount = swingPacketCount;
    }

    /**
     * Increment the packet count
     */
    public void incrementSwingPacketCount() {
        swingPacketCount++;
    }

    public boolean cancelSwingPackets() {
        return cancelSwingPackets;
    }

    public void cancelSwingPackets(boolean cancelSwingPackets) {
        this.cancelSwingPackets = cancelSwingPackets;
    }

    public int payloadPacketCount() {
        return payloadPacketCount;
    }

    public void payloadPacketCount(int payloadPacketCount) {
        this.payloadPacketCount = payloadPacketCount;
    }

    /**
     * increment the payload packet count
     */
    public void incrementPayloadPacketCount() {
        payloadPacketCount++;
    }

    public boolean cancelPayloadPackets() {
        return cancelPayloadPackets;
    }

    public void cancelPayloadPackets(boolean cancelPayloadPackets) {
        this.cancelPayloadPackets = cancelPayloadPackets;
    }

}
