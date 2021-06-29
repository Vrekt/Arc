package bridge.material;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A material API.
 */
public interface MaterialAccess {

    /**
     * Get a material
     *
     * @param name the name
     * @return the name
     */
    Material getMaterial(String name);

    /**
     * Create an item
     *
     * @param material the material name
     * @return the item
     */
    ItemStack createItem(String material);

    /**
     * Create an item
     *
     * @param material the material
     * @param data     the data
     * @return the item
     */
    ItemStack createItem(String material, short data);

}
