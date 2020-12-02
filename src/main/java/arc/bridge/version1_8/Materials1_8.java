package arc.bridge.version1_8;

import arc.bridge.MaterialsBridge;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Gate;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;

/**
 * Legacy materials API.
 */
@SuppressWarnings("Deprecated")
public final class Materials1_8 implements MaterialsBridge {

    @Override
    public boolean isFence(Block block) {
        switch (block.getType()) {
            case LEGACY_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case LEGACY_IRON_FENCE:
            case JUNGLE_FENCE:
            case NETHER_BRICK_FENCE:
            case SPRUCE_FENCE:
                return true;
        }
        return false;
    }

    @Override
    public boolean isSlab(Block block) {
        return block.getType().getData().equals(Step.class);
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
        return block.getType() == Material.LEGACY_LADDER || block.getType() == Material.LEGACY_VINE;
    }

    @Override
    public boolean isLiquid(Block block) {
        return block.getType() == Material.LEGACY_STATIONARY_LAVA || block.getType() == Material.LEGACY_LAVA || block.getType() == Material.LEGACY_WATER || block.getType() == Material.LEGACY_STATIONARY_WATER;
    }

}


