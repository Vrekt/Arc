package arc.configuration.check;

import org.bukkit.configuration.ConfigurationSection;

/**
 * A check configuration format.
 */
public final class CheckConfiguration {

    /**
     * The section for this check
     */
    private final ConfigurationSection section;

    /**
     * Boolean check values
     */
    private final boolean enabled, cancel, notify, ban, kick;

    /**
     * Violation levels
     */
    private final int cancelLevel, notifyLevel, banLevel, kickLevel;

    /**
     * Initialize this check configuration
     *
     * @param configuration the configuration
     */
    public CheckConfiguration(ConfigurationSection configuration) {
        this.section = configuration;

        enabled = configuration.getBoolean("enabled");
        cancel = configuration.getBoolean("cancel");
        notify = configuration.getBoolean("notify");
        ban = configuration.getBoolean("ban");
        kick = configuration.getBoolean("kick");

        cancelLevel = configuration.getInt("cancel-level");
        notifyLevel = configuration.getInt("notify-every");
        banLevel = configuration.getInt("ban-level");
        kickLevel = configuration.getInt("kick-level");
    }

    /**
     * @return the section
     */
    public ConfigurationSection section() {
        return section;
    }

    /**
     * @return if the check is enabled
     */
    public boolean enabled() {
        return enabled;
    }

    /**
     * @return if the check should cancel
     */
    public boolean cancel() {
        return cancel;
    }

    /**
     * @return if the check should notify
     */
    public boolean notifyViolation() {
        return notify;
    }

    /**
     * @return if the check should ban
     */
    public boolean ban() {
        return ban;
    }

    /**
     * @return if the check should kick.
     */
    public boolean kick() {
        return kick;
    }

    /**
     * If the check should be cancelled.
     *
     * @param violationLevel the players violation level
     * @return {@code true} if so
     */
    public boolean shouldCancel(int violationLevel) {
        return cancel() && violationLevel >= cancelLevel;
    }

    /**
     * Check if this check should notify right now
     *
     * @param violationLevel the players violation level
     * @return {@code true} if so
     */
    public boolean shouldNotify(int violationLevel) {
        return notifyLevel == 1 || violationLevel % notifyLevel == 0;
    }

    /**
     * If the player should be banned.
     *
     * @param violationLevel the violation level
     * @return {@code true} if so
     */
    public boolean shouldBan(int violationLevel) {
        return ban() && violationLevel >= banLevel;
    }

    /**
     * If the player should be kicked
     *
     * @param violationLevel the violation level
     * @return {@code true} if so
     */
    public boolean shouldKick(int violationLevel) {
        return kick() && violationLevel >= kickLevel;
    }

}
