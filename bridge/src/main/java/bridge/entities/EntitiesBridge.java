package bridge.entities;

import bridge.BoundingBox;
import org.bukkit.entity.Entity;

/**
 * Entities bridge
 */
public interface EntitiesBridge {

    /**
     * Get a bounding box
     *
     * @param entity the entity
     * @return the bounding box.
     */
    BoundingBox getBoundingBox(Entity entity);

}
