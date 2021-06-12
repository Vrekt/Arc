package arc.api.events;

import arc.check.CheckSubType;
import arc.check.CheckType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Invoked when a request is sent to check if a player is exempt;
 */
public final class PlayerExemptionCheckEvent extends PlayerEvent {

    /**
     * Handler list
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The check for
     */
    private final CheckType check;

    /**
     * The sub type is applicable
     */
    private final CheckSubType subType;

    /**
     * If the player is exempt.
     */
    private boolean isExempt;

    public PlayerExemptionCheckEvent(Player who, CheckType check, CheckSubType subType, boolean exempt) {
        super(who);
        this.check = check;
        this.subType = subType;
        this.isExempt = exempt;
    }

    /**
     * @return the check or {@code null}
     */
    public CheckType check() {
        return check;
    }

    /**
     * @return the sub-type or {@code null}
     */
    public CheckSubType subType() {
        return subType;
    }

    /**
     * @return if the player is exempt
     */
    public boolean isExempt() {
        return isExempt;
    }

    /**
     * Set if the player is exempt
     *
     * @param exempt exempt
     */
    public void isExempt(boolean exempt) {
        isExempt = exempt;
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

