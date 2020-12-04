package bridge1_8;

import bridge.materials.MaterialsBridge;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.Gate;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.material.TrapDoor;

/**
 * Legacy materials API.
 */
@SuppressWarnings("Deprecated")
public final class Materials1_8 implements MaterialsBridge {

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
}


