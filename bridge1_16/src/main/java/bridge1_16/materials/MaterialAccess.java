package bridge1_16.materials;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Material API for 1.16
 */
public final class MaterialAccess implements bridge.material.MaterialAccess {

    @Override
    public Material getMaterial(String name) {
        return Material.getMaterial(name);
    }

    @Override
    public ItemStack createItem(String material) {
        return new ItemStack(getMaterial(material));
    }

    @Override
    public ItemStack createItem(String material, short data) {
        return createItem(material);
    }

}
