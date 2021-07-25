package arc.api.events;

import arc.check.Check;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.Date;

/**
 * An event for when a player will be banned in X amount of time for a check.
 */
public final class PlayerPendingBanEvent extends PlayerEvent implements Cancellable {

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
    private long delay;

    /**
     * If Arc will use lite bans.
     */
    private boolean willUseLiteBans;

    /**
     * If this event is cancelled.
     */
    private boolean cancelled;

    public PlayerPendingBanEvent(Player who, Check check, Date date, long delay, boolean willUseLiteBans) {
        super(who);

        this.check = check;
        this.date = date;
        this.delay = delay;
        this.willUseLiteBans = willUseLiteBans;
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
     * This field will be ignored if {@code willUseLiteBans} is {@code true}
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Change the ban date.
     * This field will be ignored if {@code willUseLiteBans} is {@code true}
     *
     * @param date the date
     */
    public void setDate(Date date) {
        this.date = date;
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

    /**
     * @return if arc will use lite bans
     */
    public boolean willUseLiteBans() {
        return willUseLiteBans;
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
