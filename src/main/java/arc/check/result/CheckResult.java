package arc.check.result;

import arc.check.CheckSubType;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Initialize this check result
     *
     * @param result the result
     */
    public CheckResult(Result result) {
        this.result = result;
        setFailed();
    }

    /**
     * Initialize this check result.
     *
     * @param result the result
     * @param type   the type
     */
    public CheckResult(Result result, CheckSubType type) {
        this.result = result;
        setFailed(type);
    }

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
     * Add a parameter
     *
     * @param parameter the parameter
     * @param value     the value
     */
    public void parameter(String parameter, Object value) {
        informationBuilder.append("\n").append(ChatColor.GRAY);
        informationBuilder.append(parameter).append("=").append(value.toString());
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

}
