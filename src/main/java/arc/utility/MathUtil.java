package arc.utility;

import org.bukkit.Location;

/**
 * A math utility.
 */
public final class MathUtil {

    /**
     * Calculate the vertical distance
     *
     * @param from from
     * @param to   to
     * @return the vertical distance
     */
    public static double distance(Location from, Location to) {
        final var dy = to.getY() - from.getY();
        return Math.sqrt(dy * dy);
    }

}
