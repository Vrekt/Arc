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
        return block.getBlockData() instanceof Fence
                || block.getBlockData() instanceof Slab
                || block.getBlockData() instanceof Stairs
                || block.getBlockData() instanceof Gate
                || isWall(block)
                || block.getType() == Material.SNOW
                || block.getType() == Material.SKELETON_SKULL
                || block.getType() == Material.WITHER_SKELETON_SKULL
                || block.getType() == Material.CREEPER_HEAD
                || block.getType() == Material.DRAGON_HEAD
                || block.getType() == Material.PLAYER_HEAD
                || block.getType() == Material.ZOMBIE_HEAD;
    }

    @Override
    public boolean isClimbable(Block block) {
        return block.getType() == Material.LADDER
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
        return block.getBlockData() instanceof TrapDoor;
    }

    @Override
    public boolean isIce(Block block) {
        return block.getType() == Material.ICE || block.getType() == Material.PACKED_ICE || block.getType() == Material.BLUE_ICE || block.getType() == Material.FROSTED_ICE;
    }

    @Override
    public boolean isWall(Block block) {
        return block.getBlockData() instanceof Wall;
    }

    @Override
    public boolean isSlimeblock(Block block) {
        return block.getType() == Material.SLIME_BLOCK;
    }

    @Override
    public boolean isInteractable(Block block) {
        return block.getType().isInteractable();
    }

    @Override
    public boolean isCarpet(Block block) {
        switch (block.getType()) {
            case WHITE_CARPET:
            case ORANGE_CARPET:
            case MAGENTA_CARPET:
            case LIGHT_BLUE_CARPET:
            case YELLOW_CARPET:
            case LIME_CARPET:
            case PINK_CARPET:
            case GRAY_CARPET:
            case LIGHT_GRAY_CARPET:
            case CYAN_CARPET:
            case PURPLE_CARPET:
            case BLUE_CARPET:
            case BROWN_CARPET:
            case GREEN_CARPET:
            case RED_CARPET:
            case BLACK_CARPET:
                return true;
        }
        return false;
    }

    @Override
    public boolean isGround(Block block) {
        return block.getType() == Material.END_ROD;
    }

}
