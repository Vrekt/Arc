package arc.check.timing;

import arc.Arc;
import arc.check.types.CheckType;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Check timings utility.
 */
public final class CheckTimings {

    /**
     * Started timings
     */
    private static final ConcurrentMap<CheckType, ConcurrentMap<UUID, Long>> STARTED_TIMINGS = new ConcurrentHashMap<>();

    /**
     * Timings history
     */
    private static final ConcurrentMap<CheckType, Set<Long>> TIMINGS = new ConcurrentHashMap<>();

    /**
     * If enabled.
     */
    private static final boolean ENABLED;

    static {
        ENABLED = Arc.getInstance().getArcConfiguration().enableCheckTimings();

        Arc.getInstance().getLogger().info("Check timings state: " + ENABLED);
    }

    /**
     * Register a new timing
     *
     * @param theCheck the check
     */
    public static void registerTiming(CheckType theCheck) {
        if (!ENABLED) return;

        STARTED_TIMINGS.put(theCheck, new ConcurrentHashMap<>());
        TIMINGS.put(theCheck, ConcurrentHashMap.newKeySet());
    }

    /**
     * Start timing the check.
     * <p>
     * The identifier could be anything, but in most cases its a player UUID.
     *
     * @param identifier the identifier
     */
    public static void startTiming(CheckType theCheck, UUID identifier) {
        if (!ENABLED) return;

        STARTED_TIMINGS.get(theCheck).put(identifier, System.nanoTime());
    }

    /**
     * Stop timing for the player / ID.
     *
     * @param theCheck   the check for
     * @param identifier the identifier
     */
    public static void stopTiming(CheckType theCheck, UUID identifier) {
        if (!ENABLED) return;

        final long start = STARTED_TIMINGS.get(theCheck).getOrDefault(identifier, -1L);
        if (start == -1) return;

        final long diff = System.nanoTime() - start;
        TIMINGS.get(theCheck).add(diff);
    }


    /**
     * Get the average timing for the check
     *
     * @param theCheck the check
     * @return the average.
     */
    public static double getAverageTiming(CheckType theCheck) {
        return TIMINGS.get(theCheck)
                .stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);
    }

    /**
     * Get all timings
     *
     * @return the timings
     */
    public static ConcurrentMap<CheckType, Set<Long>> getAllTimings() {
        return TIMINGS;
    }
}
