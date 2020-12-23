package bridge.materials;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

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
     * Get a material
     *
     * @param name the name
     * @return the name
     */
    Material getMaterial(String name);

    /**
     * Create an item
     *
     * @param material the material name
     * @return the item
     */
    ItemStack createItem(String material);

    /**
     * Create an item
     *
     * @param material the material
     * @param data     the data
     * @return the item
     */
    ItemStack createItem(String material, short data);

    /**
     * WARNING: Will not detect walls as solid blocks, use {@code isWall} instead.
     * Check if a block is solid
     * Prevent from flagging walls.
     * Walls should already be detected earlier than this, with BlockRelative.DOWN
     * TODO: Workaround
     *
     * @param block the block
     * @return {@code true} if so
     */
    default boolean isSolid(Block block) {
        if (isWall(block)) return false;
        return block.getType().isSolid() || isSlab(block) || isStair(block) || isFence(block) || isFenceGate(block) || isTrapdoor(block);
    }

}
