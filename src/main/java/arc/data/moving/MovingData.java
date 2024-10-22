package arc.data.moving;

import arc.data.Data;
import arc.utility.math.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents player moving data.
 */
public final class MovingData implements Data {

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
     * @return temporary moving data
     */
    public static MovingData retrieveTemporary() {
        return new MovingData();
    }

    /**
     * From movement
     * To movement
     * the ground location
     */
    private Location from, to, ground;

    /**
     * Arc ground and client ground
     */
    private boolean clientOnGround, wasClientOnGround, onGround, wasOnGround;

    /**
     * If the client position packet is on-ground.
     */
    private boolean clientPositionOnGround;

    /**
     * Ascending/Descending state
     * If we have a climbable
     * If we are climbing or not + had
     * If we are sneaking/sprinting
     * If we are on ice
     * If we are in liquid
     * If we were in liquid
     */
    private boolean ascending, descending, hasClimbable, hadClimbable, climbing, onIce, inLiquid;

    private final AtomicBoolean sneaking = new AtomicBoolean(), sprinting = new AtomicBoolean();

    /**
     * The various packet counts.
     */
    private int flyingPackets, positionPackets, positionLookPackets, lookPackets;

    /**
     * If any of the packets should be cancelled.
     */
    private boolean cancelFlying, cancelPosition, cancelLook;

    /**
     * The time we have been on ground
     * The time we have been descending
     * The time we have been ascending
     * The time in air
     */
    private int onGroundTime, descendingTime, ascendingTime, inAirTime;

    /**
     * Sneaking time and sprint time
     * The time on ice
     * The time off ice
     * Invalid ground
     * The time in liquid
     * The time climbing
     */
    private int sneakTime, sprintTime, onIceTime, offIceTime, invalidGround, liquidTime, climbTime;

    /**
     * Out of liquid time
     */
    private int outOfLiquidTime;

    /**
     * The current and last vertical distance
     */
    private double vertical, lastVertical;

    /**
     * The last moving update.
     * The last flying packet
     */
    private long lastMovingUpdate, lastFlyingPacket;

    /**
     * The descending location for distance tracking.
     * The valid falling location for distance checking.
     * The last ladder location
     */
    private Location noFallDescendingLocation, validFallingLocation, ladderLocation;

    /**
     * Descending start.
     */
    private Location flightDescendingLocation;

    /**
     * If the player has failed no-fall.
     */
    private boolean failedNoFall;

    /**
     * The water location for distance tracking.
     */
    private Location waterLocation;

    /**
     * The amount of no distance changes
     */
    private int noDistanceChanges;

    /**
     * The last water distance
     */
    private double lastWaterDistance;

    /**
     * The criticals no movement amount.
     * The criticals similar movement amount.
     */
    private int noMovementAmount, similarMovementAmount;

    /**
     * If we have slimeblock
     */
    private boolean hasSlimeblock;

    /**
     * Safe location to teleport to.
     */
    private Location safeLocation;

    /**
     * Last safe location update.
     */
    private long lastSafeUpdate;

    /**
     * If the safe location can be updated.
     */
    private boolean canSafeBeUpdated;

    /**
     * IF the player was launched by a slimeblock.
     */
    private boolean hasSlimeBlockLaunch;

    /**
     * Last rocket use.
     */
    private long lastRocketUse;

    /**
     * If tracking elytra ascending
     */
    private boolean trackingAscending;

    /**
     * Start ascending location
     */
    private Location flightAscendingLocation;

    /**
     * Increases everytime a rocket is used
     * Max descend speed when gliding
     */
    private double rocketDistanceBuffer, maxDescendSpeed;

    /**
     * No vertical movement amount when flying.
     */
    private int noVerticalMovementAmount;

    /**
     * Last time player had levitation
     */
    private long lastLevitationEffect;

    /**
     * If player was previously on boat.
     */
    private boolean wasOnBoat;

    /**
     * Player isn't moving down.
     * <p>
     * No reset times.
     * <p>
     */
    private int noGlideTime, noResetAscendTime, noResetDescendTime;

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

