package arc.listener.moving.task;

import arc.Arc;
import arc.check.moving.Flight;
import arc.check.types.CheckType;
import arc.data.moving.MovingData;
import arc.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * A task ran every 10 ticks to update the player status and check things.
 */
public final class MovingTaskListener implements Runnable {

    /**
     * Flight check
     */
    private final Flight flight;

    public MovingTaskListener() {
        flight = Arc.getInstance().getCheckManager().getCheck(CheckType.FLIGHT);
        if (flight.isEnabled()) Bukkit.getScheduler().runTaskTimer(Arc.getPlugin(), this, 10, 10);
    }

    @Override
    public void run() {
        final long now = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            // make sure we are in a safe world.
            if (WorldManager.isEnabledInWorld(player) && (player.getGameMode() == GameMode.SURVIVAL
                    || player.getGameMode() == GameMode.ADVENTURE)) {
                final MovingData data = MovingData.get(player);
                if (now - data.lastMovingUpdate() >= 500) {
                    // player hasn't moved in a while, check-in.
                    checkIn(player, data);
                }
            }
        }
    }

    /**
     * Check in
     *
     * @param player the player
     * @param data   their data
     */
    private void checkIn(Player player, MovingData data) {
        if (!data.onGround()) flight.checkNoMovement(player, data);
    }

}
