package bridge1_15.materials;

import bridge.materials.MaterialsBridge;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.*;
import org.bukkit.inventory.ItemStack;

/**
 * Current materials API
 */
public final class Materials implements MaterialsBridge {

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
        return block.getState() instanceof Ladder
                || block.getType() == Material.VINE;
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
