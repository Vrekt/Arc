package arc.check;

/**
 * Tells what type the check is
 */
public enum CheckType {
    /**
     * Checks if the player is taking fall damage
     */
    NOFALL,
    /**
     * Checks if the player is flying
     */
    FLIGHT,
    /**
     * Checks if the player is moving fast
     */
    SPEED,
    /**
     * Checks if the player is using critical hits while impossible.
     */
    CRITICALS,

    /**
     * Checks if the player is sending too many packets.
     */
    MORE_PACKETS,

    /**
     * Checks if the player is removing bad effects.
     */
    BAD_EFFECTS,

    /**
     * Checks if the player is sending too many swing packets.
     */
    SWING_FREQUENCY,

    /**
     * Checks the payload packet and frequency
     */
    PAYLOAD_FREQUENCY;

}
