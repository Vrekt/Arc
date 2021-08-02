package arc.api.events;

import arc.check.Check;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * An event for when a player will be banned in X amount of time for a check.
 */
public final class PlayerPendingKickEvent extends PlayerEvent implements Cancellable {

    /**
     * Handler list
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The check
     */
    private final Check check;

    /**
     * The delay
     */
    private long delay;

    /**
     * If this event is cancelled.
     */
    private boolean cancelled;

    public PlayerPendingKickEvent(Player who, Check check, long delay) {
        super(who);

        this.check = check;
        this.delay = delay;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * @return the check banned for
     */
    public Check getCheck() {
        return check;
    }

    /**
     * @return the ban delay
     */
    public long getDelay() {
        return delay;
    }

    /**
     * Change the delay
     *
     * @param delay the delay
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * @return list
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
