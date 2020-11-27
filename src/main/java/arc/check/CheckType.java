package arc.check;

/**
 * Tells what type the check is
 */
public enum CheckType {
    /**
     * Checks if the player is taking fall damage
     */
    NOFALL("NoFall", CheckCategory.MOVING),
    /**
     * Checks if the player is flying
     */
    FLIGHT("Flight", CheckCategory.MOVING),
    /**
     * Checks if the player is moving fast
     */
    SPEED("Speed", CheckCategory.MOVING),
    /**
     * Checks if the player is using critical hits while impossible.
     */
    CRITICALS("Criticals", CheckCategory.COMBAT),

    /**
     * Checks if the player is sending too many packets.
     */
    MORE_PACKETS("MorePackets", CheckCategory.MOVING),

    /**
     * Checks if the player is sending too many swing packets.
     */
    SWING_FREQUENCY("SwingFrequency", CheckCategory.NETWORK),

    /**
     * Checks the payload packet and frequency
     */
    PAYLOAD_FREQUENCY("PayloadFrequency", CheckCategory.NETWORK),

    /**
     * Checks if the player is regenerating health too fast.
     */
    REGENERATION("Regeneration", CheckCategory.PLAYER),

    /**
     * Checks if the player is walking on water.
     */
    JESUS("Jesus", CheckCategory.MOVING),

    /**
     * Checks if the player is using items too fast.
     */
    FAST_USE("FastUse", CheckCategory.PLAYER),

    /**
     * Checks if the player is attacking too far.
     */
    REACH("Reach", CheckCategory.COMBAT),

    /**
     * Checks if the player isn't swinging their arm when attacking
     */
    NO_SWING("NoSwing", CheckCategory.COMBAT);

    /**
     * The name
     */
    private final String name;

    /**
     * The category
     */
    private final CheckCategory category;

    CheckType(String name, CheckCategory category) {
        this.name = name;
        this.category = category;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the category
     */
    public CheckCategory category() {
        return category;
    }
}
