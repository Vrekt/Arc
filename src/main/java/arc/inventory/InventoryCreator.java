package arc.inventory;

import arc.Arc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Allows building of inventory UIs
 */
public final class InventoryCreator {

    /**
     * The base inventory
     */
    private final Inventory inventory;

    /**
     * Keeps track of item clicked consumers
     */
    private final Map<Material, Consumer<ItemStack>> itemConsumers = new HashMap<>();

    /**
     * Last index
     */
    private int lastIndex = 0;

    /**
     * The empty slot item
     */
    private ItemStack emptySlotItem;

    /**
     * Initialize this builder
     *
     * @param title the title
     * @param size  the size
     */
    public InventoryCreator(String title, int size) {
        inventory = Bukkit.createInventory(null, size, title);
    }

    /**
     * Set the initial starting index
     *
     * @param index the index
     * @return this
     */
    public InventoryCreator initialIndex(int index) {
        this.lastIndex = index;
        return this;
    }

    /**
     * Set an item
     *
     * @param item          the item
     * @param indexModifier the index modifier
     * @return this
     */
    public InventoryCreator item(ItemStack item, int indexModifier, Consumer<ItemStack> onClick) {
        inventory.setItem(lastIndex, item);
        itemConsumers.put(item.getType(), onClick);
        lastIndex += indexModifier;
        return this;
    }

    /**
     * Replace an item
     *
     * @param item     the item
     * @param modified the modified
     * @return this
     */
    public InventoryCreator replace(ItemStack item, ItemStack modified) {
        for (int i = 0; i < inventory.getSize(); i++) {
            final ItemStack index = inventory.getItem(i);
            if (index != null && index.getType() == item.getType()) {
                inventory.setItem(i, modified);
            }
        }
        return this;
    }

    /**
     * Fill empty slots
     *
     * @param item the item
     * @return this
     */
    public InventoryCreator fillEmptySlots(ItemStack item) {
        emptySlotItem = item;
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, item);
            }
        }
        return this;
    }

    /**
     * Show the inventory
     *
     * @param player the player
     */
    public void show(Player player) {
        Arc.arc().inventoryRegister().register(player, this);
        player.openInventory(inventory);
    }

    /**
     * Check if the provided inventory is equal to this one
     *
     * @param other the other
     * @return {@code true} if so
     */
    public boolean isThisInventory(Inventory other) {
        return inventory.equals(other);
    }

    /**
     * Check if the item is the empty slot one
     *
     * @param other the other
     * @return {@code true} if so
     */
    public boolean isEmptySlotItem(ItemStack other) {
        return emptySlotItem.equals(other);
    }

    /**
     * Invoked consumer
     *
     * @param item the item
     */
    public void invokeConsumer(ItemStack item) {
        if (itemConsumers.containsKey(item.getType())) {
            itemConsumers.get(item.getType()).accept(item);
        }
    }
}
