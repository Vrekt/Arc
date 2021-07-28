package arc.check.moving;

import arc.Arc;
import arc.check.Check;
import arc.check.result.CheckResult;
import arc.check.timing.CheckTimings;
import arc.check.types.CheckType;
import arc.data.moving.MovingData;
import arc.utility.MovingAccess;
import arc.utility.api.BukkitAccess;
import arc.utility.block.BlockAccess;
import arc.utility.math.MathUtil;
import bridge.Version;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;

/**
 * Checks various vertical movement/flying stuff.
 * TODO: calculate ground distance
 * TODO: Flag when falling onto ladder.
 */
public final class Flight extends Check {

    /**
     * The max jump distance.
     * Max climbing speeds
     * The amount of time a player has to be on a climbable
     * <p>
     * The distance (from the ground) required to start checking ascending stuff.
     * The distance (from the ground) (horizontal) that is capped, if the hDist > capped, no check is executed.
     */
    private double maxJumpDistance, maxClimbSpeedUp, maxClimbSpeedDown, climbingCooldown, groundDistanceThreshold, groundDistanceHorizontalCap;

    /**
     * The minimum distance required to move to check vclip.
     * The minimum distance required to update the players safe location.
     * <p>
     * Minimum speed boat can fall
     * If player score is greater than this value (the player speed doesn't match expected), flag.
     */
    private double verticalClipMinimum, safeDistanceUpdateThreshold, minBoatDescendSpeed, boatDescendingScoreMin;

    /**
     * The max ascend time
     * The amount to add to {@code maxAscendTime} when the player has jump boost.
     * Max time allowed to be hovering.
     * <p>
     * The time to wait being out of liquid before checking boat fly
     * Minimum time to wait before checking boat descending
     */
    private int maxAscendTime, jumpBoostAscendAmplifier, maxInAirHoverTime, boatFlyOutOfLiquidTime, boatDescendingTime;

    /**
     * If the player should be kicked from the boat on flag.
     * If the player should be teleported to the ground.
     */
    private boolean kickPlayerFromBoat, teleportAfterKickFromBoat;

    private final boolean isLegacy;

    public Flight() {
        super(CheckType.FLIGHT);
        isEnabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("max-jump-distance", 0.42);
        addConfigurationValue("max-climbing-speed-up", 0.12);
        addConfigurationValue("max-climbing-speed-down", 0.151);
        addConfigurationValue("climbing-cooldown", 5);
        addConfigurationValue("max-ascend-time", 7);
        addConfigurationValue("jump-boost-ascend-amplifier", 3);
        addConfigurationValue("ground-distance-threshold", 1.25);
        addConfigurationValue("ground-distance-horizontal-cap", 0.50);
        addConfigurationValue("slime-block-distance-fallen-threshold", 0);
        addConfigurationValue("vertical-clip-vertical-minimum", 0.99);
        addConfigurationValue("safe-location-update-distance-threshold", 1.99);
        addConfigurationValue("max-in-air-hover-time", 6);

        addConfigurationValue("boat-fly-out-of-liquid-time", 5);
        addConfigurationValue("kick-player-from-boat", true);
        addConfigurationValue("teleport-player-after-kicked-from-boat", true);
        addConfigurationValue("min-boat-descend-speed", 0.11);
        addConfigurationValue("min-boat-descending-time", 5);
        addConfigurationValue("boat-descending-score-min", 0.15);

        isLegacy = Arc.getMCVersion() == Version.VERSION_1_8;

        if (isEnabled()) load();
    }

