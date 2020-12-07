package bridge1_15.entities;

import bridge.BoundingBox;
import bridge.entities.EntitiesBridge;
import net.minecraft.server.v1_15_R1.AxisAlignedBB;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

/**
 * Entities for 1.15
 */
public final class Entities implements EntitiesBridge {

    @Override
    public BoundingBox getBoundingBox(Entity entity) {
        final AxisAlignedBB nms = ((CraftEntity) entity).getHandle().getBoundingBox();
        return new BoundingBox(nms.minX, nms.minY, nms.minZ, nms.maxX, nms.maxY, nms.maxZ);
    }
}
