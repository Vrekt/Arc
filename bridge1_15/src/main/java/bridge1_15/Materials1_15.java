package bridge1_15;

import bridge.MaterialsBridge;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.*;

/**
 * Current materials API
 */
public final class Materials1_15 implements MaterialsBridge {

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
                || block.getType() == Material.VINE;
    }

    @Override
    public boolean isLiquid(Block block) {
        return block.isLiquid();
    }
}
