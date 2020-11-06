package arc.check.result;

/**
 * Represents a check result.
 */
public final class CheckResult {

    public enum Result {

        PASSED, FAILED

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
     * @return if we failed.
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
