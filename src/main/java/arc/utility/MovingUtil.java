package arc.utility;

import arc.Arc;
import bridge.Bridge;
import arc.data.moving.MovingData;
import arc.utility.math.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

/**
 * Moving utility for calculating various things related to movement.
 */
public final class MovingUtil {

    /**
     * The bridge
     */
    private static final Bridge BRIDGE = Arc.bridge();

    /**
     * Check if the location is on a solid block
     * 0.5, 0.3, 0.1
     *
     * @param location the location
     * @return {@code true} if so
     */
    public static boolean onGround(Location location) {
        // test subtracted blocks next
        final Block selfBlock = location.clone().subtract(0, 0.5, 0).getBlock();
        if (BRIDGE.materials().isSolid(selfBlock)) return true;

        // else, get all blocks around us and check if they are solid
        final Location clone = location.clone();
        final List<Block> neighbors = neighbors(clone, 0.3, -0.1, 0.3);
        return neighbors.stream().anyMatch(block -> BRIDGE.materials().isSolid(block));
    }

    /**
     * Check if the player has a climbable block at this location
     *
     * @param location the location
     * @return {@code true} if so
     */
    public static boolean hasClimbable(Location location) {
        final Block selfBlock = location.getBlock();
        if (BRIDGE.materials().isClimbable(selfBlock)) return true;

        final Location clone = location.clone();
        final List<Block> neighbors = neighbors(clone, 0.1, -0.06, 0.1);
        return neighbors.stream().anyMatch(block -> BRIDGE.materials().isClimbable(block));
    }

    /**
     * @return if we are in or on liquid.
     */
    public static boolean isInOrOnLiquid(Location location) {
        if (BRIDGE.materials().isLiquid(location.getBlock())
                || BRIDGE.materials().isLiquid(location.getBlock().getRelative(BlockFace.DOWN))) return true;

        final List<Block> neighbors0 = neighbors(location, 0.1, -0.01, 0.1);
        // final List<Block> neighbors1 = neighbors(location, 0.1, -0.5, 0.1);
        return neighbors0.stream().anyMatch(block -> BRIDGE.materials().isLiquid(block));
    }

    /**
     * Get a block list of neighbors around a location
     *
     * @param location  the location
     * @param xModifier the X modifier
     * @param yModifier the Y modifier
     * @param zModifier the Z modifier
     * @return the neighbors
     */
    public static List<Block> neighbors(Location location, double xModifier, double yModifier, double zModifier) {
        final double originalX = location.getX();
        final double originalY = location.getY();
        final double originalZ = location.getZ();
        final List<Block> neighbors = new ArrayList<>();

        neighbors.add(modifyAndReset(location, xModifier, yModifier, -zModifier, originalX, originalY, originalZ));
        neighbors.add(modifyAndReset(location, -xModifier, yModifier, zModifier, originalX, originalY, originalZ));
        neighbors.add(modifyAndReset(location, -xModifier, yModifier, -zModifier, originalX, originalY, originalZ));
        neighbors.add(modifyAndReset(location, xModifier, yModifier, zModifier, originalX, originalY, originalZ));
        return neighbors;
    }

    /**
     * Modify the location and then reset it
     *
     * @param location  the location
     * @param xModifier the X modifier
     * @param yModifier the Y modifier
     * @param zModifier the Z modifier
     * @param originalX the original X
     * @param originalY the original Y
     * @param originalZ the original Z
     * @return the material at the modified location
     */
    public static Block modifyAndReset(Location location, double xModifier, double yModifier, double zModifier, double originalX, double originalY, double originalZ) {
        final Block block = location.add(xModifier, yModifier, zModifier).getBlock();
        reset(location, originalX, originalY, originalZ);
        return block;
    }

    /**
     * Reset the location
     *
     * @param location  the location
     * @param originalX the original X
     * @param originalY the original Y
     * @param originalZ the original Z
     */
    public static void reset(Location location, double originalX, double originalY, double originalZ) {
        location.setX(originalX);
        location.setY(originalY);
        location.setZ(originalZ);
    }

    /**
     * Update a moving player
     *
     * @param data their data
     * @param from from
     * @param to   to
     */
    public static void updateMovingPlayer(MovingData data, Location from, Location to) {
        data.from(from);
        data.to(to);

        // calculate ground
        final boolean wasOnGround = data.onGround();
        final boolean onGround = onGround(to);
        data.onGround(onGround);
        data.wasOnGround(wasOnGround);

        if (onGround) {
            data.ground(to);
            data.onGroundTime(data.onGroundTime() + 1);
        } else {
            data.onGroundTime(0);
        }

        // calculate vertical distance
        final double distance = MathUtil.vertical(from, to);
        final double last = data.vertical();
        data.lastVerticalDistance(last);
        data.verticalDistance(distance);

        // set ascending/descending states
        data.ascending(to.getY() > from.getY() && distance > 0.0);
        data.descending(from.getY() > to.getY() && distance > 0.0);
        if (data.descending()) {
            data.descendingTime(data.descendingTime() + 1);
        } else {
            data.descendingTime(0);
        }

        if (data.ascending()) {
            data.ascendingTime(data.ascendingTime() + 1);
        } else {
            data.ascendingTime(0);
        }

        data.climbing(distance > 0.0 && hasClimbable(to));
        data.lastMovingUpdate(System.currentTimeMillis());
    }

}
