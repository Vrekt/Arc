package arc.data.packet;

import arc.data.Data;
import arc.utility.math.MathUtil;
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
     * Break packet count
     */
    private int swingPacketCount, payloadPacketCount, breakPacketCount;

    /**
     * If swing packets should be cancelled.
     * If payload packets should be cancelled.
     * If block dig packets should be cancelled.
     */
    private boolean cancelSwingPackets, cancelPayloadPackets, cancelBreakPackets;

    /**
     * Reset
     */
    private long lastBreakPacketReset;

    public int swingPacketCount() {
        return swingPacketCount;
    }

    public void swingPacketCount(int swingPacketCount) {
        this.swingPacketCount = MathUtil.clampInt(swingPacketCount, 0, 1000);
    }

    /**
     * Increment the packet count
     */
    public void incrementSwingPacketCount() {
        swingPacketCount = MathUtil.clampInt(swingPacketCount + 1, 0, 1000);
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
        this.payloadPacketCount = MathUtil.clampInt(payloadPacketCount, 0, 1000);
    }

    /**
     * increment the payload packet count
     */
    public void incrementPayloadPacketCount() {
        payloadPacketCount = MathUtil.clampInt(payloadPacketCount + 1, 0, 1000);
    }

    public boolean cancelPayloadPackets() {
        return cancelPayloadPackets;
    }

    public void cancelPayloadPackets(boolean cancelPayloadPackets) {
        this.cancelPayloadPackets = cancelPayloadPackets;
    }

    public int getBreakPacketCount() {
        return breakPacketCount;
    }

    public void setBreakPacketCount(int breakPacketCount) {
        this.breakPacketCount = MathUtil.clampInt(breakPacketCount, 0, 1000);
    }

    public boolean cancelBreakPackets() {
        return cancelBreakPackets;
    }

    public void setCancelBreakPackets(boolean cancelBreakPackets) {
        this.cancelBreakPackets = cancelBreakPackets;
    }

    public long getLastBreakPacketReset() {
        return lastBreakPacketReset;
    }

    public void setLastBreakPacketReset(long lastBreakPacketReset) {
        this.lastBreakPacketReset = lastBreakPacketReset;
    }
}
