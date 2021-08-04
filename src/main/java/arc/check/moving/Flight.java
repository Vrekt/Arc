package arc.check.moving;

import arc.Arc;
import arc.check.Check;
import arc.check.result.CheckResult;
import arc.check.timing.CheckTimings;
import arc.check.types.CheckSubType;
import arc.check.types.CheckType;
import arc.data.moving.MovingData;
import arc.exemption.type.ExemptionType;
import arc.utility.MovingAccess;
import arc.utility.api.BukkitAccess;
import arc.utility.block.BlockAccess;
import arc.utility.math.MathUtil;
import bridge.Version;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Checks various vertical movement/flying stuff.
 */
public final class Flight extends Check {

    /**
     * Keeps track of piston push events.
     * Key is a chunk key.
     * Value is a block key.
     * <p>
     * TODO: Probably good enough for now, but entries to be exempt could be purged early.
     */
    private final ConcurrentMap<Long, Long> chunkAndBlockKeys = new ConcurrentHashMap<>();

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
     * <p>
     * Minimum speed boat can fall
     * If player score is greater than this value (the player speed doesn't match expected), flag.
     */
    private double verticalClipMinimum, minBoatDescendSpeed, boatDescendingScoreMin;

    /**
     * The max difference allowed between a=(vertical speed - expected) then, (vertical speed - a)
     */
    private double slimeblockMaxScoreDiff;

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

    /**
     * Default distance a rocket will send you vertically.
     * The min distance needed from the start of your ascent to start checking.
     * The default distance you can fly without boosting from a rocket.
     * The max descend speed allowed with an elytra
     * The modifier to use to calculate the max distance you can fly ascendStart + noRocketDistance + (descendSpeed * modifier)
     */
    private double distanceRocketSendsYou, minDistanceFromAscend, noRocketDistance, maxDescendSpeed, descendSpeedModifier;

    /**
     * Max amount of times a player can not move vertically while flying
     * The time required out of liquid to check ascend
     */
    private int maxFlyingNoVerticalMovement, ascendOutOfLiquidCooldown;

    /**
     * Min = will check and make sure player doesn't fly too high AFTER using a rocket.
     * Max = will check and make sure player doesn't fly too high with no rocket use.
     * <p>
     * How long to exempt for after being pushed by a piston, in milliseconds.
     * The time to wait after the player had levitation to check.
     */
    private long minLastRocketUseTime, maxLastRocketUseTime, pistonExemptionTimeMs, levitationExemptionTimeMs;

    /**
     * The max amount of times the player can have no difference in velocity when falling.
     */
    private int noGlideDifferenceMax;

    /**
     * The minimum time needed to be descending to check glide.
     * The minimum distance away from ground needed to check glide, 0.85 = player jump
     * The max difference allowed between calculated fall velocity and actual fall velocity.
     */
    private double glideDescendTimeMin, glideDescendDistanceMin, glideMaxDifference;

    /**
     * No reset ascend checks if the player is ascending too high.
     * Players previously could bypass regular ascend check by ascending slowly and descending every now and again.
     * This check does not reset the players ascend time if they descend.
     * <p>
     * The distance needed away from ground to start checking a no reset ascend.
     * The max amount of ascending moves allowed.
     */
    private double noResetAscendGroundDistanceThreshold, maxNoResetAscendMoves;

    /**
     * If this check is legacy. (1.8.8)
     */
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

        isLegacy = Arc.getMCVersion() == Version.VERSION_1_8;
        writeGeneralConfiguration();
        writeBoatFlyConfiguration();
        writeElytraFlyConfiguration();
        if (isEnabled()) load();
    }

