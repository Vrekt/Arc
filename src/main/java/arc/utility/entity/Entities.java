package arc.utility.entity;

import arc.Arc;
import bridge.BoundingBox;
import org.bukkit.entity.Entity;

/**
 * Entity util.
 */
public final class Entities {

    /**
     * Get the bounding box of an entity
     *
     * @param entity the entity
     * @return the {@link BoundingBox}
     */
    public static BoundingBox getBoundingBox(Entity entity) {
        return Arc.bridge().entities().getBoundingBox(entity);
    }

}
