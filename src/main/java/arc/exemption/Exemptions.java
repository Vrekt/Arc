package arc.exemption;

import arc.check.CheckType;
import arc.utility.Closeable;

import java.util.HashMap;
import java.util.Map;

/**
 * Player exemption data
 */
public final class Exemptions implements Closeable {

    /**
     * Exemption data
     */
    private final Map<CheckType, Long> exemptions = new HashMap<>();

    /**
     * Add an exemption
     *
     * @param check    the check
     * @param duration the duration
     */
    public void addExemption(CheckType check, long duration) {
        exemptions.put(check, duration);
    }

    /**
     * Check if there is an exemption
     *
     * @param check the check
     * @return {@code true} if so
     */
    public boolean isExempt(CheckType check) {
        final long time = exemptions.getOrDefault(check, 0L);
        if (time == 0) return false;

        final boolean result = (System.currentTimeMillis() - time <= 0);
        if (!result) exemptions.remove(check);
        return result;
    }

    @Override
    public void close() {
        exemptions.clear();
    }

}
