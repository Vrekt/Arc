package bridge;

/**
 * Supported versions
 */
public enum Version {

    /**
     * 1.8
     */
    VERSION_1_8(0, "1.8.8", "version-1-8-8"),
    /**
     * 1.12
     */
    VERSION_1_12(1, "1.12", "version-1-12"),
    /**
     * 1.16
     */
    VERSION_1_16(2, "1.16", "version-1-16");

    /**
     * The number
     */
    private final int number;

    /**
     * Version string
     * Friendly name for configuration
     */
    private final String version, friendlyName;

    Version(int number, String version, String friendlyName) {
        this.number = number;
        this.version = version;
        this.friendlyName = friendlyName;
    }

    /**
     * @return the version string
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return the config friendly name
     */
    public String getFriendlyName() {
        return friendlyName;
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
     * Check if this version is older than the other
     *
     * @param other the other
     * @return {@code true} if so
     */
    public boolean isOlderThan(Version other) {
        return this.number < other.number;
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

    /**
     * Check if the string version is compatible
     *
     * @param version the version
     * @return the version or {@code null} if not compatible.
     */
    public static Version isCompatible(String version) {
        for (Version value : values()) {
            if (version.contains(value.version)) return value;
        }
        return null;
    }
}