    public Location ground() {
        return ground;
    }

    public void ground(Location ground) {
        this.ground = ground;
    }

    public boolean clientOnGround() {
        return clientOnGround;
    }

    public void clientOnGround(boolean clientOnGround) {
        this.clientOnGround = clientOnGround;
    }

    public boolean wasClientOnGround() {
        return wasClientOnGround;
    }

    public void wasClientOnGround(boolean wasClientOnGround) {
        this.wasClientOnGround = wasClientOnGround;
    }

    public boolean onGround() {
        return onGround;
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

    public boolean hasClimbable() {
        return hasClimbable;
    }

    public void hasClimbable(boolean hasClimbable) {
        this.hasClimbable = hasClimbable;
    }

    public boolean hadClimbable() {
        return hadClimbable;
    }

    public void hadClimbable(boolean hadClimbable) {
        this.hadClimbable = hadClimbable;
    }

    public boolean climbing() {
        return climbing;
    }

    public void climbing(boolean climbing) {
        this.climbing = climbing;
    }

    public boolean sneaking() {
        return sneaking.get();
    }

    public void sneaking(boolean sneaking) {
        this.sneaking.set(sneaking);
    }

    public boolean sprinting() {
        return sprinting.get();
    }

    public void sprinting(boolean sprinting) {
        this.sprinting.set(sprinting);
    }

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

    public int positionLookPackets() {
        return positionLookPackets;
    }

    public void positionLookPackets(int positionLookPackets) {
        this.positionLookPackets = positionLookPackets;
    }

    public int lookPackets() {
        return lookPackets;
    }

    public void lookPackets(int lookPackets) {
        this.lookPackets = lookPackets;
    }

    public int onGroundTime() {
        return onGroundTime;
    }

    public void onGroundTime(int onGroundTime) {
        this.onGroundTime = MathUtil.clampInt(onGroundTime, 0, 1000);
    }

    public void incrementOnGroundTime() {
        onGroundTime(onGroundTime + 1);
    }

    public int descendingTime() {
        return descendingTime;
    }

    public void descendingTime(int descendingTime) {
        this.descendingTime = MathUtil.clampInt(descendingTime, 0, 1000);
    }

    public void incrementDescendingTime() {
        descendingTime(descendingTime + 1);
    }

    public int ascendingTime() {
        return ascendingTime;
    }

    public void ascendingTime(int ascendingTime) {
        this.ascendingTime = MathUtil.clampInt(ascendingTime, 0, 1000);
    }

    public void incrementAscendingTime() {
        ascendingTime(ascendingTime + 1);
    }

    public int sneakTime() {
        return sneakTime;
    }

    public void sneakTime(int sneakTime) {
        this.sneakTime = MathUtil.clampInt(sneakTime, 0, 1000);
    }

    public void incrementSneakTime() {
        sneakTime(sneakTime + 1);
    }

    public int sprintTime() {
        return sprintTime;
    }

    public void sprintTime(int sprintTime) {
        this.sprintTime = MathUtil.clampInt(sprintTime, 0, 1000);
    }

    public void incrementSprintTime() {
        sprintTime(sprintTime + 1);
    }

    public int onIceTime() {
        return onIceTime;
    }

    public void onIceTime(int onIceTime) {
        this.onIceTime = MathUtil.clampInt(onIceTime, 0, 1000);
    }

    public void incrementOnIceTime() {
        onIceTime(onIceTime + 1);
    }

    public int offIceTime() {
        return offIceTime;
    }

    public void offIceTime(int offIceTime) {
        this.offIceTime = MathUtil.clampInt(offIceTime, 0, 1000);
    }

    public void incrementOffIceTime() {
        offIceTime(offIceTime + 1);
    }

    public boolean onIce() {
        return onIce;
    }

    public void onIce(boolean onIce) {
        this.onIce = onIce;
    }

    public double vertical() {
        return vertical;
    }

    public void vertical(double vertical) {
        this.vertical = vertical;
    }

    public double lastVertical() {
        return lastVertical;
    }

    public void lastVertical(double lastVertical) {
        this.lastVertical = lastVertical;
    }

    public long lastMovingUpdate() {
        return lastMovingUpdate;
    }

    public void lastMovingUpdate(long lastMovingUpdate) {
        this.lastMovingUpdate = lastMovingUpdate;
    }

    public boolean inLiquid() {
        return inLiquid;
    }

    public void inLiquid(boolean inLiquid) {
        this.inLiquid = inLiquid;
    }

    public long lastFlyingPacket() {
        return lastFlyingPacket;
    }

    public void lastFlyingPacket(long lastFlyingPacket) {
        this.lastFlyingPacket = lastFlyingPacket;
    }

    public int invalidGround() {
        return invalidGround;
    }

    public void invalidGround(int invalidGround) {
        this.invalidGround = MathUtil.clampInt(invalidGround, 0, 1000);
    }

    public boolean failedNoFall() {
        return failedNoFall;
    }

    public void failedNoFall(boolean failedNoFall) {
        this.failedNoFall = failedNoFall;
    }

    public Location descendingLocation() {
        return noFallDescendingLocation;
    }

    public void descendingLocation(Location descendingLocation) {
        this.noFallDescendingLocation = descendingLocation;
    }

    public Location ladderLocation() {
        return ladderLocation;
    }

    public void ladderLocation(Location ladderLocation) {
        this.ladderLocation = ladderLocation;
    }

    public Location validFallingLocation() {
        return validFallingLocation;
    }

    public void validFallingLocation(Location validFallingLocation) {
        this.validFallingLocation = validFallingLocation;
    }

    public boolean cancelFlying() {
        return cancelFlying;
    }

    public void cancelFlying(boolean cancelFlying) {
        this.cancelFlying = cancelFlying;
    }

    public boolean cancelPosition() {
        return cancelPosition;
    }

    public void cancelPosition(boolean cancelPosition) {
        this.cancelPosition = cancelPosition;
    }

    public boolean cancelLook() {
        return cancelLook;
    }

    public void cancelLook(boolean cancelLook) {
        this.cancelLook = cancelLook;
    }

    public int liquidTime() {
        return liquidTime;
    }

    public void liquidTime(int liquidTime) {
        this.liquidTime = MathUtil.clampInt(liquidTime, 0, 100);
    }

    public int getOutOfLiquidTime() {
        return outOfLiquidTime;
    }

    public void setOutOfLiquidTime(int outOfLiquidTime) {
        this.outOfLiquidTime = MathUtil.clampInt(outOfLiquidTime, 0, 100);
    }

    public Location waterLocation() {
        return waterLocation;
    }

    public void waterLocation(Location waterLocation) {
        this.waterLocation = waterLocation;
    }

    public int noDistanceChanges() {
        return noDistanceChanges;
    }

    public void noDistanceChanges(int noDistanceChanges) {
        this.noDistanceChanges = MathUtil.clampInt(noDistanceChanges, 0, 100);
    }

    public double lastWaterDistance() {
        return lastWaterDistance;
    }

    public void lastWaterDistance(double lastWaterDistance) {
        this.lastWaterDistance = lastWaterDistance;
    }

    public boolean clientPositionOnGround() {
        return clientPositionOnGround;
    }

    public void clientPositionOnGround(boolean clientPositionOnGround) {
        this.clientPositionOnGround = clientPositionOnGround;
    }

    public int noMovementAmount() {
        return noMovementAmount;
    }

    public void noMovementAmount(int noMovementAmount) {
        this.noMovementAmount = MathUtil.clampInt(noMovementAmount, 0, 100);
    }

    public int similarMovementAmount() {
        return similarMovementAmount;
    }

    public void similarMovementAmount(int similarMovementAmount) {
        this.similarMovementAmount = MathUtil.clampInt(similarMovementAmount, 0, 100);
    }

    public int climbTime() {
        return climbTime;
    }

    public void climbTime(int climbTime) {
        this.climbTime = MathUtil.clampInt(climbTime, 0, 100);
    }

    public boolean hasSlimeblock() {
        return hasSlimeblock;
    }

    public void hasSlimeblock(boolean hasSlimeblock) {
        this.hasSlimeblock = hasSlimeblock;
    }

    public Location getSafeLocation() {
        return safeLocation;
    }

    public void setSafeLocation(Location safeLocation) {
        this.safeLocation = safeLocation;
    }

    public boolean shouldSafeBeUpdated() {
        return canSafeBeUpdated;
    }

    public void setCanSafeBeUpdated(boolean canSafeBeUpdated) {
        this.canSafeBeUpdated = canSafeBeUpdated;
    }

    public long getLastSafeUpdate() {
        return lastSafeUpdate;
    }

    public void setLastSafeUpdate(long lastSafeUpdate) {
        this.lastSafeUpdate = lastSafeUpdate;
    }

    public int getInAirTime() {
        return inAirTime;
    }

    public void setInAirTime(int inAirTime) {
        this.inAirTime = MathUtil.clampInt(inAirTime, 0, 5000);
    }

    public boolean hasSlimeBlockLaunch() {
        return hasSlimeBlockLaunch;
    }

    public void setHasSlimeBlockLaunch(boolean hasSlimeBlockLaunch) {
        this.hasSlimeBlockLaunch = hasSlimeBlockLaunch;
    }

    public Location getFlightDescendingLocation() {
        return flightDescendingLocation;
    }

    public void setFlightDescendingLocation(Location flightDescendingLocation) {
        this.flightDescendingLocation = flightDescendingLocation;
    }

    public long getLastRocketUse() {
        return lastRocketUse;
    }

    public void setLastRocketUse(long lastRocketUse) {
        this.lastRocketUse = lastRocketUse;
    }

    public boolean isTrackingAscending() {
        return trackingAscending;
    }

    public void setTrackingAscending(boolean trackingAscending) {
        this.trackingAscending = trackingAscending;
    }

    public Location getFlightAscendingLocation() {
        return flightAscendingLocation;
    }

    public void setFlightAscendingLocation(Location flightAscendingLocation) {
        this.flightAscendingLocation = flightAscendingLocation;
    }

    public double getRocketDistanceBuffer() {
        return rocketDistanceBuffer;
    }

    public void setRocketDistanceBuffer(double rocketDistanceBuffer) {
        this.rocketDistanceBuffer = rocketDistanceBuffer;
    }

    public double getMaxDescendSpeed() {
        return maxDescendSpeed;
    }

    public void setMaxDescendSpeed(double maxDescendSpeed) {
        this.maxDescendSpeed = maxDescendSpeed;
    }

    public int getNoVerticalMovementAmount() {
        return noVerticalMovementAmount;
    }

    public void setNoVerticalMovementAmount(int noVerticalMovementAmount) {
        this.noVerticalMovementAmount = MathUtil.clampInt(noVerticalMovementAmount, 0, 100);
    }

    public long getLastLevitationEffect() {
        return lastLevitationEffect;
    }

    public void setLastLevitationEffect(long lastLevitationEffect) {
        this.lastLevitationEffect = lastLevitationEffect;
    }

    public boolean wasOnBoat() {
        return wasOnBoat;
    }

    public void setWasOnBoat(boolean wasOnBoat) {
        this.wasOnBoat = wasOnBoat;
    }

    public int getNoGlideTime() {
        return noGlideTime;
    }

    public void setNoGlideTime(int noGlideTime) {
        this.noGlideTime = MathUtil.clampInt(noGlideTime, 0, 100);
    }

    public int getNoResetAscendTime() {
        return noResetAscendTime;
    }

    public void setNoResetAscendTime(int noResetAscendTime) {
        this.noResetAscendTime = MathUtil.clampInt(noResetAscendTime, 0, 1000);
    }

    public int getNoResetDescendTime() {
        return noResetDescendTime;
    }

    public void setNoResetDescendTime(int noResetDescendTime) {
        this.noResetDescendTime = MathUtil.clampInt(noResetDescendTime, 0, 1000);
    }

}
