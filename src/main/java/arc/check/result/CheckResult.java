package arc.check.result;

import arc.check.CheckSubType;
import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Represents a check result.
 */
public final class CheckResult {

    public enum Result {

        /**
         * The player has passed
         */
        PASSED,
        /**
         * The player has failed
         */
        FAILED,
    }

    /**
     * The result
     */
    private Result result = Result.PASSED;

    /**
     * The check sub-type
     */
    private CheckSubType subType;

    /**
     * Information builder.
     */
    private StringBuilder informationBuilder;

    /**
     * The cancel location
     */
    private Location cancel;

    /**
     * The cancel type
     */
    private CancelType cancelType;

    /**
     * Empty
     */
    public CheckResult() {

    }

    /**
     * @return the result
     */
    public Result result() {
        return result;
    }

    /**
     * Set the result
     *
     * @param result the result
     */
    public void result(Result result) {
        this.result = result;
    }

    /**
     * Set failed
     *
     * @param type the sub-type
     */
    public CheckResult setFailed(CheckSubType type) {
        setFailed();
        this.subType = type;
        return this;
    }

    /**
     * Set failed
     *
     * @param type        the sub-type
     * @param information the information
     */
    public CheckResult setFailed(CheckSubType type, String information) {
        setFailed(type);
        info(information);
        return this;
    }

    /**
     * Set failed
     *
     * @param information the initial information
     */
    public CheckResult setFailed(String information) {
        setFailed();
        info(information);
        return this;
    }

    /**
     * Attach parameter debug information to this result.
     *
     * @param parameter the parameter
     * @param value     the value
     * @return this
     */
    public CheckResult withParameter(String parameter, Object value) {
        if (informationBuilder == null) informationBuilder = new StringBuilder();
        informationBuilder.append("\n").append(ChatColor.GRAY);
        informationBuilder.append(parameter).append("=").append(value.toString());
        return this;
    }

    /**
     * Set failed
     */
    public CheckResult setFailed() {
        this.result = Result.FAILED;
        this.informationBuilder = new StringBuilder();
        return this;
    }

    /**
     * Add an information line
     *
     * @param information the information
     */
    public void info(String information) {
        informationBuilder.append(ChatColor.RED).append(information);
        informationBuilder.append("\n");
    }

    /**
     * Set where to cancel to
     *
     * @param location the location
     */
    public void cancelTo(Location location, CancelType type) {
        this.cancel = location;
        this.cancelType = type;
    }

    /**
     * @return if the player has failed
     */
    public boolean failed() {
        return result == Result.FAILED;
    }

    /**
     * @return retrieve the information
     */
    public String information() {
        return informationBuilder.toString();
    }

    /**
     * @return the sub-type
     */
    public CheckSubType subType() {
        return subType;
    }

    /**
     * @return check if this result has a sub-type.
     */
    public boolean hasSubType() {
        return subType != null;
    }

    /**
     * @return the cancel location
     */
    public Location cancel() {
        return cancel;
    }

    /**
     * @return the cancel type
     */
    public CancelType cancelType() {
        return cancelType;
    }

    /**
     * Reset this result
     */
    public void reset() {
        if (result == Result.FAILED) {
            cancel = null;
            result = Result.PASSED;
            informationBuilder.setLength(0);
        }
    }

}
