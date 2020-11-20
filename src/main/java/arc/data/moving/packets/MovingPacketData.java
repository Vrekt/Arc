package arc.data.moving.packets;

/**
 * ProtocolLib packet data.
 */
public final class MovingPacketData {

    /**
     * Amount of flying packets
     * Amount of position packets.
     */
    private int flyingPackets, positionPackets;

    /**
     * Cancel packets
     * If the player should be kicked.
     */
    private boolean cancelFlyingPackets, cancelPositionPackets, kick;

    /**
     * The last check.
     */
    private long lastCheck;

    public int flyingPackets() {
        return flyingPackets;
    }

    public void flyingPackets(int flyingPackets) {
        this.flyingPackets = flyingPackets;
    }

    public int positionPackets() {
        return positionPackets;
    }

    public void positionPackets(int positionPackets) {
        this.positionPackets = positionPackets;
    }

    public boolean cancelFlyingPackets() {
        return cancelFlyingPackets;
    }

    public void cancelFlyingPackets(boolean cancelFlyingPackets) {
        this.cancelFlyingPackets = cancelFlyingPackets;
    }

    public boolean cancelPositionPackets() {
        return cancelPositionPackets;
    }

    public void cancelPositionPackets(boolean cancelPositionPackets) {
        this.cancelPositionPackets = cancelPositionPackets;
    }

    public boolean kick() {
        return kick;
    }

    public void kick(boolean kick) {
        this.kick = kick;
    }

    public long lastCheck() {
        return lastCheck;
    }

    public void lastCheck(long lastCheck) {
        this.lastCheck = lastCheck;
    }
}
