package arc.utility.chat;

/**
 * Placeholders
 */
public enum Placeholders {

    /**
     * Player
     */
    PLAYER("%player%"),
    /**
     * Check
     */
    CHECK("%check%"),
    /**
     * Level
     */
    LEVEL("%level%"),

    /**
     * Prefix
     */
    PREFIX("%prefix%");

    /**
     * The placeholder
     */
    private final String placeholder;

    Placeholders(String placeholder) {
        this.placeholder = placeholder;
    }

    /**
     * @return the placeholder
     */
    public String placeholder() {
        return placeholder;
    }
}