    /**
     * Check the player
     *
     * @param player the player
     * @param data   their data
     */
    public void check(Player player, MovingData data) {
        if (exempt(player)) return;

        // TODO: Elytra support later, for now ignore gliding players.
        if (BukkitAccess.isFlyingWithElytra(player)) {
            return;
        }

        final CheckResult result = new CheckResult();
        final Location from = data.from();
        final Location to = data.to();
        final Location ground = data.ground();
        final Location safe = data.getSafeLocation();

        final double vertical = data.vertical();
        startTiming(player);

        // check if the player is on skulls, slabs, stairs, fences, gates, beds, etc.
        final boolean hasVerticalModifier = BlockAccess.hasVerticalModifierAt(to, to.getWorld(), 0.3, -0.1, 0.3)
                || BlockAccess.hasVerticalModifierAt(to, to.getWorld(), 0.5, -1, 0.5);

        // check if its a valid vertical move.
        // TODO: We need a better way to determine
        // TODO: If the player walked up a slab.
        // TODO: Currently, this method allows the player
        // TODO: To fly higher than normal on ground (if they are on a slab)
        final boolean validVerticalMove = vertical > 0.0
                && !player.isInsideVehicle()
                && !data.inLiquid()
                && !data.hasClimbable();

        // check the vertical move of this player.
        if (validVerticalMove) {
            // check vertical clip regardless of ascending or descending state.
            // return here since we don't want the rest of the check interfering with setback.
            final boolean failed = checkIfMovedThroughSolidBlock(player, result, safe, from, to, vertical);
            if (!hasVerticalModifier && !failed) {
                checkVerticalMove(player, data, ground, from, to, vertical, data.ascendingTime(), result);
            }
        }

        if (data.hasClimbable()) {
            // the ascending cooldown, will need to be reversed if descending.
            final double cooldown = climbingCooldown - (vertical * 2);
            if (data.climbTime() >= cooldown) checkClimbingMovement(player, data, from, vertical, cooldown, result);
        }

        // update safe location if not failed and on ground.
        if (!result.hasFailedBefore()
                && data.onGround()) {
            data.setSafeLocation(to);
            data.setLastSafeUpdate(System.currentTimeMillis());
        }

        if (!isLegacy) runNonLegacyChecks(player, data, ground, from, to, vertical, data.ascendingTime(), result);

        debug(player, "Vertical: " + vertical);
        stopTiming(player);
    }

    /**
     * Check the player when they haven't moved in awhile.
     * <p>
     * As of right now this is mostly a hover check.
     * <p>
     * TODO: Might not need a temporary data set.
     *
     * @param player the player
     * @param data   the data
     */
    public void checkNoMovement(Player player, MovingData data) {
        if (exempt(player)) return;

        // don't wanna update the current player moving data set
        // in-case it causes issues else-where, so for now use a temporary one
        // to retrieve stuff we need right now
        final MovingData temp = MovingData.retrieveTemporary();
        MovingAccess.updatePlayerMovingData(temp, data.to(), player.getLocation());

        // update in-air time here since we're not moving or calculating movement.
        int inAirTime = data.getInAirTime();
        if (!temp.onGround()) {
            data.setInAirTime(data.getInAirTime() + 1);
            inAirTime++;
        } else {
            data.setInAirTime(0);
        }

        if (!temp.onGround() && temp.vertical() == 0.0 && !MovingAccess.isOnBoat(player)) {
            // player is hovering
            if (inAirTime >= maxInAirHoverTime) {

                player.sendMessage("BOAT: " + MovingAccess.isOnBoat(player));

                // flag player, hovering too long.
                final CheckResult result = new CheckResult();
                result.setFailed("Hovering off the ground for too long")
                        .withParameter("inAirTime", inAirTime)
                        .withParameter("max", maxInAirHoverTime);

                handleCheckViolation(player, result, data.ground());
            }
        }

    }

    /**
     * Run checks are meant for versions newer than 1.8
     *
     * @param player        the player
     * @param data          their data
     * @param ground        ground location
     * @param from          the from
     * @param to            movedTo
     * @param vertical      vertical
     * @param ascendingTime ascendingTime
     * @param result        result
     */
    private void runNonLegacyChecks(Player player, MovingData data, Location ground, Location from, Location to, double vertical, int ascendingTime, CheckResult result) {
        if (player.isInsideVehicle() && player.getVehicle() instanceof Boat)
            checkEntityMovement(player, data, ground, from, to, vertical, ascendingTime, result);
    }

