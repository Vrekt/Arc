package arc.check.result;

import arc.violation.result.ViolationResult;

/**
 * A check callback for results
 * TODO: May or may not be used
 */
public interface CheckCallback {

    /**
     * Invoked when the result is received.
     *
     * @param result the result
     */
    void onResult(CheckResult checkResult, ViolationResult violationResult);

}
