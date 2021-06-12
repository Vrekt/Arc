package arc.utility.block;

import arc.Arc;
import bridge.material.MaterialApi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * A basic block utility
 */
public final class Blocks {

    /**
     * Scheduler
     */
    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    /**
     * Plugin
     */
    private static final Plugin PLUGIN = Arc.plugin();

    /**
     * MaterialApi bridge.
     */
    private static final MaterialApi MATERIALS = Arc.bridge().material();

    /**
     * Get a block sync
     *
     * @param location the location
     * @param relative the relative
     * @return the block
     */
    public static Block getBlockSync(Location location, BlockFace relative) {
        return getBlockSync(location.add(relative.getModX(), relative.getModY(), relative.getModZ()));
    }

    /**
     * Get a block sync.
     * If the chunk is loaded, the block is returned.
     * Otherwise, the chunk is loaded sync and the block is returned.
     *
     * @param location the location
     * @return the block
     */
    public static Block getBlockSync(Location location) {
        if (Objects.requireNonNull(location.getWorld()).isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
            return location.getBlock();
        } else {
            try {
                return SCHEDULER.callSyncMethod(PLUGIN, () -> {
                    location.getWorld().loadChunk(location.getBlockX() >> 4, location.getBlockZ() >> 4);
                    return location.getBlock();
                }).get();
            } catch (InterruptedException | ExecutionException exception) {
                exception.printStackTrace();
            }
        }
        return location.getBlock();
    }

    /**
     * Check if the block is a fence.
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isFence(Block block) {
        return MATERIALS.isFence(block);
    }

    /**
     * Check if the block is a slab
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isSlab(Block block) {
        return MATERIALS.isSlab(block);
    }

    /**
     * Check if a block is a stair
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isStair(Block block) {
        return MATERIALS.isStair(block);
    }

    /**
     * Check if a block is a fence gate
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isFenceGate(Block block) {
        return MATERIALS.isFenceGate(block);
    }

    /**
     * Check if a block is a climbable
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isClimbable(Block block) {
        return MATERIALS.isClimbable(block);
    }

    /**
     * Check if a block is a liquid
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isLiquid(Block block) {
        return MATERIALS.isLiquid(block);
    }

    /**
     * Check if a block is a trapdoor
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isTrapdoor(Block block) {
        return MATERIALS.isTrapdoor(block);
    }

    /**
     * Check if a block is ice
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isIce(Block block) {
        return MATERIALS.isIce(block);
    }

    /**
     * Do not make isWall part of the {@code isSolid} check
     *
     * @param block the block
     * @return {@code true} if the block is a wall
     */
    public static boolean isWall(Block block) {
        return MATERIALS.isWall(block);
    }

    /**
     * Check if a block is considered solid
     *
     * @param block the block
     * @return {@code true} if so
     */
    public static boolean isSolid(Block block) {
        return MATERIALS.isSolid(block);
    }

}
