package arc.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps track of inventory UIs and invokes events.
 * TODO: Remove player on disconnect.
 */
public final class InventoryRegister implements Listener {

    /**
     * Keeps track of Inventory UIs
     */
    private final Map<Player, InventoryCreator> inventoryMap = new ConcurrentHashMap<>();

    /**
     * Register
     *
     * @param player  the player
     * @param builder the builder
     */
    public void register(Player player, InventoryCreator builder) {
        inventoryMap.put(player, builder);
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            final Player player = (Player) event.getPlayer();
            inventoryMap.remove(player);
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            final Player player = (Player) event.getWhoClicked();
            if (inventoryMap.containsKey(player)) {
                final InventoryCreator creator = inventoryMap.get(player);
                final Inventory inventory = event.getClickedInventory();
                if (creator.isThisInventory(inventory)) {
                    event.setCancelled(true);

                    final ItemStack item = event.getCurrentItem();
                    if (!creator.isEmptySlotItem(item)) {
                        creator.invokeConsumer(item);
                    }
                } else {
                    inventoryMap.remove(player);
                }
            }
        }
    }

}
