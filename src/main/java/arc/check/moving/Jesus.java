package arc.check.moving;

import arc.check.Check;
import arc.check.result.CheckResult;
import arc.check.timing.CheckTimings;
import arc.check.types.CheckType;
import arc.data.moving.MovingData;
import arc.utility.math.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Checks if the player is walking on water.
 * TODO: Players can toggle jesus and jump around on water.
 * TODO: May be covered by flight check later on.
 */
public final class Jesus extends Check {

    /**
     * The time in liquid required to start checking.
     * The time in liquid required to distance check.
     */
    private int timeInLiquidRequired, timeInLiquidRequiredDistanceChecking;

    /**
     * The amount of times allowed where the vertical distance hasn't changed.
     */
    private int maxNoDistanceChangeAllowed;

    /**
     * The max setback distance
     * The min distance required to start checking
     * The min distance allowed (last water distance - current water distance) when ascending.
     */
    private double maxSetbackDistance, ascendingMinDistanceRequired, ascendingMinDifferenceDistance;

    public Jesus() {
        super(CheckType.JESUS);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("time-in-liquid-required", 3);
        addConfigurationValue("max-setback-distance", 2);
        addConfigurationValue("time-ascending-required", 1);
        addConfigurationValue("time-in-liquid-required-distance-checking", 3);
        addConfigurationValue("max-no-distance-change-allowed", 3);
        addConfigurationValue("ascending-min-distance-required", 0.12);
        addConfigurationValue("ascending-min-difference-distance", 0.05);

        if (enabled()) load();
    }

    /**
     * Check this player
     *
     * @param player the player
     * @param data   their data
     */
    public void check(Player player, MovingData data) {
        if (exempt(player)) return;

        startTiming(player);
        final Location to = data.to();
        final boolean liquid = data.inLiquid();
        // reset data if in liquid
        if (!liquid) {
            data.liquidTime(0);
            data.waterLocation(null);
            data.noDistanceChanges(0);
        }

        if (liquid && !data.onGround() && !player.isInsideVehicle()) {
            // retrieve whats needed to check.
            final CheckResult result = new CheckResult();
            final boolean clientGround = data.clientOnGround();
            final double vertical = Math.floor((data.vertical()) * 100) / 100;
            final int liquidTime = data.liquidTime() + 1;
            data.liquidTime(liquidTime);

            // make sure we have liquid for awhile.
            if (liquidTime > timeInLiquidRequired) {
                Location water = data.waterLocation();
                if (water == null) {
                    water = to;
                    data.waterLocation(water);
                }

                // basic check, if on ground and no vertical, flag.
                if (clientGround && vertical == 0.0) {
                    result.setFailed("Client on ground and vertical is 0.0")
                            .withParameter("clientGround", true)
                            .withParameter("vertical", vertical);
                } else {
                    // alternative check for other types
                    final double distance = Math.floor(MathUtil.vertical(water, to) * 100) / 100;
                    final double lastDistance = data.lastWaterDistance();
                    data.lastWaterDistance(distance);

                    // make sure we are ascending
                    // we want to be ascending but not for too long.
                    // make sure we have a lower water distance
                    if (data.ascending() && data.ascendingTime() < 10 && distance <= ascendingMinDistanceRequired) {
                        // calculate the difference.
                        // this check works by comparing the distances moved vertically
                        // within the water.
                        // storing the first water location will allow us to monitor how the player has been moving
                        // basically, if we haven't been moving that much (distance<=ascendingMinDistanceRequired)
                        // then, that's odd already, since vanilla has much more crazy vertical movements
                        final double difference = Math.abs(distance - lastDistance);
                        if (difference <= ascendingMinDifferenceDistance) {
                            result.setFailed("Odd ascending behaviour")
                                    .withParameter("ascendingTime", data.ascendingTime())
                                    .withParameter("maxAscendingTime", 10)
                                    .withParameter("waterDistance", distance)
                                    .withParameter("lastWaterDistance", lastDistance)
                                    .withParameter("difference", difference)
                                    .withParameter("min", ascendingMinDifferenceDistance);
                        }
                    }

                    // another alternative check
                    // this one is similar, if the vertical is 0.0
                    // after being the water awhile, flag.
                    // could maybe cause 1 or 2 false positives.
                    int noDistanceChanges = data.noDistanceChanges();
                    if (distance == 0.0) noDistanceChanges++;
                    data.noDistanceChanges(noDistanceChanges);

                    if (!result.failed()
                            && liquidTime >= timeInLiquidRequiredDistanceChecking
                            && distance == 0.0
                            && noDistanceChanges >= maxNoDistanceChangeAllowed) {
                        result.setFailed("vertical distance has not changed overtime")
                                .withParameter("liquidTime", liquidTime)
                                .withParameter("required", timeInLiquidRequiredDistanceChecking)
                                .withParameter("noDistanceChanges", noDistanceChanges)
                                .withParameter("max", maxNoDistanceChangeAllowed)
                                .withParameter("distance", 0.0);
                    }
                }

                // teleport our player back to a desirable location, if they failed.
                if (checkViolation(player, result)) {
                    if (data.ground() != null) {
                        final double distance = MathUtil.distance(data.ground(), data.to());
                        setbackPlayer(player, distance > maxSetbackDistance ? data.from() : data.ground());
                    } else {
                        setbackPlayer(player, data.from());
                    }
                }
            }
        }

        stopTiming(player);
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        timeInLiquidRequired = configuration.getInt("time-in-liquid-required");
        maxSetbackDistance = configuration.getDouble("max-setback-distance");
        timeInLiquidRequiredDistanceChecking = configuration.getInt("time-in-liquid-required-distance-checking");
        maxNoDistanceChangeAllowed = configuration.getInt("max-no-distance-change-allowed");
        ascendingMinDistanceRequired = configuration.getDouble("ascending-min-distance-required");
        ascendingMinDifferenceDistance = configuration.getDouble("ascending-min-difference-distance");

        CheckTimings.registerTiming(checkType);
    }
}
