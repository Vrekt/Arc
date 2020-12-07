package arc.check.timings;

import arc.check.CheckType;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Check timing
 */
public final class CheckTiming {

    /**
     * The check
     */
    private final CheckType check;

    /**
     * The timings
     */
    private final Set<Long> timings = ConcurrentHashMap.newKeySet();

    /**
     * Started timings
     */
    private final Map<UUID, Long> started = new ConcurrentHashMap<>();

    /**
     * Initialize
     *
     * @param check the check
     */
    public CheckTiming(CheckType check) {
        this.check = check;
    }

    /**
     * Start timing
     */
    public void start(Player player) {
        if (timings.size() >= 10000) {
            timings.clear();
        }
        started.put(player.getUniqueId(), System.currentTimeMillis());
    }

    /**
     * Stop timing
     */
    public void stop(Player player) {
        if (timings.size() >= 10000) {
            timings.clear();
        }
        final long delta = System.currentTimeMillis() - started.get(player.getUniqueId());
        timings.add(delta);
    }

    /**
     * Get average
     *
     * @return the average
     */
    public long average() {
        return (long) timings.stream().mapToDouble(l -> l).average().orElse(0);
    }

    /**
     * Check if there are any timings
     *
     * @return {@code true} if so
     */
    public boolean hasAny() {
        return timings.size() > 0;
    }

}
