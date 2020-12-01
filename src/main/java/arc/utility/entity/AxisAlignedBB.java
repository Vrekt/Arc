package arc.utility.entity;

/**
 * A basic AxisAlignedBB.
 */
public final class AxisAlignedBB {

    /**
     * Coordinates
     */
    private final double minX, minY, minZ, maxX, maxY, maxZ;

    public AxisAlignedBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public AxisAlignedBB(net.minecraft.server.v1_8_R3.AxisAlignedBB nms) {
        this(nms.a, nms.b, nms.c, nms.d, nms.e, nms.f);
    }

    public double minX() {
        return minX;
    }

    public double minY() {
        return minY;
    }

    public double minZ() {
        return minZ;
    }

    public double maxX() {
        return maxX;
    }

    public double maxY() {
        return maxY;
    }

    public double maxZ() {
        return maxZ;
    }

}
