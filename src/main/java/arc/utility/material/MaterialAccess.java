package arc.utility.material;

import arc.Arc;
import bridge.Version;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Material access
 */
public final class MaterialAccess {

    /**
     * Access
     */
    private static final bridge.material.MaterialAccess ACCESS = Arc.getBridge().getMaterialAccess();

    /**
     * Firework rocket.
     */
    public static final Material FIREWORK_ROCKET;

    static {
        FIREWORK_ROCKET = Arc.getMCVersion() == Version.VERSION_1_8 ? ACCESS.getMaterial("FIREWORK")
                : ACCESS.getMaterial("FIREWORK_ROCKET");
    }

    /**
     * Get a material
     *
     * @param name the name
     * @return the name
     */
    public static Material getMaterial(String name) {
        return ACCESS.getMaterial(name);
    }

    /**
     * Create an item
     *
     * @param material the material name
     * @return the item
     */
    public static ItemStack createItem(String material) {
        return ACCESS.createItem(material);
    }

    /**
     * Create an item
     *
     * @param material the material
     * @param data     the data
     * @return the item
     */
    public static ItemStack createItem(String material, short data) {
        return ACCESS.createItem(material, data);
    }
}
