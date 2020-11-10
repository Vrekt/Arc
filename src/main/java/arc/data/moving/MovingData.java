package arc.data.moving;

import arc.data.moving.nf.NoFallData;
import arc.data.moving.packets.PacketData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents player moving data.
 */
public final class MovingData {

    /**
     * The register
     */
    private static final Map<UUID, MovingData> REGISTER = new ConcurrentHashMap<>();

    /**
     * Get data
     *
     * @param player the player
     * @return the data
     */
    public static MovingData get(Player player) {
        return REGISTER.computeIfAbsent(player.getUniqueId(), uuid -> new MovingData());
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
     * If the client is on ground or not
     * If Arc determines the player is on ground or not
     */
    private boolean clientOnGround, wasClientOnGround, onGround, wasOnGround;

    /**
     * Ascending/Descending state
     * If we are climbing or not
     */
    private boolean ascending, descending, climbing;

    /**
     * The time we have been on ground
     */
    private int onGroundTime;

    /**
     * Movement location
     * The last known ground location
     */
    private Location from, to, ground;

    /**
     * The vertical distances traveled.
     */
    private double lastVerticalDistance, verticalDistance;

    /**
     * No fall data
     */
    private final NoFallData noFallData = new NoFallData();

    /**
     * Packet data
     */
    private final PacketData packetData = new PacketData();

    public boolean climbing() {
        return climbing;
    }

    public void climbing(boolean climbing) {
        this.climbing = climbing;
    }

    public double lastVerticalDistance() {
        return lastVerticalDistance;
    }

    public void lastVerticalDistance(double lastVerticalDistance) {
        this.lastVerticalDistance = lastVerticalDistance;
    }

    public double vertical() {
        return verticalDistance;
    }

    public void verticalDistance(double verticalDistance) {
        this.verticalDistance = verticalDistance;
    }

    public Location ground() {
        return ground;
    }

    /**
     * @return {@code true} if the player has ground location
     */
    public boolean hasGround() {
        return ground != null;
    }

    public void ground(Location ground) {
        this.ground = ground;
    }

    public Location from() {
        return from;
    }

    public void from(Location from) {
        this.from = from;
    }

    public Location to() {
        return to;
    }

    public void to(Location to) {
        this.to = to;
    }

    public boolean ascending() {
        return ascending;
    }

    public void ascending(boolean ascending) {
        this.ascending = ascending;
    }

    public boolean descending() {
        return descending;
    }

    public void descending(boolean descending) {
        this.descending = descending;
    }

    public boolean onGround() {
        return onGround;
    }

    public boolean clientOnGround() {
        return clientOnGround;
    }

    public void clientOnGround(boolean clientOnGround) {
        this.clientOnGround = clientOnGround;
    }

    public void onGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean wasOnGround() {
        return wasOnGround;
    }

    public void wasOnGround(boolean wasOnGround) {
        this.wasOnGround = wasOnGround;
    }

    /**
     * @return nf data
     */
    public NoFallData nf() {
        return noFallData;
    }

    /**
     * @return packets
     */
    public PacketData packets() {
        return packetData;
    }

    public int onGroundTime() {
        return onGroundTime;
    }

    public void onGroundTime(int onGroundTime) {
        this.onGroundTime = onGroundTime;
    }

    public boolean wasClientOnGround() {
        return wasClientOnGround;
    }

    public void wasClientOnGround(boolean wasClientOnGround) {
        this.wasClientOnGround = wasClientOnGround;
    }
}
