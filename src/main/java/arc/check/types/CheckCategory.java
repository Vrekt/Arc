package arc.check.types;

/**
 * Represents a check category.
 */
public enum CheckCategory {

    /**
     * Checks movement
     */
    MOVING,
    /**
     * Checks other player related things
     */
    PLAYER,
    /**
     * Checks packets
     */
    NETWORK,
    /**
     * Checks combat
     */
    COMBAT,

    /**
     * Checks blocks
     */
    BLOCK,

    /**
     * Checks block breaking
     */
    BLOCKBREAK,

    /**
     * Checks block placing
     */
    BLOCKPLACE,

    /**
     * Checks block interactions
     */
    BLOCKINTERACT,

}
