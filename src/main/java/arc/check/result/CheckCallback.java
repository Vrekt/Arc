package arc.check.result;

import arc.violation.result.ViolationResult;

/**
 * A check callback for results
 */
public interface CheckCallback {

    /**
     * Invoked when the result is received.
     *
     * @param violationResult the violation result
     */
    void onResult(ViolationResult violationResult);

}
