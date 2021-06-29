package bridge.block;

import org.bukkit.block.Block;

/**
 * A cross version compatible block access.
 */
public interface BlockAccess {

    /**
     * Check if the provided {@code block} has a vertical modifier, when stepping onto it.
     *
     * @param block the block
     * @return {@code true} if so
     */
    boolean hasVerticalModifier(Block block);

    /**
     * Check if this block is a climbable
     *
     * @param block the block
     * @return {@code true} if so
     */
    boolean isClimbable(Block block);

    /**
     * Check if this block is a liquid
     *
     * @param block the block
     * @return {@code true} if so
     */
    boolean isLiquid(Block block);

    /**
     * Check if the block is a trapdoor
     *
     * @param block the block
     * @return {@code true} if so
     */
    boolean isTrapdoor(Block block);

    /**
     * Check if the block is ice
     *
     * @param block the ice
     * @return {@code true} if so
     */
    boolean isIce(Block block);

    /**
     * Check if the block is a wall block
     *
     * @param block the block
     * @return {@code true} if so
     */
    boolean isWall(Block block);

    /**
     * Check if the block is a slime block
     *
     * @param block the block
     * @return {@code true} if so
     */
    boolean isSlimeblock(Block block);

    /**
     * Check if the provided {@code block} is considered to be ground, meaning you can stand on it.
     *
     * @param block the block
     * @return {@code true} if so
     */
    default boolean isConsideredGround(Block block) {
        if (isWall(block)) return false;
        return block.getType().isSolid() || hasVerticalModifier(block) || isTrapdoor(block);
    }

}
