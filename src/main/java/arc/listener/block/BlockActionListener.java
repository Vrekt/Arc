package arc.listener.block;

import arc.Arc;
import arc.check.block.blockbreak.BlockBreakReach;
import arc.check.block.blockinteract.BlockInteractReach;
import arc.check.block.blockplace.BlockPlaceReach;
import arc.check.types.CheckType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listens for block actions.
 * <p>
 * Interactions, breaking, placing, etc.
 */
public final class BlockActionListener implements Listener {

    /**
     * Break
     */
    private final BlockBreakReach blockBreakReach;

    /**
     * Place
     */
    private final BlockPlaceReach blockPlaceReach;

    /**
     * Interact
     */
    private final BlockInteractReach blockInteractReach;

    public BlockActionListener() {
        blockBreakReach = Arc.getInstance().getCheckManager().getCheck(CheckType.BLOCK_BREAK_REACH);
        blockPlaceReach = Arc.getInstance().getCheckManager().getCheck(CheckType.BLOCK_PLACE_REACH);
        blockInteractReach = Arc.getInstance().getCheckManager().getCheck(CheckType.BLOCK_INTERACT_REACH);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onBreak(BlockBreakEvent event) {
        if (blockBreakReach.check(event.getPlayer(), event.getPlayer().getLocation(), event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onPlace(BlockPlaceEvent event) {
        if (blockPlaceReach.check(event.getPlayer(), event.getPlayer().getLocation(), event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (blockInteractReach.check(event.getPlayer(), event.getPlayer().getLocation(), event.getClickedBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

}
