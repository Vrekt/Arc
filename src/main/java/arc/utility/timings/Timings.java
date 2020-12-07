package arc.utility.timings;

import arc.check.CheckType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Basic timings monitor
 */
public final class Timings {

    /**
     * Check timings
     */
    private static final Map<CheckType, Set<Long>> CHECK_TIMINGS = new ConcurrentHashMap<>();

    static {
        for (CheckType check : CheckType.values()) {
            CHECK_TIMINGS.put(check, ConcurrentHashMap.newKeySet());
        }
    }

    /**
     * Add a timing
     *
     * @param check the check
     * @param time  the time
     */
    public static void addTiming(CheckType check, long time) {
        if (CHECK_TIMINGS.get(check).size() >= 10000) {
            CHECK_TIMINGS.get(check).clear();
        }
        CHECK_TIMINGS.get(check).add(time);
    }

    public static void print(Player player) {
        final List<Long> all = new ArrayList<>();
        for (CheckType check : CheckType.values()) {
            if (CHECK_TIMINGS.get(check).size() == 0) {
                continue;
            }
            final long avg = (long) CHECK_TIMINGS.get(check)
                    .stream()
                    .mapToLong(l -> l)
                    .average()
                    .getAsDouble();

            player.sendMessage(ChatColor.RED + check.getName() + ":" + avg);
            all.add(avg);
        }

        player.sendMessage(ChatColor.GREEN + "ALL " + all);
    }

}
