package bridge;

/**
 * A basic bridge.
 */
public interface Bridge {

    /**
     * The current version
     *
     * @return the version
     */
    Version current();

    /**
     * The bridge materials data
     *
     * @return the {@link MaterialsBridge}
     */
    MaterialsBridge materials();

}
