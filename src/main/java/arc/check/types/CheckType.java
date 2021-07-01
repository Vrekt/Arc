package arc.check.types;

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
    ATTACK_REACH("AttackReach", "Reach (Attack)", CheckCategory.COMBAT),

    /**
     * Checks if the player isn't swinging their arm when attacking
     */
    NO_SWING("NoSwing", CheckCategory.COMBAT),

    /**
     * Kill aura
     */
    KILL_AURA("KillAura", CheckCategory.COMBAT),

    /**
     * Block reach
     */
    BLOCK_BREAK_REACH("BlockBreakReach", "Block Break Reach", "Reach", CheckCategory.BLOCKBREAK),

    /**
     * Block place reach
     */
    BLOCK_PLACE_REACH("BlockPlaceReach", "Block Place Reach", "Reach", CheckCategory.BLOCKPLACE),

    /**
     * Block interact reach
     */
    BLOCK_INTERACT_REACH("BlockInteractReach", "Block Interact Reach", "Reach", CheckCategory.BLOCKINTERACT),

    /**
     * Break no swing
     */
    BLOCK_BREAK_NO_SWING("BlockBreakNoSwing", "Block Break NoSwing", "NoSwing", CheckCategory.BLOCKBREAK),
    /**
     * Place no swing
     */
    BLOCK_PLACE_NO_SWING("BlockPlaceNoSwing", "Block Place NoSwing", "NoSwing", CheckCategory.BLOCKPLACE),
    /**
     * Interact no swing
     */
    BLOCK_INTERACT_NO_SWING("BlockInteractNoSwing", "Block Interact NoSwing", "NoSwing", CheckCategory.BLOCKINTERACT),

    /**
     * Nuker
     */
    NUKER("Nuker", CheckCategory.BLOCK);

    /**
     * The name
     * Pretty name
     * Permission name
     */
    private final String name, prettyName, permissionName;

    /**
     * The category
     */
    private final CheckCategory category;

    CheckType(String name, String prettyName, CheckCategory category) {
        this.name = name;
        this.prettyName = prettyName;
        this.permissionName = name;
        this.category = category;
    }

    CheckType(String name, String prettyName, String permissionName, CheckCategory category) {
        this.name = name;
        this.prettyName = prettyName;
        this.permissionName = permissionName;
        this.category = category;
    }

    CheckType(String name, CheckCategory category) {
        this.name = name;
        this.prettyName = name;
        this.permissionName = name;
        this.category = category;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the pretty name
     */
    public String getPrettyName() {
        return prettyName;
    }

    /**
     * @return the permission name
     */
    public String getPermissionName() {
        return permissionName;
    }

    /**
     * @return the category
     */
    public CheckCategory category() {
        return category;
    }

    /**
     * Get a check by name
     *
     * @param name the name
     * @return the {@link CheckType} or {@code null} if not found
     */
    public static CheckType getCheckTypeByName(String name) {
        for (CheckType value : values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

}