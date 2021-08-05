package bridge1_8.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.Gate;
import org.bukkit.material.Stairs;
import org.bukkit.material.TrapDoor;

/**
 * Block access for 1.8
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
            case BED_BLOCK:
            case BED:
            case SNOW:
            case SKULL:
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

    @Override
    public boolean isInteractable(Block block) {
        switch (block.getType()) {
            case STONE_BUTTON:
            case TRAP_DOOR:
            case IRON_TRAPDOOR:
            case SIGN:
            case WALL_SIGN:
            case BANNER:
            case STANDING_BANNER:
            case ENCHANTMENT_TABLE:
            case WALL_BANNER:
            case WOOD_BUTTON:
            case ACACIA_DOOR:
            case ACACIA_FENCE:
            case ACACIA_FENCE_GATE:
            case ANVIL:
            case BEACON:
            case BIRCH_DOOR:
            case BIRCH_FENCE:
            case BIRCH_FENCE_GATE:
            case BED:
            case BREWING_STAND:
            case BRICK_STAIRS:
            case CAKE:
            case CAULDRON:
            case CHEST:
            case DARK_OAK_DOOR:
            case DARK_OAK_FENCE:
            case DARK_OAK_FENCE_GATE:
            case DARK_OAK_STAIRS:
            case DAYLIGHT_DETECTOR:
            case DISPENSER:
            case DRAGON_EGG:
            case DROPPER:
            case ENDER_CHEST:
            case FLOWER_POT:
            case FURNACE:
            case HOPPER:
            case IRON_DOOR:
            case JUKEBOX:
            case JUNGLE_DOOR:
            case JUNGLE_FENCE:
            case JUNGLE_FENCE_GATE:
            case LEVER:
            case NOTE_BLOCK:
            case QUARTZ_STAIRS:
            case REDSTONE_ORE:
            case REDSTONE_WIRE:
            case SPRUCE_DOOR:
            case SPRUCE_FENCE:
            case SPRUCE_FENCE_GATE:
            case TRAPPED_CHEST:
                return true;
        }
        return false;
    }

    @Override
    public boolean isCarpet(Block block) {
        return block.getType() == Material.CARPET;
    }

    @Override
    public boolean isGround(Block block) {
        return false;
    }
}
