package bridge1_8.materials;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Legacy materials API.
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
        return new ItemStack(getMaterial(material), 1, data);
    }
}



