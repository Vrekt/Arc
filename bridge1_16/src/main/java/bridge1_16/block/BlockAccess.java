package bridge1_16.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.*;

/**
 * Block access for 1.16
 */
public final class BlockAccess implements bridge.block.BlockAccess {

    @Override
    public boolean hasVerticalModifier(Block block) {
        return block.getState() instanceof Fence
                || block.getState() instanceof Slab
                || block.getState() instanceof Stairs
                || block.getState() instanceof Gate
                || isWall(block)
                || block.getType() == Material.SNOW;
    }

    @Override
    public boolean isClimbable(Block block) {
        return block.getState().getBlock() instanceof Ladder
                || block.getType() == Material.VINE
                || block.getType() == Material.TWISTING_VINES
                || block.getType() == Material.WEEPING_VINES;
    }

    @Override
    public boolean isLiquid(Block block) {
        return block.isLiquid();
    }

    @Override
    public boolean isTrapdoor(Block block) {
        return block.getState() instanceof TrapDoor;
    }

    @Override
    public boolean isIce(Block block) {
        return block.getType() == Material.ICE || block.getType() == Material.PACKED_ICE || block.getType() == Material.BLUE_ICE || block.getType() == Material.FROSTED_ICE;
    }

    @Override
    public boolean isWall(Block block) {
        return block.getState() instanceof Wall;
    }

    @Override
    public boolean isSlimeblock(Block block) {
        return block.getType() == Material.SLIME_BLOCK;
    }

}
