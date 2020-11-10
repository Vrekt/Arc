package arc.listener.inventory;

import arc.Arc;
import arc.check.CheckType;
import arc.data.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

/**
 * Listens for inventory type actions
 * TODO: FastConsume
 */
public final class InventoryListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private void onConsume(PlayerItemConsumeEvent event) {
        // Check if we consume milk for the bad effects check.
        if (event.getItem().getType() == Material.MILK_BUCKET) {
            // if so, exempt for 500 ms, and remove all effects.
            final var player = event.getPlayer();
            Arc.arc().exemptions().addExemption(player, CheckType.BAD_EFFECTS, 500);
            final var data = PlayerData.get(player);
            data.removeEffects();
        }
    }

}
