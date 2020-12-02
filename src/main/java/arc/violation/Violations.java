package arc.violation;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of player violations
 * TODO: Why is this a string and not {@link arc.check.CheckType}
 */
public final class Violations {

    /**
     * Keeps track of violations by check name.
     * TODO: Needs to be concurrent?
     */
    private final Map<String, Integer> violations = new HashMap<>();

    /**
     * Get the violation level for a check
     *
     * @param checkName the check name
     * @return the level
     */
    public int getViolationLevel(String checkName) {
        return violations.getOrDefault(checkName, 0);
    }

    /**
     * Increment the violation level
     *
     * @param checkName the check name
     */
    public int incrementViolationLevel(String checkName) {
        final int level = getViolationLevel(checkName) + 1;
        violations.put(checkName, level);
        return level;
    }

    /**
     * Decrease the violation level by 1.
     *
     * @param checkName the check name
     */
    public void decreaseViolationLevel(String checkName) {
        violations.put(checkName, getViolationLevel(checkName) - 1);
    }

}
