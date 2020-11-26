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
    public static double vertical(Location from, Location to) {
        final double dy = to.getY() - from.getY();
        return Math.sqrt(dy * dy);
    }

    /**
     * Calculate the 3D distance
     *
     * @param from from
     * @param to   to
     * @return the distance
     */
    public static double distance(Location from, Location to) {
        final double dx = to.getX() - from.getX();
        final double dy = to.getY() - from.getY();
        final double dz = to.getZ() - from.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

}
