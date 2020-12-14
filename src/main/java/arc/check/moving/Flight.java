package arc.check.moving;

import arc.Arc;
import arc.check.Check;
import arc.check.CheckSubType;
import arc.check.CheckType;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import arc.utility.MovingUtil;
import arc.utility.math.MathUtil;
import arc.violation.result.ViolationResult;
import bridge.Version;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Checks various vertical movement/flying stuff.
 */
public final class Flight extends Check {

    /**
     * Min diff allowed when falling
     * Max ascend allowed
     */
    private double boatFlyMinDiff, boatFlyMaxAscend;

    /**
     * Min descending time required
     * Min ascending time required.
     * Max setback distance
     */
    private int boatFlyMinDescendingTime, boatFlyMinAscendingTime, boatFlyMaxSetbackDistance;

    public Flight() {
        super(CheckType.FLIGHT);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        createSubTypeSections(CheckSubType.FLIGHT_BOATFLY);
        addConfigurationValue(CheckSubType.FLIGHT_BOATFLY, "boat-fly-min-descending-time", 5);
        addConfigurationValue(CheckSubType.FLIGHT_BOATFLY, "boat-fly-min-ascending-time", 5);
        addConfigurationValue(CheckSubType.FLIGHT_BOATFLY, "boat-fly-min-diff", 0.038);
        addConfigurationValue(CheckSubType.FLIGHT_BOATFLY, "boat-fly-max-ascend", 0.0);
        addConfigurationValue(CheckSubType.FLIGHT_BOATFLY, "boat-fly-max-setback-distance", 1);

        if (enabled()) load();
    }

    /**
     * Check this player for flight
     *
     * @param player the player
     * @param data   the data
     * @param event  the event
     */
    public void check(Player player, MovingData data, PlayerMoveEvent event) {
        if (!enabled() || exempt(player)) return;

        final CheckResult result = new CheckResult();
        checkBoatFly(player, data, result, event);


    }

    /**
     * Check boat fly.
     *
     * @param player the player
     * @param data   data
     * @param result the result
     */
    private void checkBoatFly(Player player, MovingData data, CheckResult result, PlayerMoveEvent event) {
        if (!Arc.version().isNewerThan(Version.VERSION_1_8) || exempt(player, CheckSubType.FLIGHT_BOATFLY)) return;

        if (player.isInsideVehicle() && player.getVehicle() instanceof Boat) {
            final Boat boat = (Boat) player.getVehicle();
            final boolean inOrOnLiquid = MovingUtil.isInOrOnLiquid(boat.getLocation());

            if (!boat.isOnGround() && !inOrOnLiquid
                    && !data.onGround()) {
                final double diff = data.vertical() - data.lastVerticalDistance();
                if (data.descendingTime() > boatFlyMinDescendingTime
                        && diff >= 0.0 && diff < boatFlyMinDiff) {
                    result.setFailed(CheckSubType.FLIGHT_BOATFLY, "difference less than minimum.");
                    result.parameter("difference", diff);
                    result.parameter("minimum", boatFlyMinDiff);
                    result.parameter("descendingTime", data.descendingTime());
                    result.parameter("minDescendingTime", boatFlyMinDescendingTime);
                }

                if (data.ascendingTime() > boatFlyMinAscendingTime
                        && diff >= boatFlyMaxAscend) {
                    result.setFailed(CheckSubType.FLIGHT_BOATFLY, "difference greater than max.");
                    result.parameter("difference", diff);
                    result.parameter("max", boatFlyMaxAscend);
                }
            }
        }

        // process the violation
        final ViolationResult violation = checkViolation(player, result);
        if (violation.cancel()) {
            // kick player from vehicle and set-back if possible.
            player.leaveVehicle();
            // calc setback distance and set it
            final Location setback = data.hasGround() ? data.ground() : data.from();
            final double distance = MathUtil.distance(setback, data.to());
            if (distance < boatFlyMaxSetbackDistance) {
                event.setTo(setback);
            }
        }
    }

    @Override
    public void reloadConfig() {
        unload();
        if (enabled()) load();
    }

    @Override
    public void load() {
        final ConfigurationSection boatFlySection = configuration.subTypeSection(CheckSubType.FLIGHT_BOATFLY);

        boatFlyMinDiff = boatFlySection.getDouble("boat-fly-min-diff");
        boatFlyMinDescendingTime = boatFlySection.getInt("boat-fly-min-descending-time");
        boatFlyMinAscendingTime = boatFlySection.getInt("boat-fly-min-ascending-time");
        boatFlyMaxAscend = boatFlySection.getDouble("boat-fly-max-ascend");
        boatFlyMaxSetbackDistance = boatFlySection.getInt("boat-fly-max-setback-distance");
    }
}
