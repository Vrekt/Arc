package arc.utility;

import arc.data.moving.MovingData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Gate;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Moving utility for calculating various things related to movement.
 */
public final class MovingUtil {

    /**
     * Test if a material is a slab
     */
    private static final Predicate<Material> IS_SLAB = material -> material.getData().equals(Step.class);

    /**
     * Test if a material is a stair
     */
    private static final Predicate<Material> IS_STAIR = material -> material.getData().equals(Stairs.class);

    /**
     * Test if a material is a fence
     */
    private static final Predicate<Material> IS_FENCE = material -> {
        switch (material) {
            case FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case IRON_FENCE:
            case JUNGLE_FENCE:
            case NETHER_FENCE:
            case SPRUCE_FENCE:
                return true;
        }
        return false;
    };

    /**
     * Test if a material is a fence gate
     */
    private static final Predicate<Material> IS_FENCE_GATE = material -> material.getData().equals(Gate.class);

    /**
     * Test if a material is considered solid
     */
    private static final Predicate<Material> CONSIDERED_SOLID = material ->
            material.isSolid()
                    || IS_SLAB.test(material)
                    || IS_STAIR.test(material)
                    || IS_FENCE.test(material)
                    || IS_FENCE_GATE.test(material);

    /**
     * Check if the location is on a solid block
     * 0.5, 0.3, 0.1
     * <p>
     * TODO: Removed:
     * TODO: final var relativeBlock = location.getBlock().getRelative(BlockFace.DOWN);
     * TODO: if (CONSIDERED_SOLID.test(relativeBlock.getType())) return true;
     * Was causing inaccuracy, may need to be added back but with modifications.
     *
     * @param location the location
     * @return {@code true} if so
     */
    public static boolean onGround(Location location) {
        // test subtracted blocks next
        final var selfBlock = location.clone().subtract(0, 0.5, 0).getBlock().getType();
        if (CONSIDERED_SOLID.test(selfBlock)) return true;

        // else, get all blocks around us and check if they are solid
        final var clone = location.clone();
        final var neighbors0 = neighbors(clone, 0.3, -0.5, 0.3);
        return neighbors0.stream().anyMatch(CONSIDERED_SOLID);
    }

    /**
     * Check if the player has a climbable block at this location
     *
     * @param location the location
     * @return {@code true} if so
     */
    public static boolean hasClimbable(Location location) {
        final var selfBlock = location.getBlock().getType();
        if (isClimbable(selfBlock)) return true;

        final var clone = location.clone();
        final var neighbors = neighbors(clone, 0.1, -0.06, 0.1);
        return neighbors.stream().anyMatch(MovingUtil::isClimbable);
    }

    /**
     * Check if this block is a climbable
     *
     * @param material the mat
     * @return {@code true} if so
     */
    public static boolean isClimbable(Material material) {
        return material == Material.LADDER || material == Material.VINE;
    }

    /**
     * @return if we are in liquid.
     */
    public static boolean isInLiquid(Location location) {
        return location.getBlock().isLiquid() || location.getBlock().getRelative(BlockFace.DOWN).isLiquid();
    }

    /**
     * Get a material list of neighbors around a location
     *
     * @param location  the location
     * @param xModifier the X modifier
     * @param yModifier the Y modifier
     * @param zModifier the Z modifier
     * @return the neighbors
     */
    public static List<Material> neighbors(Location location, double xModifier, double yModifier, double zModifier) {
        final var originalX = location.getX();
        final var originalY = location.getY();
        final var originalZ = location.getZ();
        final var neighbors = new ArrayList<Material>();

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
    public static Material modifyAndReset(Location location, double xModifier, double yModifier, double zModifier, double originalX, double originalY, double originalZ) {
        final var material = location.add(xModifier, yModifier, zModifier).getBlock().getType();
        reset(location, originalX, originalY, originalZ);
        return material;
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
        final var wasOnGround = data.onGround();
        final var onGround = onGround(to);
        data.onGround(onGround);
        data.wasOnGround(wasOnGround);

        if (onGround) {
            data.ground(to);
            data.onGroundTime(data.onGroundTime() + 1);
        } else {
            data.onGroundTime(0);
        }

        // calculate vertical distance
        final var distance = MathUtil.distance(from, to);
        final var last = data.vertical();
        data.lastVerticalDistance(last);
        data.verticalDistance(distance);

        // set ascending/descending states
        data.ascending(to.getY() > from.getY() && distance > 0.0);
        data.descending(from.getY() > to.getY() && distance > 0.0);
        data.climbing(distance > 0.0 && hasClimbable(to));
        data.lastMovingUpdate(System.currentTimeMillis());
    }

}
