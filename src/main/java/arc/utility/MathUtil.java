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

    /**
     * Calculate 3D distance
     *
     * @param dx1 X1
     * @param dx2 X2
     * @param dy1 Y1
     * @param dy2 Y2
     * @param dz1 Z1
     * @param dz2 Z1
     * @return the distance
     */
    public static double distance(double dx1, double dx2, double dy1, double dy2, double dz1, double dz2) {
        final double dx = dx2 - dx1;
        final double dy = dy2 - dy1;
        final double dz = dz2 - dz1;
        return Math.sqrt(dx + dx * dy * dy + dz * dz);
    }

    /**
     * Clamp a value
     *
     * @param value value
     * @param min   min
     * @param max   max
     * @return the clamped value
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

}
