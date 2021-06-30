package arc.utility.math;

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
        final double dy = Math.abs(to.getY() - from.getY());
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
        final double dx = Math.abs(to.getX() - from.getX());
        final double dy = Math.abs(to.getY() - from.getY());
        final double dz = Math.abs(to.getZ() - from.getZ());
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * 3D Distance.
     *
     * @param location location
     * @param x        X
     * @param y        Y
     * @param z        Z
     * @return distance
     */
    public static double distance(Location location, double x, double y, double z) {
        final double dx = Math.abs(location.getX() - x);
        final double dy = Math.abs(location.getY() - y);
        final double dz = Math.abs(location.getZ() - z);
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * 3D Distance.
     *
     * @param x1 X
     * @param y1 Y
     * @param z1 Z
     * @param x2 X
     * @param y2 Y
     * @param z2 Z
     * @return distance
     */
    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        final double dx = Math.abs(x1 - x2);
        final double dy = Math.abs(y1 - y2);
        final double dz = Math.abs(z1 - z2);
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Calculate horizontal distance
     *
     * @param from from
     * @param to   to
     * @return distance
     */
    public static double horizontal(Location from, Location to) {
        final double dx = Math.abs(to.getX() - from.getX());
        final double dz = Math.abs(to.getZ() - from.getZ());
        return Math.sqrt(dx * dx + dz * dz);
    }

    public static int clampInt(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Wrap the angle
     *
     * @param angle the angle
     * @return the modified angle
     */
    public static float wrapAngle(float angle) {
        angle = angle % 360.0F;
        if (angle >= 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }

}
