package arc.api.events;

import arc.check.CheckType;
import arc.check.result.CheckResult;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Invoked when a player violates a check.
 */
public final class PlayerViolationEvent extends PlayerEvent implements Cancellable {

    /**
     * Handler list
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The check failed.
     */
    private final CheckType checkFailed;

    /**
     * The violation level
     */
    private final int violationLevel;

    /**
     * The check result.
     */
    private final CheckResult result;

    /**
     * If this event is cancelled.
     */
    private boolean cancelled;

    /**
     * Initialize this event
     *
     * @param who            who failed
     * @param checkFailed    the check failed
     * @param violationLevel the violation level
     * @param result         the check result
     */
    public PlayerViolationEvent(Player who, CheckType checkFailed, int violationLevel, CheckResult result) {
        super(who);
        this.checkFailed = checkFailed;
        this.violationLevel = violationLevel;
        this.result = result;
    }

    /**
     * @return the check that was failed.
     */
    public CheckType checkFailed() {
        return checkFailed;
    }

    /**
     * @return the violation level
     */
    public int violationLevel() {
        return violationLevel;
    }

    /**
     * @return the result
     */
    public CheckResult result() {
        return result;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
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
