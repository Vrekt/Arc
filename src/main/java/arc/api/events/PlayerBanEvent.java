package arc.api.events;

import arc.check.Check;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.Date;

/**
 * An event for when a player gets banned for a check.
 */
public final class PlayerBanEvent extends PlayerEvent implements Cancellable {

    /**
     * Handler list
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The check
     */
    private final Check check;

    /**
     * The ban date
     */
    private Date date;

    /**
     * The delay
     */
    private int delay;

    /**
     * If this event is cancelled.
     */
    private boolean cancelled;

    public PlayerBanEvent(Player who, Check check, Date date, int delay) {
        super(who);

        this.check = check;
        this.date = date;
        this.delay = delay;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * @return the check banned for
     */
    public Check check() {
        return check;
    }

    /**
     * @return the date
     */
    public Date date() {
        return date;
    }

    /**
     * Change the ban date.
     *
     * @param date the date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the ban delay
     */
    public int delay() {
        return delay;
    }

    /**
     * Change the delay
     *
     * @param delay the delay
     */
    public void setDelay(int delay) {
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
