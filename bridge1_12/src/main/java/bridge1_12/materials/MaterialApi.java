package bridge1_12.materials;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Gate;
import org.bukkit.material.Stairs;
import org.bukkit.material.TrapDoor;

/**
 * 1.12 Material API
 */
public final class MaterialApi implements bridge.material.MaterialApi {

    @Override
    public boolean isFence(Block block) {
        switch (block.getType()) {
            case FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case IRON_FENCE:
            case JUNGLE_FENCE:
            case NETHER_FENCE:
            case SPRUCE_FENCE:
            case ACACIA_FENCE:
                return true;
        }
        return false;
    }

    @Override
    public boolean isSlab(Block block) {
        return block.getType() == Material.STEP
                || block.getType() == Material.WOOD_STEP
                || block.getType() == Material.PURPUR_SLAB
                || block.getType() == Material.STONE_SLAB2;
    }

    @Override
    public boolean isStair(Block block) {
        return block.getType().getData().equals(Stairs.class);
    }

    @Override
    public boolean isFenceGate(Block block) {
        return block.getType().getData().equals(Gate.class);
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
        return new ItemStack(getMaterial(material), 1, data);
    }

}
