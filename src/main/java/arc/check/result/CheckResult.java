package arc.check.result;

import arc.Arc;
import arc.check.types.CheckSubType;
import org.bukkit.ChatColor;

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
     * If we have failed at all.
     */
    private boolean failedBefore;

    /**
     * Empty
     */
    public CheckResult() {

    }

    /**
     * Create a result from another
     *
     * @param other the other
     */
    public CheckResult(CheckResult other) {
        this.result = other.result;
        this.subType = other.subType;
        this.failedBefore = other.failedBefore;
        this.informationBuilder = other.informationBuilder;
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
        if (informationBuilder != null && informationBuilder.length() != 0) {
            Arc.getPlugin().getLogger().warning("A check is not resetting the check result, information: \n" + information);
        }

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
        this.failedBefore = true;
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
     * @return if the player has failed
     */
    public boolean failed() {
        return result == Result.FAILED;
    }

    /**
     * @return if the player has failed before.
     */
    public boolean hasFailedBefore() {
        return failedBefore;
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
     * Reset this result
     */
    public void reset() {
        if (result == Result.FAILED) {
            result = Result.PASSED;
            informationBuilder.setLength(0);
        }
    }
}
