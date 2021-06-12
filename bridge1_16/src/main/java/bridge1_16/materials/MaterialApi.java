package bridge1_16.materials;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.*;
import org.bukkit.inventory.ItemStack;

/**
 * Material API for 1.16
 */
public final class MaterialApi implements bridge.material.MaterialApi {

    @Override
    public boolean isFence(Block block) {
        return block.getState() instanceof Fence;
    }

    @Override
    public boolean isSlab(Block block) {
        return block.getState() instanceof Slab;
    }

    @Override
    public boolean isStair(Block block) {
        return block.getState() instanceof Stairs;
    }

    @Override
    public boolean isFenceGate(Block block) {
        return block.getState() instanceof Gate;
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
    public Material getMaterial(String name) {
        return Material.getMaterial(name);
    }

    @Override
    public ItemStack createItem(String material) {
        return new ItemStack(getMaterial(material));
    }

    @Override
    public ItemStack createItem(String material, short data) {
        return createItem(material);
    }

}
