package arc.listener.block;

import arc.Arc;
import arc.check.block.Nuker;
import arc.check.types.CheckType;
import arc.utility.block.BlockAccess;
import arc.world.WorldManager;
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
    private final arc.check.block.blockbreak.Reach blockBreakReach;

    /**
     * Place
     */
    private final arc.check.block.blockplace.Reach blockPlaceReach;

    /**
     * Interact
     */
    private final arc.check.block.blockinteract.Reach blockInteractReach;

    /**
     * Break NoSwing
     */
    private final arc.check.block.blockbreak.NoSwing blockBreakNoSwing;

    /**
     * Place NoSwing
     */
    private final arc.check.block.blockplace.NoSwing blockPlaceNoSwing;

    /**
     * Interact NoSwing
     */
    private final arc.check.block.blockinteract.NoSwing blockInteractNoSwing;

    /**
     * Nuker.
     */
    private final Nuker nuker;

    public BlockActionListener() {
        blockBreakReach = Arc.getInstance().getCheckManager().getCheck(CheckType.BLOCK_BREAK_REACH);
        blockPlaceReach = Arc.getInstance().getCheckManager().getCheck(CheckType.BLOCK_PLACE_REACH);
        blockInteractReach = Arc.getInstance().getCheckManager().getCheck(CheckType.BLOCK_INTERACT_REACH);
        blockBreakNoSwing = Arc.getInstance().getCheckManager().getCheck(CheckType.BLOCK_BREAK_NO_SWING);
        blockInteractNoSwing = Arc.getInstance().getCheckManager().getCheck(CheckType.BLOCK_INTERACT_NO_SWING);
        blockPlaceNoSwing = Arc.getInstance().getCheckManager().getCheck(CheckType.BLOCK_PLACE_NO_SWING);
        nuker = Arc.getInstance().getCheckManager().getCheck(CheckType.NUKER);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (!WorldManager.isEnabledInWorld(player)) return;

        if (!nuker.isPacketCheck()) event.setCancelled(nuker.check(player));
        if (blockBreakReach.check(player, player.getLocation(), event.getBlock().getLocation()))
            event.setCancelled(true);
        if (blockBreakNoSwing.check(player)) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (!WorldManager.isEnabledInWorld(player)) return;

        if (blockPlaceReach.check(player, player.getLocation(), event.getBlock().getLocation()))
            event.setCancelled(true);
        if (blockPlaceNoSwing.check(player)) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null
                && BlockAccess.isInteractable(event.getClickedBlock())) {
            final Player player = event.getPlayer();
            if (!WorldManager.isEnabledInWorld(player)) return;

            if (blockInteractReach.check(player, player.getLocation(), event.getClickedBlock().getLocation()))
                event.setCancelled(true);
            if (blockInteractNoSwing.check(player)) event.setCancelled(true);
        }
    }

}