    /**
     * Checks entity related movement.
     * <p>
     * Meant to block boat fly in 1.9+ newer versions.
     *
     * @param player        the player
     * @param data          their data
     * @param ground        ground location
     * @param from          the from
     * @param to            movedTo
     * @param vertical      vertical
     * @param ascendingTime ascendingTime
     * @param result        result
     */
    private void checkEntityMovement(Player player, MovingData data, Location ground, Location from, Location to, double vertical, int ascendingTime, CheckResult result) {
        // attempt to find liquid in a large range.
        final boolean findAnyLiquid = data.inLiquid() || BlockAccess.hasLiquidAt(to, player.getWorld(), 1, -1, 1);

        final int outOfLiquidTime = findAnyLiquid ? 0 : data.getOutOfLiquidTime() + 1;
        data.setOutOfLiquidTime(outOfLiquidTime);
        // no liquid and we haven't been in any in awhile, check.
        if (!findAnyLiquid
                && outOfLiquidTime >= boatFlyOutOfLiquidTime) {
            // +2 ascending time to be extra safe
            if (data.ascending() && ascendingTime >= 2) {
                // not possible, flag.
                result.setFailed("Ascending while in a boat")
                        .withParameter("outOfLiquidTime", outOfLiquidTime)
                        .withParameter("max", boatFlyOutOfLiquidTime)
                        .withParameter("ascendingTime", ascendingTime)
                        .withParameter("max", 2);

                if (kickPlayerFromBoat) player.leaveVehicle();
                handleCheckViolationAndReset(player, result, teleportAfterKickFromBoat ? ground : null);
            }

            // check if player is falling too slow in boat.
            final Boat boat = (Boat) player.getVehicle();
            if (boat != null) {
                final boolean boatOnGround = MovingAccess.onGround(boat.getLocation());

                // ensure we are descending for a decent amount of time first.
                if (data.descending() && data.descendingTime() >= boatDescendingTime && !boatOnGround) {
                    if (vertical < minBoatDescendSpeed) {
                        // too slow regardless, flag
                        result.setFailed("descending too slow in a boat")
                                .withParameter("vertical", vertical)
                                .withParameter("min", minBoatDescendSpeed);

                        if (kickPlayerFromBoat) player.leaveVehicle();
                        handleCheckViolationAndReset(player, result, teleportAfterKickFromBoat ? ground : null);
                    } else {
                        final double expected = MathUtil.vertical(from, to) - 0.1;
                        final double score = Math.abs(vertical - expected);

                        if (score >= boatDescendingScoreMin) {
                            // player falling too slow.
                            result.setFailed("descending too slow in a boat")
                                    .withParameter("vertical", vertical)
                                    .withParameter("expected", expected)
                                    .withParameter("score", score)
                                    .withParameter("min", boatDescendingScoreMin);

                            if (kickPlayerFromBoat) player.leaveVehicle();
                            handleCheckViolationAndReset(player, result, teleportAfterKickFromBoat ? ground : null);
                        }
                    }
                }
            }
        }
    }

    /**
     * Check the players vertical movement.
     *
     * @param player        the player
     * @param data          their data
     * @param ground        ground location
     * @param from          the from
     * @param to            movedTo
     * @param vertical      vertical
     * @param ascendingTime ascendingTime
     * @param result        result
     */
    private void checkVerticalMove(Player player, MovingData data, Location ground, Location from, Location to, double vertical, int ascendingTime, CheckResult result) {
        if (data.ascending()) {
            // check ground distance.
            final double distance = MathUtil.vertical(ground, to);
            if (distance >= groundDistanceThreshold) {
                // high off ground (hopefully) check.
                // make sure we are within the limits of the ground.
                // we don't want a flag when the player is wildly jumping around.
                final double hDist = MathUtil.horizontal(ground, to);
                if (ascendingTime >= 5 && hDist < groundDistanceHorizontalCap) {
                    result.setFailed("Vertical distance from ground greater than allowed within limits.")
                            .withParameter("distance", distance)
                            .withParameter("threshold", groundDistanceThreshold)
                            .withParameter("hDist", hDist)
                            .withParameter("cap", groundDistanceHorizontalCap);
                    handleCheckViolationAndReset(player, result, ground);
                }
            }

            // TODO: check if we have launch from a slime-block.
            // ensure we didn't walk up a block that modifies your vertical
            final double maxJumpHeight = getJumpHeight(player);

            // go back to where we were.
            // maybe ground later.
            if (vertical > maxJumpHeight) {
                result.setFailed("Vertical move greater than max jump height.")
                        .withParameter("vertical", vertical)
                        .withParameter("max", maxJumpHeight);
                handleCheckViolationAndReset(player, result, from);
            }

            // add to our modifier if we have a jump effect.
            // this will need to be amplified by the amplifier.
            final int modifier = player.hasPotionEffect(PotionEffectType.JUMP)
                    ? BukkitAccess.getPotionEffect(player, PotionEffectType.JUMP).getAmplifier()
                    + jumpBoostAscendAmplifier : 0;
            if (ascendingTime > (maxAscendTime + modifier) && !data.hadClimbable()) {
                result.setFailed("Ascending for too long")
                        .withParameter("vertical", vertical)
                        .withParameter("time", data.ascendingTime())
                        .withParameter("max", (maxAscendTime + modifier));
                handleCheckViolationAndReset(player, result, from);
            }
        }
    }

