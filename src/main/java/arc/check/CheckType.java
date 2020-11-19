package arc.check;

/**
 * Tells what type the check is
 */
public enum CheckType {
    /**
     * Checks if the player is taking fall damage
     */
    NOFALL("NoFall"),
    /**
     * Checks if the player is flying
     */
    FLIGHT("Flight"),
    /**
     * Checks if the player is moving fast
     */
    SPEED("Speed"),
    /**
     * Checks if the player is using critical hits while impossible.
     */
    CRITICALS("Criticals"),

    /**
     * Checks if the player is sending too many packets.
     */
    MORE_PACKETS("MorePackets"),

    /**
     * Checks if the player is removing bad effects.
     */
    BAD_EFFECTS("BadEffects"),

    /**
     * Checks if the player is sending too many swing packets.
     */
    SWING_FREQUENCY("SwingFrequency"),

    /**
     * Checks the payload packet and frequency
     */
    PAYLOAD_FREQUENCY("PayloadFrequency");

    /**
     * The name
     */
    private final String name;

    CheckType(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
