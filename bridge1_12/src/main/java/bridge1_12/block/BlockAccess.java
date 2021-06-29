package bridge1_12.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.Gate;
import org.bukkit.material.Stairs;
import org.bukkit.material.TrapDoor;

/**
 * Block access for 1.12
 */
public final class BlockAccess implements bridge.block.BlockAccess {

    @Override
    public boolean hasVerticalModifier(Block block) {
        switch (block.getType()) {
            case FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case IRON_FENCE:
            case JUNGLE_FENCE:
            case NETHER_FENCE:
            case SPRUCE_FENCE:
            case ACACIA_FENCE:
            case STEP:
            case WOOD_STEP:
            case STONE_SLAB2:
            case PURPUR_SLAB:
            case BED_BLOCK:
            case BED:
                return true;
        }

        return block.getType().getData().equals(Stairs.class) || block.getType().getData().equals(Gate.class) || isWall(block);
    }

    @Override
    public boolean isClimbable(Block block) {
        return block.getType() == Material.LADDER || block.getType() == Material.VINE;
    }

    @Override
    public boolean isLiquid(Block block) {
        return block.isLiquid();
    }

    @Override
    public boolean isTrapdoor(Block block) {
        return block.getType().getData().equals(TrapDoor.class);
    }

    @Override
    public boolean isIce(Block block) {
        return block.getType() == Material.ICE || block.getType() == Material.PACKED_ICE;
    }

    @Override
    public boolean isWall(Block block) {
        return block.getType() == Material.COBBLE_WALL;
    }

    @Override
    public boolean isSlimeblock(Block block) {
        return block.getType() == Material.SLIME_BLOCK;
    }

}
