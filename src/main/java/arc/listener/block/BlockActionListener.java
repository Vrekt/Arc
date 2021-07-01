package arc.listener.block;

import arc.Arc;
import arc.check.block.Nuker;
import arc.check.block.blockbreak.BlockBreakReach;
import arc.check.block.blockinteract.BlockInteractReach;
import arc.check.block.blockplace.BlockPlaceReach;
import arc.check.types.CheckType;
import org.bukkit.entity.Player;
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

    /**
     * Nuker.
     */
    private final Nuker nuker;

    public BlockActionListener() {
        blockBreakReach = Arc.getInstance().getCheckManager().getCheck(CheckType.BLOCK_BREAK_REACH);
        blockPlaceReach = Arc.getInstance().getCheckManager().getCheck(CheckType.BLOCK_PLACE_REACH);
        blockInteractReach = Arc.getInstance().getCheckManager().getCheck(CheckType.BLOCK_INTERACT_REACH);
        nuker = Arc.getInstance().getCheckManager().getCheck(CheckType.NUKER);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (!nuker.isPacketCheck()) event.setCancelled(nuker.check(player));
        if (blockBreakReach.check(player, player.getLocation(), event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        if (blockPlaceReach.check(player, player.getLocation(), event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            final Player player = event.getPlayer();

            if (blockInteractReach.check(player, player.getLocation(), event.getClickedBlock().getLocation()))
                event.setCancelled(true);
        }
    }

}
