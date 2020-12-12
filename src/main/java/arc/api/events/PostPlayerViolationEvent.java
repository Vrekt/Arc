package arc.api.events;

import arc.check.CheckType;
import arc.violation.result.ViolationResult;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Invoked after the violation has been processed.
 */
public final class PostPlayerViolationEvent extends PlayerEvent {

    /**
     * Handler list
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The violation result.
     */
    private final ViolationResult violationResult;

    /**
     * The check failed.
     */
    private final CheckType checkFailed;

    /**
     * The violation level
     */
    private final int violationLevel;

    /**
     * The violation information
     */
    private final String information;

    /**
     * Initialize this event
     *
     * @param who             who failed
     * @param violationResult the result
     * @param checkFailed     the check failed
     * @param violationLevel  the violation level
     * @param information     the information
     */
    public PostPlayerViolationEvent(Player who, ViolationResult violationResult, CheckType checkFailed, int violationLevel, String information) {
        super(who);
        this.violationResult = violationResult;
        this.checkFailed = checkFailed;
        this.violationLevel = violationLevel;
        this.information = information;
    }

    /**
     * @return the violation result.
     */
    public ViolationResult result() {
        return violationResult;
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
     * @return the information
     */
    public String information() {
        return information;
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
