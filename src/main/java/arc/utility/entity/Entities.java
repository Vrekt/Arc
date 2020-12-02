package arc.utility.entity;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.inventivetalent.boundingbox.BoundingBox;
import org.inventivetalent.boundingbox.BoundingBoxAPI;

import java.util.HashMap;
import java.util.Map;

/**
 * Entity util.
 */
public final class Entities {
    /**
     * Stores entity bounds.
     */
    private static final Map<EntityType, Pair<Float, Float>> ENTITY_BOUNDS = new HashMap<>();

    static {
        ENTITY_BOUNDS.put(EntityType.PLAYER, Pair.of(0.6f, 1.8f));
        ENTITY_BOUNDS.put(EntityType.ENDER_DRAGON, Pair.of(16.0f, 8.0f));
        ENTITY_BOUNDS.put(EntityType.WITHER, Pair.of(0.9f, 3.5f));
        ENTITY_BOUNDS.put(EntityType.CHICKEN, Pair.of(0.4f, 0.7f));
        ENTITY_BOUNDS.put(EntityType.WOLF, Pair.of(0.6f, 0.8f));
        ENTITY_BOUNDS.put(EntityType.VILLAGER, Pair.of(0.6f, 1.8f));
        ENTITY_BOUNDS.put(EntityType.SQUID, Pair.of(0.95f, 0.95f));
        ENTITY_BOUNDS.put(EntityType.SHEEP, Pair.of(0.9f, 1.3f));
        ENTITY_BOUNDS.put(EntityType.RABBIT, Pair.of(0.6f, 0.7f));
        ENTITY_BOUNDS.put(EntityType.PIG, Pair.of(0.9f, 0.9f));
        ENTITY_BOUNDS.put(EntityType.OCELOT, Pair.of(0.6f, 0.7f));
        ENTITY_BOUNDS.put(EntityType.MUSHROOM_COW, Pair.of(0.9f, 1.3f));
        ENTITY_BOUNDS.put(EntityType.HORSE, Pair.of(1.4f, 1.6f));
        ENTITY_BOUNDS.put(EntityType.COW, Pair.of(0.9f, 1.3f));
        ENTITY_BOUNDS.put(EntityType.BAT, Pair.of(0.5f, 0.9f));
        ENTITY_BOUNDS.put(EntityType.ZOMBIE, Pair.of(0.6f, 1.95f));
        ENTITY_BOUNDS.put(EntityType.WITCH, Pair.of(0.6f, 1.95f));
        ENTITY_BOUNDS.put(EntityType.SPIDER, Pair.of(1.4f, 0.9f));
        ENTITY_BOUNDS.put(EntityType.SNOWMAN, Pair.of(0.7f, 1.9f));
        ENTITY_BOUNDS.put(EntityType.SKELETON, Pair.of(0.6f, 1.95f));
        ENTITY_BOUNDS.put(EntityType.SILVERFISH, Pair.of(0.4f, 0.3f));
        ENTITY_BOUNDS.put(EntityType.IRON_GOLEM, Pair.of(1.4f, 2.9f));
        ENTITY_BOUNDS.put(EntityType.GUARDIAN, Pair.of(1.9975f, 1.9975f));
        ENTITY_BOUNDS.put(EntityType.GIANT, Pair.of(0.6f * 6f, 1.95f * 6f));
        ENTITY_BOUNDS.put(EntityType.GHAST, Pair.of(4.0f, 4.0f));
        ENTITY_BOUNDS.put(EntityType.ENDERMITE, Pair.of(0.4f, 0.3f));
        ENTITY_BOUNDS.put(EntityType.ENDERMAN, Pair.of(0.6f, 2.9f));
        ENTITY_BOUNDS.put(EntityType.CAVE_SPIDER, Pair.of(0.7f, 0.5f));
    }

    /**
     * Get the bounding box of an entity
     *
     * @param entity the entity
     * @return the {@link BoundingBox}
     */
    public static BoundingBox getBoundingBox(Entity entity) {
        if (!ENTITY_BOUNDS.containsKey(entity.getType())) {
            return BoundingBoxAPI.getBoundingBox(entity);
        }
        final Pair<Float, Float> bounds = ENTITY_BOUNDS.get(entity.getType());
        final Location location = entity.getLocation();
        final float width = bounds.getLeft() / 2.0F;
        final float height = bounds.getRight();

        return new BoundingBox(location.getX() - width, location.getY(), location.getZ() - width,
                location.getX() + width, location.getY() + height, location.getZ() + width);
    }

}