    /**
     * Write general configuration values
     */
    private void writeGeneralConfiguration() {
        addConfigurationValue("max-jump-distance", 0.42);
        addConfigurationValue("max-climbing-speed-up", 0.12);
        addConfigurationValue("max-climbing-speed-down", 0.151);
        addConfigurationValue("climbing-cooldown", 5);
        addConfigurationValue("max-ascend-time", 7);
        addConfigurationValue("jump-boost-ascend-amplifier", 3);
        addConfigurationValue("ground-distance-threshold", 1.25);
        addConfigurationValue("ground-distance-horizontal-cap", 0.50);
        addConfigurationValue("vertical-clip-vertical-minimum", 0.99);
        addConfigurationValue("safe-location-update-distance-threshold", 1.99);
        addConfigurationValue("max-in-air-hover-time", 6);
        addConfigurationValue("slimeblock-max-score-difference", 0.42);
        addConfigurationValue("piston-exemption-time-ms", 500);
        addConfigurationValue("levitation-exemption-cooldown", 1500);
        addConfigurationValue("ascend-out-of-liquid-cooldown", 3);
        addConfigurationValue("no-glide-difference-max", 2);
        addConfigurationValue("glide-descend-time-min", 5);
        addConfigurationValue("glide-descend-distance-min", 1.6);
        addConfigurationValue("glide-max-difference", 0.010);
        addConfigurationValue("no-reset-ascend-ground-distance-threshold", 1);
        addConfigurationValue("max-no-reset-ascend-moves", 8);
    }

    /**
     * Write boat fly configuration
     */
    private void writeBoatFlyConfiguration() {
        createSubTypeSections(CheckSubType.FLIGHT_BOATFLY);
        addConfigurationValue(CheckSubType.FLIGHT_BOATFLY, "boat-fly-out-of-liquid-time", 5);
        addConfigurationValue(CheckSubType.FLIGHT_BOATFLY, "kick-player-from-boat", true);
        addConfigurationValue(CheckSubType.FLIGHT_BOATFLY, "teleport-player-after-kicked-from-boat", true);
        addConfigurationValue(CheckSubType.FLIGHT_BOATFLY, "min-boat-descend-speed", 0.11);
        addConfigurationValue(CheckSubType.FLIGHT_BOATFLY, "min-boat-descending-time", 5);
        addConfigurationValue(CheckSubType.FLIGHT_BOATFLY, "boat-descending-score-min", 0.15);
    }

    /**
     * Write elytra fly configuration
     */
    private void writeElytraFlyConfiguration() {
        createSubTypeSections(CheckSubType.FLIGHT_ELYTRAFLY);
        addConfigurationValue(CheckSubType.FLIGHT_ELYTRAFLY, "distance-rocket-sends-you", 64);
        addConfigurationValue(CheckSubType.FLIGHT_ELYTRAFLY, "min-last-rocket-use-time", 3500);
        addConfigurationValue(CheckSubType.FLIGHT_ELYTRAFLY, "max-last-rocket-use-time", 5000);
        addConfigurationValue(CheckSubType.FLIGHT_ELYTRAFLY, "min-distance-from-ascend", 3);
        addConfigurationValue(CheckSubType.FLIGHT_ELYTRAFLY, "no-rocket-distance", 10);
        addConfigurationValue(CheckSubType.FLIGHT_ELYTRAFLY, "max-descend-speed", 4);
        addConfigurationValue(CheckSubType.FLIGHT_ELYTRAFLY, "descend-speed-modifier", 15);
        addConfigurationValue(CheckSubType.FLIGHT_ELYTRAFLY, "max-flying-no-vertical-movement", 5);
    }

