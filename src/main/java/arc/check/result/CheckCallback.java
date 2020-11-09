package arc.check.result;

import arc.violation.result.ViolationResult;

/**
 * A check callback for results
 */
public interface CheckCallback {


    /**
     * Invoked when the result is received.
     *
     * @param checkResult     the check result
     * @param violationResult the violation result
     */
    void onResult(CheckResult checkResult, ViolationResult violationResult);

}
