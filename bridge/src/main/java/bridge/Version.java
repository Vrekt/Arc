package bridge;

/**
 * Supported versions
 */
public enum Version {

    /**
     * 1.8
     */
    VERSION_1_8(0),
    /**
     * 1.15
     */
    VERSION_1_15(1),
    /**
     * 1.16
     */
    VERSION_1_16(2);

    /**
     * The number
     */
    private final int number;

    Version(int number) {
        this.number = number;
    }

    /**
     * Check if this version is newer than the other
     *
     * @param other the other
     * @return {@code true} if so
     */
    public boolean isNewerThan(Version other) {
        return this.number > other.number;
    }

    /**
     * Check if {@code v1} is newer than {@code v2}
     *
     * @param v1 version 1
     * @param v2 version 2
     * @return {@code true} if so
     */
    public static boolean isNewerThan(Version v1, Version v2) {
        return v1.number > v2.number;
    }

}
