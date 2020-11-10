package arc.check.result;

/**
 * Represents a check result.
 */
public final class CheckResult {

    /**
     * Empty/Default check result
     */
    public static final CheckResult EMPTY = new CheckResult(Result.PASSED);

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
     * The check information
     */
    private String information;

    /**
     * Initialize this check result with a result
     *
     * @param result the result
     */
    public CheckResult(Result result) {
        this.result = result;
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
     */
    public void setFailed() {
        this.result = Result.FAILED;
    }

    /**
     * Set failed + information
     */
    public void setFailed(String information) {
        this.result = Result.FAILED;
        information(information);
    }

    /**
     * @return if the player has failed
     */
    public boolean failed() {
        return result == Result.FAILED;
    }

    /**
     * Set passed
     */
    public void setPassed() {
        this.result = Result.PASSED;
    }

    /**
     * @return the information
     */
    public String information() {
        return information;
    }

    /**
     * Set the information
     *
     * @param information information
     */
    public void information(String information) {
        this.information = information;
    }

}
