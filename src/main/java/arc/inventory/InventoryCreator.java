package arc.inventory;

import arc.Arc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
     * The event listener for this inventory
     */
    private final TemporaryInventoryListener listener = new TemporaryInventoryListener();

    /**
     * Last index
     */
    private int lastIndex = 0;

    /**
     * The empty slot item
     */
    private ItemStack emptySlotItem;

    /**
     * The player who this inventory is for.
     */
    private Player player;

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
     * @param onClick       the consumer
     * @return this
     */
    public InventoryCreator item(ItemStack item, int indexModifier, Consumer<ItemStack> onClick) {
        inventory.setItem(lastIndex, item);
        itemConsumers.put(item.getType(), onClick);
        lastIndex += indexModifier;
        return this;
    }

    /**
     * Set an item
     *
     * @param item          the item
     * @param indexModifier the index modifier
     * @return this
     */
    public InventoryCreator item(ItemStack item, int indexModifier) {
        inventory.setItem(lastIndex, item);
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
        this.player = player;
        player.openInventory(inventory);
        Arc.plugin().getServer().getPluginManager().registerEvents(listener, Arc.plugin());
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

    /**
     * A temporary inventory listener.
     */
    private final class TemporaryInventoryListener implements Listener {
        @EventHandler
        private void onInventoryClose(InventoryCloseEvent event) {
            final Player player = (Player) event.getPlayer();
            if (InventoryCreator.this.player.getUniqueId().equals(player.getUniqueId())) {
                // we have the same player, cleanup.
                cleanup();
            }
        }

        @EventHandler
        private void onInventoryClick(InventoryClickEvent event) {
            if (event.getWhoClicked() instanceof Player) {
                final Player player = (Player) event.getWhoClicked();
                if (InventoryCreator.this.player.getUniqueId().equals(player.getUniqueId())) {
                    final Inventory inventory = event.getClickedInventory();
                    if (isThisInventory(inventory)) {
                        // if we have the same player and inventory!
                        event.setCancelled(true);

                        final ItemStack item = event.getCurrentItem();
                        if (item != null && !isEmptySlotItem(item)) invokeConsumer(item);
                    } else {
                        cleanup();
                    }
                }
            }
        }

        @EventHandler
        private void onQuit(PlayerQuitEvent event) {
            // handle quitting just in-case i suppose
            final Player player = event.getPlayer();
            if (InventoryCreator.this.player.getUniqueId().equals(player.getUniqueId())) {
                cleanup();
            }
        }

        /**
         * Cleanup.
         */
        private void cleanup() {
            HandlerList.unregisterAll(this);
            itemConsumers.clear();
            InventoryCreator.this.player = null;
            InventoryCreator.this.emptySlotItem = null;
        }

    }

}
