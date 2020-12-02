package arc.bridge;

import org.bukkit.block.Block;

/**
 * Materials bridge.
 */
public interface MaterialsBridge {

    /**
     * Check if a block is a fence
     *
     * @param block the block
     * @return {@code true} if so
     */
    boolean isFence(Block block);

    /**
     * Check if a block is a slab
     *
     * @param block the block
     * @return {@code true} if so
     */
    boolean isSlab(Block block);

    /**
     * Check if a block is a stair
     *
     * @param block the block
     * @return {@code true} if so
     */
    boolean isStair(Block block);

    /**
     * Check if a block is a fence gate
     *
     * @param block the block
     * @return {@code true} if so
     */
    boolean isFenceGate(Block block);

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
     * Check if a block is solid
     *
     * @param block the block
     * @return {@code true} if so
     */
    default boolean isSolid(Block block) {
        return block.getType().isSolid() || isSlab(block) || isStair(block) || isFence(block) || isFenceGate(block);
    }

}