    /**
     * Check if the player moved vertically through a solid block.
     *
     * @param player   the player
     * @param result   the result
     * @param safe     the safe location
     * @param from     the from
     * @param to       the to
     * @param vertical the vertical
     * @return {@code true} if the player moved through a solid block.
     */
    private boolean checkIfMovedThroughSolidBlock(Player player, CheckResult result, Location safe, Location from, Location to, double vertical) {
        if (vertical >= verticalClipMinimum) {
            // safe
            final double min1 = Math.min(safe.getY(), to.getY());
            final double max1 = Math.max(safe.getY(), to.getY()) + 1;

            // from
            final double min2 = Math.min(from.getY(), to.getY());
            final double max2 = Math.max(from.getY(), to.getY()) + 1;

            if (hasSolidBlockBetween(min1, max1, player.getWorld(), safe)) {
                result.setFailed("Attempted to move through a block")
                        .withParameter("vertical", vertical)
                        .withParameter("min", verticalClipMinimum)
                        .withParameter("safe", true);
                return handleCheckViolationAndReset(player, result, safe);
            } else if (hasSolidBlockBetween(min2, max2, player.getWorld(), from)) {
                result.setFailed("Attempted to move through a block")
                        .withParameter("vertical", vertical)
                        .withParameter("min", verticalClipMinimum)
                        .withParameter("from", true);
                return handleCheckViolationAndReset(player, result, from);
            }
        }

        return false;
    }

    /**
     * Check if there is a solid block between the coordinates.
     *
     * @param min    the min
     * @param max    the max
     * @param world  the world
     * @param origin the origin
     * @return the result
     */
    private boolean hasSolidBlockBetween(double min, double max, World world, Location origin) {
        for (double y = min; y <= max + 1; y++) {
            if (BlockAccess.isConsideredGround(world.getBlockAt(
                    origin.getBlockX(),
                    NumberConversions.floor(y),
                    origin.getBlockZ()))) return true;
        }

        return false;
    }

    /**
     * Check climbing movement.
     *
     * @param player   the player
     * @param data     the data
     * @param from     the from
     * @param vertical the vertical
     * @param cooldown the cooldown time
     * @param result   the result
     */
    private void checkClimbingMovement(Player player, MovingData data, Location from, double vertical, double cooldown, CheckResult result) {
        final double modifiedCooldown = data.ascending() ? cooldown : (climbingCooldown) + (vertical * 2);
        final double max = data.ascending() ? maxClimbSpeedUp : maxClimbSpeedDown;
        final int time = data.ascending() ? data.ascendingTime() : data.descendingTime();

        if (time >= modifiedCooldown && vertical > max) {
            result.setFailed("Climbing a ladder too fast")
                    .withParameter("vertical", vertical)
                    .withParameter("max", max)
                    .withParameter("cooldown", modifiedCooldown)
                    .withParameter("ascending", data.ascending());
            handleCheckViolationAndReset(player, result, from);
        }
    }

    /**
     * Retrieve the jump height.
     *
     * @param player the player
     * @return the jump height.
     */
    private double getJumpHeight(Player player) {
        double current = maxJumpDistance;
        if (player.hasPotionEffect(PotionEffectType.JUMP)) {
            current += (0.4 * BukkitAccess.getPotionEffect(player, PotionEffectType.JUMP).getAmplifier());
        }
        return current;
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        maxJumpDistance = configuration.getDouble("max-jump-distance");
        maxClimbSpeedUp = configuration.getDouble("max-climbing-speed-up");
        maxClimbSpeedDown = configuration.getDouble("max-climbing-speed-down");
        climbingCooldown = configuration.getDouble("climbing-cooldown");
        maxAscendTime = configuration.getInt("max-ascend-time");
        jumpBoostAscendAmplifier = configuration.getInt("jump-boost-ascend-amplifier");
        groundDistanceThreshold = configuration.getDouble("ground-distance-threshold");
        groundDistanceHorizontalCap = configuration.getDouble("ground-distance-horizontal-cap");
        verticalClipMinimum = configuration.getDouble("vertical-clip-vertical-minimum");
        safeDistanceUpdateThreshold = configuration.getDouble("safe-location-update-distance-threshold");
        maxInAirHoverTime = configuration.getInt("max-in-air-hover-time");
        boatFlyOutOfLiquidTime = configuration.getInt("boat-fly-out-of-liquid-time");
        kickPlayerFromBoat = configuration.getBoolean("kick-player-from-boat");
        teleportAfterKickFromBoat = configuration.getBoolean("teleport-player-after-kicked-from-boat");
        minBoatDescendSpeed = configuration.getDouble("min-boat-descend-speed");
        boatDescendingTime = configuration.getInt("min-boat-descending-time");
        boatDescendingScoreMin = configuration.getDouble("boat-descending-score-min");

        CheckTimings.registerTiming(checkType);
    }
}
