package arc.check.moving;

import arc.check.Check;
import arc.check.CheckSubType;
import arc.check.CheckType;

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


    @Override
    public void reloadConfig() {

    }

    @Override
    public void load() {

    }
}
