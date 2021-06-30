package arc.utility.block;

import arc.Arc;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.NumberConversions;

/**
 * Block access utility
 */
public final class BlockAccess {

    /**
     * Block access
     */
    private static final bridge.block.BlockAccess ACCESS = Arc.getBridge().getBlockAccess();

    /**
     * Check if the provided origin and modified X, Y, Z coordinates have a vertical modifier
     *
     * @param origin the origin
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return {@code true} if so
     */
    public static boolean hasVerticalModifierAt(Location origin, World world, double x, double y, double z) {
        if (hasVerticalModifierAt0(origin, world, x, y, z)) return true;
        if (hasVerticalModifierAt0(origin, world, x, y, -z)) return true;
        if (hasVerticalModifierAt0(origin, world, -x, y, z)) return true;
        return hasVerticalModifierAt0(origin, world, -x, y, -z);
    }

    /**
     * Check if the provided origin and modified X, Y, Z coordinates have solid ground.
     *
     * @param origin the origin
     * @param world  the world
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return {@code true} if so
     */
    public static boolean hasSolidGroundAt(Location origin, World world, double x, double y, double z) {
        if (hasSolidGroundAt0(origin, world, x, y, z)) return true;
        if (hasSolidGroundAt0(origin, world, x, y, -z)) return true;
        if (hasSolidGroundAt0(origin, world, -x, y, z)) return true;
        return hasSolidGroundAt0(origin, world, -x, y, -z);
    }

    /**
     * Check if the provided origin and modified X, Y, Z coordinates have a climbable block.
     *
     * @param origin the origin
     * @param world  the world
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return {@code true} if so
     */
    public static boolean hasClimableAt(Location origin, World world, double x, double y, double z) {
        if (hasClimableAt0(origin, world, x, y, z)) return true;
        if (hasClimableAt0(origin, world, x, y, -z)) return true;
        if (hasClimableAt0(origin, world, -x, y, z)) return true;
        return hasClimableAt0(origin, world, -x, y, -z);
    }

    /**
     * Check if the provided origin and modified X, Y, Z coordinates is liquid.
     *
     * @param origin the origin
     * @param world  the world
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return {@code true} if so
     */
    public static boolean hasLiquidAt(Location origin, World world, double x, double y, double z) {
        if (hasLiquidAt0(origin, world, x, y, z)) return true;
        if (hasLiquidAt0(origin, world, x, y, -z)) return true;
        if (hasLiquidAt0(origin, world, -x, y, z)) return true;
        return hasLiquidAt0(origin, world, -x, y, -z);
    }

    /**
     * Check if the provided origin and modified X, Y, Z coordinates has ice
     *
     * @param origin the origin
     * @param world  the world
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return {@code true} if so
     */
    public static boolean hasIceAt(Location origin, World world, double x, double y, double z) {
        if (hasIceAt0(origin, world, x, y, z)) return true;
        if (hasIceAt0(origin, world, x, y, -z)) return true;
        if (hasIceAt0(origin, world, -x, y, z)) return true;
        return hasIceAt0(origin, world, -x, y, -z);
    }

    /**
     * Check if the provided origin and modified X, Y, Z coordinates have a vertical modifier
     *
     * @param origin    the origin
     * @param blockFace the block face
     * @return {@code true} if so
     */
    public static boolean hasVerticalModifierAt(Location origin, BlockFace blockFace) {
        return ACCESS.hasVerticalModifier(origin.getBlock().getRelative(blockFace));
    }

    /**
     * Check if the modified location has a vertical modifier block.
     *
     * @param origin the origin
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the result
     */
    private static boolean hasVerticalModifierAt0(Location origin, World world, double x, double y, double z) {
        return ACCESS.hasVerticalModifier(world.getBlockAt(NumberConversions.floor(origin.getX() + x),
                NumberConversions.floor(origin.getY() + y), NumberConversions.floor(origin.getZ() + z)));
    }

    /**
     * Check if the modified location has solid ground.
     *
     * @param origin the origin
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the result
     */
    private static boolean hasSolidGroundAt0(Location origin, World world, double x, double y, double z) {
        return ACCESS.isConsideredGround(world.getBlockAt(NumberConversions.floor(origin.getX() + x),
                NumberConversions.floor(origin.getY() + y), NumberConversions.floor(origin.getZ() + z)));
    }

    /**
     * Check if the modified location has a climable.
     *
     * @param origin the origin
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the result
     */
    private static boolean hasClimableAt0(Location origin, World world, double x, double y, double z) {
        return ACCESS.isClimbable(world.getBlockAt(NumberConversions.floor(origin.getX() + x),
                NumberConversions.floor(origin.getY() + y), NumberConversions.floor(origin.getZ() + z)));
    }

    /**
     * Check if the modified location has liquid
     *
     * @param origin the origin
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the result
     */
    private static boolean hasLiquidAt0(Location origin, World world, double x, double y, double z) {
        return ACCESS.isLiquid(world.getBlockAt(NumberConversions.floor(origin.getX() + x),
                NumberConversions.floor(origin.getY() + y), NumberConversions.floor(origin.getZ() + z)));
    }

    /**
     * Check if the modified location has ice
     *
     * @param origin the origin
     * @param x      the modified X
     * @param y      the modified Y
     * @param z      the modified Z
     * @return the result
     */
    private static boolean hasIceAt0(Location origin, World world, double x, double y, double z) {
        return ACCESS.isIce(world.getBlockAt(NumberConversions.floor(origin.getX() + x),
                NumberConversions.floor(origin.getY() + y), NumberConversions.floor(origin.getZ() + z)));
    }

    /**
     * Check if a block is a climbable
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isClimbable(Block block) {
        return ACCESS.isClimbable(block);
    }

    /**
     * Check if a block is a liquid
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isLiquid(Block block) {
        return ACCESS.isLiquid(block);
    }

    /**
     * Check if a block is a trapdoor
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isTrapdoor(Block block) {
        return ACCESS.isTrapdoor(block);
    }

    /**
     * Check if a block is ice
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isIce(Block block) {
        return ACCESS.isIce(block);
    }

    /**
     * Do not make isWall part of the {@code isSolid} check
     *
     * @param block the block
     * @return {@code true} if the block is a wall
     */
    public static boolean isWall(Block block) {
        return ACCESS.isWall(block);
    }

    /**
     * Check if a block is considered ground, meaning you can stand on it.
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isConsideredGround(Block block) {
        return ACCESS.isConsideredGround(block);
    }

    /**
     * Check if a block is a slime-block
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isSlimeblock(Block block) {
        return ACCESS.isSlimeblock(block);
    }

}