    /**
     * Check the player
     *
     * @param player the player
     * @param data   their data
     */
    public void check(Player player, MovingData data) {
        if (exempt(player)) return;

        final boolean elytra = BukkitAccess.isFlyingWithElytra(player);
        final CheckResult result = new CheckResult();
        final Location from = data.from();
        final Location to = data.to();
        final Location ground = data.ground();

        final double vertical = data.vertical();
        startTiming(player);

        updateRelevantMovingData(player, data);

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

        final double distanceToGround = MathUtil.vertical(ground, to);

        // check the vertical move of this player.
        if (validVerticalMove && !elytra) {
            final boolean wasMovedByPiston = wasMovedByPiston(player, from);
            if (!hasVerticalModifier) {
                checkVerticalMove(player, data, ground, from, to, vertical, distanceToGround, data.ascendingTime(), wasMovedByPiston, result);
            }
        }

        // check gliding, descending movement.
        if (!player.isInsideVehicle()
                && !data.inLiquid()
                && !data.hasClimbable()
                && !elytra
                && !BukkitAccess.hasSlowFalling(player)) {
            checkGlide(player, data, ground, to, vertical, distanceToGround, result);
        }

        if (data.hasClimbable() && !elytra) {
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

        // don't want to update the current player moving data set
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

        if (BukkitAccess.isFlyingWithElytra(player))
            checkElytraMovement(player, data, from, to, vertical, result);
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
    private void checkEntityMovement(Player player, MovingData data, Location ground, Location from, Location to,
                                     double vertical, int ascendingTime, CheckResult result) {
        if (exempt(player, CheckSubType.FLIGHT_BOATFLY)) return;

        // attempt to find liquid in a large range.
        final boolean findAnyLiquid = data.inLiquid()
                || BlockAccess.hasLiquidAt(to, player.getWorld(), 1, -1, 1);
        final int outOfLiquidTime = findAnyLiquid ? 0 : data.getOutOfLiquidTime();

        // no liquid and we haven't been in any in awhile, check.
        if (!findAnyLiquid
                && outOfLiquidTime >= boatFlyOutOfLiquidTime) {
            // +2 ascending time to be extra safe
            if (data.ascending() && ascendingTime >= 2) {
                // not possible, flag.
                result.setFailed(CheckSubType.FLIGHT_BOATFLY, "Ascending while in a boat")
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
                        result.setFailed(CheckSubType.FLIGHT_BOATFLY, "descending too slow in a boat")
                                .withParameter("vertical", vertical)
                                .withParameter("min", minBoatDescendSpeed);

                        if (kickPlayerFromBoat) player.leaveVehicle();
                        handleCheckViolationAndReset(player, result, teleportAfterKickFromBoat ? ground : null);
                    } else {
                        // TODO: Uhh ?? + Leave for now I guess.
                        final double expected = MathUtil.vertical(from, to) - 0.1;
                        final double score = Math.abs(vertical - expected);

                        if (score >= boatDescendingScoreMin) {
                            // player falling too slow.
                            result.setFailed(CheckSubType.FLIGHT_BOATFLY, "descending too slow in a boat")
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
     * Check elytra flying movements.
     *
     * @param player   the player
     * @param data     their data
     * @param from     the from
     * @param to       the to
     * @param vertical the vertical
     * @param result   the result
     */
    private void checkElytraMovement(Player player, MovingData data, Location from, Location to,
                                     double vertical, CheckResult result) {
        if (exempt(player, CheckSubType.FLIGHT_ELYTRAFLY)) return;

        // check for flying and not descending or ascending.
        if (data.ascending()) {
            final double verticalDelta = Math.abs(vertical - data.lastVertical());
            if (verticalDelta == 0.0) {
                // player not moving vertically.
                data.setNoVerticalMovementAmount(data.getNoVerticalMovementAmount() + 1);
                if (data.getNoVerticalMovementAmount() >= maxFlyingNoVerticalMovement) {
                    result.setFailed(CheckSubType.FLIGHT_ELYTRAFLY, "Not moving vertically while flying")
                            .withParameter("vertical", vertical)
                            .withParameter("last", data.lastVertical())
                            .withParameter("amt", data.getNoVerticalMovementAmount())
                            .withParameter("max", 5);
                    handleCheckViolationAndReset(player, result, from);
                }
            } else {
                data.setNoVerticalMovementAmount(data.getNoVerticalMovementAmount() - 1);
            }

            // player is ascending, set the current start location and return.
            if (!data.isTrackingAscending()) {
                data.setTrackingAscending(true);
                data.setFlightAscendingLocation(to);

                if (data.getLastRocketUse() == 0.0) {
                    // no rocket use, no buffer.
                    data.setRocketDistanceBuffer(0.0);
                } else {
                    data.setRocketDistanceBuffer(distanceRocketSendsYou);
                }

                return;
            }
            final long lastRocket = data.getLastRocketUse();

            // retrieve the distance we have flown upwards since first ascend.
            final double verticalDistanceFromAscend = MathUtil.vertical(data.getFlightAscendingLocation(), to);
            // retrieve time since last boost from a rocket.
            final long delta = System.currentTimeMillis() - lastRocket;

            final double maxDistance = data.getRocketDistanceBuffer();
            final boolean hasLastUse = lastRocket != 0.0;

            // check if the player is ascending too high based on rocket usage
            if (verticalDistanceFromAscend > minDistanceFromAscend && (delta <= minLastRocketUseTime || !hasLastUse)) {
                // player recently used a rocket.
                if (verticalDistanceFromAscend > maxDistance) {
                    result.setFailed(CheckSubType.FLIGHT_ELYTRAFLY, "Flying too high with an elytra")
                            .withParameter("maxDistance", maxDistance)
                            .withParameter("distance", verticalDistanceFromAscend)
                            .withParameter("delta", delta)
                            .withParameter("minDelta", minLastRocketUseTime);
                    // just cancel to from, should make it annoying enough.
                    handleCheckViolationAndReset(player, result, from);
                }
            } else if (verticalDistanceFromAscend > minDistanceFromAscend && delta >= maxLastRocketUseTime) {
                data.setRocketDistanceBuffer(noRocketDistance);
                // hasn't used a rocket in awhile.
                // player.sendMessage("Max: " + data.getMaxDescendSpeed());
                // get the descend speed and times it by a set amount to get an allowed buffer.
                // this can be abused to fly higher, but cap at some point.
                final double max = Math.min(data.getMaxDescendSpeed(), maxDescendSpeed);
                final double modifier = max * descendSpeedModifier;
                final double allowed = data.getRocketDistanceBuffer() + modifier;

                // get a max ceiling the player can go.
                final double ceiling = data.getFlightAscendingLocation().getY() + modifier;
                if (to.getY() > ceiling) {
                    result.setFailed(CheckSubType.FLIGHT_ELYTRAFLY, "Flying too high with no rocket boost")
                            .withParameter("ceil", ceiling)
                            .withParameter("y", to.getY())
                            .withParameter("speed", max)
                            .withParameter("modifier", modifier)
                            .withParameter("allowed", allowed);
                    handleCheckViolationAndReset(player, result, from);
                }

                // flag if too far away in general.
                if (verticalDistanceFromAscend > allowed) {
                    result.setFailed(CheckSubType.FLIGHT_ELYTRAFLY, "Flying too high with no rocket boost")
                            .withParameter("distFromAscend", verticalDistanceFromAscend)
                            .withParameter("allowed", allowed)
                            .withParameter("speed", max)
                            .withParameter("modifier", modifier)
                            .withParameter("allowed", allowed);
                    handleCheckViolationAndReset(player, result, from);
                }
            }
        } else {
            // player is descending, track speed.
            // Could probably be abused, since doesn't reset until ground.
            if (vertical > data.getMaxDescendSpeed()) data.setMaxDescendSpeed(vertical);

            final double verticalDelta = Math.abs(vertical - data.lastVertical());
            if (verticalDelta == 0.0) {
                // player not moving vertically.
                data.setNoVerticalMovementAmount(data.getNoVerticalMovementAmount() + 1);
                if (data.getNoVerticalMovementAmount() >= 5) {
                    result.setFailed(CheckSubType.FLIGHT_ELYTRAFLY, "Not moving vertically while flying")
                            .withParameter("vertical", vertical)
                            .withParameter("last", data.lastVertical())
                            .withParameter("amt", data.getNoVerticalMovementAmount())
                            .withParameter("max", 5);
                    handleCheckViolationAndReset(player, result, from);
                }
            } else {
                data.setNoVerticalMovementAmount(data.getNoVerticalMovementAmount() - 1);
            }
        }
    }

    /**
     * Check the players vertical movement.
     *
     * @param player           the player
     * @param data             their data
     * @param ground           ground location
     * @param from             the from
     * @param to               movedTo
     * @param vertical         vertical
     * @param distance         the distance to ground
     * @param ascendingTime    ascendingTime
     * @param wasMovedByPiston if the player was moved by a piston
     * @param result           result
     */
    private void checkVerticalMove(Player player, MovingData data, Location ground, Location from, Location to,
                                   double vertical, double distance, int ascendingTime, boolean wasMovedByPiston, CheckResult result) {
        if (data.ascending()) {
            // check ground distance.

            final boolean hasSlimeblock = data.hasSlimeblock();
            if (hasSlimeblock && vertical > 0.42 && distance > 1f) {
                data.setHasSlimeBlockLaunch(true);
            }

            // tighter distance check, more moves allowed though, don't use ascend that resets.
            if (distance >= noResetAscendGroundDistanceThreshold) {
                // check ascending moves, no reset.
                final int moves = data.getNoResetAscendTime();

                if (moves >= maxNoResetAscendMoves
                        && !BukkitAccess.hasLevitation(player)
                        && !data.hasSlimeBlockLaunch()
                        && (System.currentTimeMillis() - data.getLastLevitationEffect() >= levitationExemptionTimeMs)) {
                    // we have a few moves here to work with.
                    result.setFailed("Ascending too long")
                            .withParameter("distance", distance)
                            .withParameter("moves", moves)
                            .withParameter("max", maxNoResetAscendMoves);
                    handleCheckViolationAndReset(player, result, ground);
                }
            }

            if (distance >= groundDistanceThreshold) {
                // high off ground (hopefully) check.
                // make sure we are within the limits of the ground.
                // we don't want a flag when the player is wildly jumping around.
                final double hDist = MathUtil.horizontal(ground, to);
                if (ascendingTime >= 5
                        && hDist < groundDistanceHorizontalCap
                        && !hasSlimeblock
                        && !data.hasSlimeBlockLaunch()
                        && !BukkitAccess.hasLevitation(player)
                        && (System.currentTimeMillis() - data.getLastLevitationEffect() >= levitationExemptionTimeMs)) {
                    result.setFailed("Vertical distance from ground greater than allowed within limits.")
                            .withParameter("distance", distance)
                            .withParameter("threshold", groundDistanceThreshold)
                            .withParameter("hDist", hDist)
                            .withParameter("cap", groundDistanceHorizontalCap);
                    handleCheckViolationAndReset(player, result, ground);
                }
            }

            // ensure we didn't walk up a block that modifies your vertical
            double maxJumpHeight = getJumpHeight(player);

            if (hasSlimeblock) {
                // player was launched, set state
                final boolean launched = data.hasSlimeBlockLaunch();
                if (launched) {
                    // get rough estimate of player fall distance
                    final double fallen = MathUtil.vertical(data.getFlightDescendingLocation() == null ? from
                            : data.getFlightDescendingLocation(), from);
                    if (data.getFlightDescendingLocation() != null) {
                        // decrease the modifier if we fall from a significant height, +
                        // to prevent one time abuse
                        final double modifier = fallen > 15 ? 0.11D : 0.18d;
                        final double rough = 0.4D + fallen * modifier;
                        final double diff = Math.abs(vertical - rough);
                        final double score = vertical - diff;
                        if (score >= slimeblockMaxScoreDiff) {
                            result.setFailed("Bounced too high from a slimeblock")
                                    .withParameter("vertical", vertical)
                                    .withParameter("rough", rough)
                                    .withParameter("diff", diff)
                                    .withParameter("score", score)
                                    .withParameter("max", slimeblockMaxScoreDiff);
                            handleCheckViolationAndReset(player, result, ground);
                        }
                    }

                    // player is jumping really high with no velocity
                    if (distance <= 1f && vertical > maxJumpHeight && fallen <= 1f) {
                        result.setFailed("Vertical greater than max jump height on slimeblock")
                                .withParameter("groundDistance", distance)
                                .withParameter("vertical", vertical)
                                .withParameter("max", maxJumpHeight);
                        handleCheckViolationAndReset(player, result, ground);
                    }
                }
            }

            // cooldown for levitation and after levitation since we have high vertical possible.

            final boolean boat = MovingAccess.isOnBoat(player);
            // boat modifier, slightly higher step height.
            if (boat) maxJumpHeight += 0.0175;

            if (vertical > maxJumpHeight
                    && !hasSlimeblock
                    && !data.hasSlimeBlockLaunch()
                    && !wasMovedByPiston
                    && !BukkitAccess.hasLevitation(player)
                    && System.currentTimeMillis() - data.getLastLevitationEffect() >= levitationExemptionTimeMs
                    && !boat
                    && !data.wasOnBoat()) {
                result.setFailed("Vertical move greater than max jump height.")
                        .withParameter("vertical", vertical)
                        .withParameter("max", maxJumpHeight);
                handleCheckViolationAndReset(player, result, from);
            }

            // update after
            data.setWasOnBoat(boat);

            // add to our modifier if we have a jump effect.
            // this will need to be amplified by the amplifier.
            final int modifier = player.hasPotionEffect(PotionEffectType.JUMP)
                    ? BukkitAccess.getPotionEffect(player, PotionEffectType.JUMP).getAmplifier()
                    + jumpBoostAscendAmplifier : 0;
            if (ascendingTime > (maxAscendTime + modifier)
                    && !data.hadClimbable()
                    && !data.hasSlimeBlockLaunch()
                    && !BukkitAccess.hasLevitation(player)
                    && (System.currentTimeMillis() - data.getLastLevitationEffect() >= levitationExemptionTimeMs)
                    && data.getOutOfLiquidTime() >= ascendOutOfLiquidCooldown) {
                result.setFailed("Ascending for too long")
                        .withParameter("vertical", vertical)
                        .withParameter("time", data.ascendingTime())
                        .withParameter("max", (maxAscendTime + modifier));
                handleCheckViolationAndReset(player, result, from);
            }
        }
    }

    /**
     * Generally check player gliding and flight movements.
     *
     * @param player   the player
     * @param data     their data
     * @param ground   ground location
     * @param to       to
     * @param vertical vertical
     * @param distance the distance to ground
     * @param result   result
     */
    private void checkGlide(Player player, MovingData data, Location ground, Location to, double vertical, double distance, CheckResult result) {

        // first, basic glide check
        // ensure player is actually moving down when off the ground here.
        if (!data.onGround() && !data.ascending()) {
            // calculate how we moved since last time.
            final double delta = Math.abs(vertical - data.lastVertical());

            // player hasn't moved, increase 'time'
            if (vertical == 0.0 || delta == 0.0) {
                data.setNoGlideTime(data.getNoGlideTime() + 1);
            } else {
                // decrease / reward
                data.setNoGlideTime(data.getNoGlideTime() - 1);
            }

            if (data.getNoGlideTime() > noGlideDifferenceMax) {
                result.setFailed("No vertical difference while off ground")
                        .withParameter("vertical", vertical)
                        .withParameter("last", data.lastVertical())
                        .withParameter("delta", delta)
                        .withParameter("time", data.getNoGlideTime())
                        .withParameter("max", noGlideDifferenceMax);
                handleCheckViolationAndReset(player, result, ground);
            }

            // next, calculate how we should be falling.
            // ensure we have been falling though, and have at-least decent distance.
            // Check horizontal distance as-well since its possible to glide pretty far
            // before hitting the vertical distance required.
            if (data.getNoResetDescendTime() >= glideDescendTimeMin
                    && ((distance >= glideDescendDistanceMin)
                    || MathUtil.horizontal(ground, data.to()) >= glideDescendDistanceMin)) {

                final int time = data.getInAirTime();

                // meant to stop increasing the overall expected after a certain distance
                // sort of like a reset.
                final double mod = time <= 50 ? 0.000006 :
                        MathUtil.vertical(data.getFlightDescendingLocation(), to) >= 50 ? 0.00000456
                                : 0.000006;

                final double expected = mod * Math.pow(data.getInAirTime(), 2) - 0.0011 * data.getInAirTime() + 0.077;
                final double difference = expected - delta;

                if (difference > glideMaxDifference) {
                    result.setFailed("Gliding delta not expected")
                            .withParameter("delta", delta)
                            .withParameter("e", expected)
                            .withParameter("diff", difference)
                            .withParameter("max", glideMaxDifference);
                    handleCheckViolationAndReset(player, result, ground);
                }
            }
        }
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
    private void checkClimbingMovement(Player player, MovingData data, Location from, double vertical,
                                       double cooldown, CheckResult result) {
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

    /**
     * Update moving data for check
     *
     * @param player the player
     * @param data   their data
     */
    private void updateRelevantMovingData(Player player, MovingData data) {
        if (data.onGround()) {
            if (!data.hasSlimeblock()
                    && data.hasSlimeBlockLaunch()) {
                data.setHasSlimeBlockLaunch(false);
            }
            data.setTrackingAscending(false);
            data.setMaxDescendSpeed(0.0);
            data.setNoVerticalMovementAmount(0);

            // slowly decrease the time here.
            // if start seeing false positives, reset 0 instead.
            data.setNoGlideTime(data.getNoGlideTime() - 1);
            data.setGlideDescendFlags(0);
        } else {
            if (data.descending()) {
                data.setTrackingAscending(false);
                data.setHasSlimeBlockLaunch(false);

                // we just started descending, set.
                if (data.descendingTime() == 1) data.setFlightDescendingLocation(data.from());
            }

            // reset ascending time if we have potion effects
            if (BukkitAccess.hasLevitation(player)) {
                data.ascendingTime(0);
                data.setNoResetAscendTime(0);
                data.setLastLevitationEffect(System.currentTimeMillis());
            }
        }
    }

    /**
     * Check if the player was moved by a piston, or they are previously exempt.
     *
     * @param player the player
     * @param from   from
     * @return {@code true} if exempt or moved by piston.
     */
    private boolean wasMovedByPiston(Player player, Location from) {
        final Block block = from.getBlock().getRelative(BlockFace.DOWN);
        final long chunkKey = BukkitAccess.getChunkKey(block.getChunk());

        boolean wasMovedByPiston = false;
        // see if we had a recent piston event matching this players' location.
        if (chunkAndBlockKeys.containsKey(chunkKey)) {
            wasMovedByPiston = chunkAndBlockKeys.get(chunkKey)
                    == BukkitAccess.getBlockKey(block);

            chunkAndBlockKeys.remove(chunkKey);
        }

        // add this to the exemptions if we were pushed.
        if (wasMovedByPiston) {
            // exempt for 10 ticks.
            Arc.getInstance().getExemptionManager().addExemption(player, ExemptionType.PISTON, pistonExemptionTimeMs);
        }

        return exempt(player, ExemptionType.PISTON) || wasMovedByPiston;
    }

    /**
     * Record a piston event
     *
     * @param block the block
     */
    public void recordPistonEvent(Block block) {
        // purge if too many events.
        if (this.chunkAndBlockKeys.size() >= 100) chunkAndBlockKeys.clear();
        this.chunkAndBlockKeys.put(BukkitAccess.getChunkKey(block.getChunk()), BukkitAccess.getBlockKey(block));
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        loadGeneralConfiguration();
        if (!isLegacy) loadBoatFlyConfiguration();
        if (!isLegacy) loadElytraFlyConfiguration();
        CheckTimings.registerTiming(checkType);
    }

    /**
     * Load general configuration
     */
    private void loadGeneralConfiguration() {
        maxJumpDistance = configuration.getDouble("max-jump-distance");
        maxClimbSpeedUp = configuration.getDouble("max-climbing-speed-up");
        maxClimbSpeedDown = configuration.getDouble("max-climbing-speed-down");
        climbingCooldown = configuration.getDouble("climbing-cooldown");
        maxAscendTime = configuration.getInt("max-ascend-time");
        jumpBoostAscendAmplifier = configuration.getInt("jump-boost-ascend-amplifier");
        groundDistanceThreshold = configuration.getDouble("ground-distance-threshold");
        groundDistanceHorizontalCap = configuration.getDouble("ground-distance-horizontal-cap");
        verticalClipMinimum = configuration.getDouble("vertical-clip-vertical-minimum");
        maxInAirHoverTime = configuration.getInt("max-in-air-hover-time");
        slimeblockMaxScoreDiff = configuration.getDouble("slimeblock-max-score-difference");
        pistonExemptionTimeMs = configuration.getLong("piston-exemption-time-ms");
        levitationExemptionTimeMs = configuration.getInt("levitation-exemption-cooldown");
        ascendOutOfLiquidCooldown = configuration.getInt("ascend-out-of-liquid-cooldown");
        noGlideDifferenceMax = configuration.getInt("no-glide-difference-max");
        glideDescendTimeMin = configuration.getInt("glide-descend-time-min");
        glideDescendDistanceMin = configuration.getDouble("glide-descend-distance-min");
        glideMaxDifference = configuration.getDouble("glide-max-difference");
        noResetAscendGroundDistanceThreshold = configuration.getDouble("no-reset-ascend-ground-distance-threshold");
        maxNoResetAscendMoves = configuration.getInt("max-no-reset-ascend-moves");
    }

    /**
     * Load boat fly configuration
     */
    private void loadBoatFlyConfiguration() {
        final ConfigurationSection configuration = this.configuration.getSubType(CheckSubType.FLIGHT_BOATFLY);
        boatFlyOutOfLiquidTime = configuration.getInt("boat-fly-out-of-liquid-time");
        kickPlayerFromBoat = configuration.getBoolean("kick-player-from-boat");
        teleportAfterKickFromBoat = configuration.getBoolean("teleport-player-after-kicked-from-boat");
        minBoatDescendSpeed = configuration.getDouble("min-boat-descend-speed");
        boatDescendingTime = configuration.getInt("min-boat-descending-time");
        boatDescendingScoreMin = configuration.getDouble("boat-descending-score-min");
    }

    /**
     * Load elytra fly config
     */
    private void loadElytraFlyConfiguration() {
        final ConfigurationSection configuration = this.configuration.getSubType(CheckSubType.FLIGHT_ELYTRAFLY);
        distanceRocketSendsYou = configuration.getDouble("distance-rocket-sends-you");
        minLastRocketUseTime = configuration.getLong("min-last-rocket-use-time");
        maxLastRocketUseTime = configuration.getLong("max-last-rocket-use-time");
        minDistanceFromAscend = configuration.getDouble("min-distance-from-ascend");
        noRocketDistance = configuration.getDouble("no-rocket-distance");
        maxDescendSpeed = configuration.getDouble("max-descend-speed");
        descendSpeedModifier = configuration.getDouble("descend-speed-modifier");
        maxFlyingNoVerticalMovement = configuration.getInt("max-flying-no-vertical-movement");
    }

}
