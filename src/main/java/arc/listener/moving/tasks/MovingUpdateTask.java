package arc.listener.moving.tasks;

import arc.Arc;
import arc.data.moving.MovingData;
import arc.utility.MovingUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Updates the players moving data if they have not moved.
 * TODO: Run this at a tick-rate of 10? For now its 20.
 */
public final class MovingUpdateTask implements Runnable {

    /**
     * This task
     */
    private BukkitTask thisTask;

    /**
     * Start
     */
    public void start() {
        thisTask = Bukkit.getScheduler().runTaskTimer(Arc.plugin(), this, 20, 20);
    }

    /**
     * Stop
     */
    public void stop() {
        thisTask.cancel();
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final MovingData data = MovingData.get(player);

            // update this players moving data if they haven't moved in the last half-second;
            // only update if we have moved.
            // TODO: More work needed
            if (data.lastMovingUpdate() >= 500) {
                final Location to = data.to();
                final Location playerLocation = player.getLocation();
                if (to.getX() != playerLocation.getX() || to.getY() != playerLocation.getY() || to.getZ() != playerLocation.getZ()) {
                    MovingUtil.updateMovingPlayer(data, data.to(), player.getLocation());
                } else {
                    data.lastMovingUpdate(System.currentTimeMillis());
                }
            }
        }
    }
}
