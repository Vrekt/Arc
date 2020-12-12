package arc.data.moving;

import arc.data.moving.nf.NoFallData;
import arc.data.moving.packets.MovingPacketData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
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
     * The time we have been descending
     */
    private int onGroundTime, descendingTime, ascendingTime;

    /**
     * The amount of times the vertical has been similar.
     */
    private int similarVerticalAmount, noVerticalAmount, similarVerticalAmountJesus;

    /**
     * The time we have been in water
     */
    private int inWaterTime;

    /**
     * The max difference allowed for similar vertical amounts.
     */
    private double similarVerticalDifference;

    /**
     * Movement location
     * The last known ground location
     * The speed setback location
     */
    private Location from, to, ground, speedSetback;

    /**
     * Last moving update.
     * Last teleport
     */
    private long lastMovingUpdate, lastTeleport;

    /**
     * The vertical distances traveled.
     */
    private double lastVerticalDistance, verticalDistance;

    /**
     * If the player is sprinting
     * If the player is sneaking
     */
    private boolean sprinting, sneaking;

    /**
     * Sneaking time and sprint time
     * The time on ice
     * The time off ice
     */
    private int sneakTime, sprintTime, onIceTime, offIceTime;

    /**
     * Average in water diffs
     */
    private List<Double> averageInWaterDifferences;

    /**
     * No fall data
     */
    private final NoFallData noFallData = new NoFallData();

    /**
     * Packet data
     */
    private final MovingPacketData packetData = new MovingPacketData();

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
    public MovingPacketData packets() {
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

    public long lastMovingUpdate() {
        return lastMovingUpdate;
    }

    public void lastMovingUpdate(long lastMovingUpdate) {
        this.lastMovingUpdate = lastMovingUpdate;
    }

    public int similarVerticalAmount() {
        return similarVerticalAmount;
    }

    public void similarVerticalAmount(int similarVerticalAmount) {
        this.similarVerticalAmount = similarVerticalAmount;
    }

    public int noVerticalAmount() {
        return noVerticalAmount;
    }

    public void noVerticalAmount(int noVerticalAmount) {
        this.noVerticalAmount = noVerticalAmount;
    }

    public int inWaterTime() {
        return inWaterTime;
    }

    public void inWaterTime(int inWaterTime) {
        this.inWaterTime = inWaterTime;
    }

    public List<Double> averageInWaterDifferences() {
        return averageInWaterDifferences;
    }

    public void averageInWaterDifferences(List<Double> averageInWaterDifferences) {
        this.averageInWaterDifferences = averageInWaterDifferences;
    }

    public int descendingTime() {
        return descendingTime;
    }

    public void descendingTime(int descendingTime) {
        this.descendingTime = descendingTime;
    }

    public int ascendingTime() {
        return ascendingTime;
    }

    public void ascendingTime(int ascendingTime) {
        this.ascendingTime = ascendingTime;
    }

    public long lastTeleport() {
        return lastTeleport;
    }

    public void lastTeleport(long lastTeleport) {
        this.lastTeleport = lastTeleport;
    }

    public boolean sprinting() {
        return sprinting;
    }

    public void sprinting(boolean sprinting) {
        this.sprinting = sprinting;
    }

    public boolean sneaking() {
        return sneaking;
    }

    public void sneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }

    public Location speedSetback() {
        return speedSetback;
    }

    public void speedSetback(Location speedSetback) {
        this.speedSetback = speedSetback;
    }

    public int sneakTime() {
        return sneakTime;
    }

    public void sneakTime(int sneakTime) {
        this.sneakTime = sneakTime;
    }

    public int sprintTime() {
        return sprintTime;
    }

    public void sprintTime(int sprintTime) {
        this.sprintTime = sprintTime;
    }

    public int onIceTime() {
        return onIceTime;
    }

    public void onIceTime(int onIceTime) {
        this.onIceTime = onIceTime;
    }

    public int offIceTime() {
        return offIceTime;
    }

    public void offIceTime(int offIceTime) {
        this.offIceTime = offIceTime;
    }
}
