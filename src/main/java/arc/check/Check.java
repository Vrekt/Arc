package arc.check;

import arc.Arc;
import arc.check.result.CheckResult;
import arc.configuration.check.CheckConfiguration;
import arc.configuration.check.CheckConfigurationWriter;
import arc.permissions.Permissions;
import arc.violation.result.ViolationResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Represents a check.
 */
public abstract class Check {

    /**
     * The check name
     */
    private final String name;

    /**
     * The check type
     */
    private final CheckType checkType;

    /**
     * The category
     */
    private final CheckCategory category;

    /**
     * The check configuration
     */
    protected CheckConfiguration configuration;

    /**
     * The configuration writer.
     */
    protected final CheckConfigurationWriter writer = new CheckConfigurationWriter();

    /**
     * Initialize the check
     *
     * @param name      the name
     * @param checkType the type
     * @param category  the category
     */
    protected Check(String name, CheckType checkType, CheckCategory category) {
        this.name = name;
        this.checkType = checkType;
        this.category = category;

        writer.name(name);
    }

    /**
     * Set if this check is enabled.
     *
     * @param enabled enabled
     * @return this
     */
    public Check enabled(boolean enabled) {
        writer.enabled(enabled);
        return this;
    }

    /**
     * Set if this check should cancel
     *
     * @param cancel cancel
     * @return this
     */
    public Check cancel(boolean cancel) {
        writer.cancel(cancel);
        return this;
    }

    /**
     * Set the cancel level
     *
     * @param level level
     * @return this
     */
    public Check cancelLevel(int level) {
        writer.cancelLevel(level);
        return this;
    }

    /**
     * Set if this check should notify
     *
     * @param notify notify
     * @return this
     */
    public Check notify(boolean notify) {
        writer.notify(notify);
        return this;
    }

    /**
     * Set the notify level
     *
     * @param level level
     * @return this
     */
    public Check notifyEvery(int level) {
        writer.notifyEvery(level);
        return this;
    }

    /**
     * Set if this check should ban
     *
     * @param ban ban
     * @return this
     */
    public Check ban(boolean ban) {
        writer.ban(ban);
        if (!ban) writer.banLevel(0);
        return this;
    }

    /**
     * Set the ban level
     *
     * @param level level
     * @return this
     */
    public Check banLevel(int level) {
        writer.banLevel(level);
        return this;
    }

    /**
     * Set if this check should kick
     *
     * @param kick kick
     * @return this
     */
    public Check kick(boolean kick) {
        writer.kick(kick);
        if (!kick) writer.kickLevel(0);
        return this;
    }

    /**
     * Set the kick level
     *
     * @param level level
     * @return this
     */
    public Check kickLevel(int level) {
        writer.kickLevel(level);
        return this;
    }

    /**
     * Write the config
     */
    public void write() {
        configuration = writer.finish();
    }

    /**
     * Add a configuration value
     *
     * @param valueName the value name
     * @param value     the value
     */
    protected void addConfigurationValue(String valueName, Object value) {
        if (containsValue(valueName)) return;
        configuration.section().set(valueName, value);
    }

    /**
     * Check if a value exists.
     *
     * @param valueName the value name
     * @return {@code true} if so.
     */
    protected boolean containsValue(String valueName) {
        return configuration.section().contains(valueName);
    }

    /**
     * Get a value
     *
     * @param valueName the value name
     * @return the value
     */
    protected int getValueInt(String valueName) {
        return configuration.section().getInt(valueName);
    }

    /**
     * Get a value
     *
     * @param valueName the value name
     * @return the value
     */
    protected double getValueDouble(String valueName) {
        return configuration.section().getDouble(valueName);
    }

    /**
     * Get a value
     *
     * @param valueName the value name
     * @return the value
     */
    protected boolean getValueBoolean(String valueName) {
        return configuration.section().getBoolean(valueName);
    }

    /**
     * Get a string list
     *
     * @param valueName the value name
     * @return the list
     */
    protected List<String> getList(String valueName) {
        return configuration.section().getStringList(valueName);
    }

    /**
     * Process the check result.
     *
     * @param result the result
     */
    protected ViolationResult result(Player player, CheckResult result) {
        if (result.failed()) return Arc.arc().violations().violation(player, this, result);
        return ViolationResult.EMPTY;
    }

    /**
     * Process this result but ignore the output
     *
     * @param player the player
     * @param result the result
     */
    protected void resultIgnore(Player player, CheckResult result) {
        if (result.failed()) Arc.arc().violations().violation(player, this, result);
    }

    /**
     * Kick the player
     * TODO: Translate kick message string.
     *
     * @param player  the player
     * @param message the message to broadcast
     */
    protected void kick(Player player, String message) {
        final var config = Arc.arc().configuration();
        Bukkit.getScheduler().runTaskLater(Arc.plugin(), ()
                -> {
            player.kickPlayer(config.kickConfiguration().kickMessage());
            broadcast(player, message);
        }, config.kickConfiguration().kickDelay());
    }

    /**
     * Broadcast to players who have the permission ARC_VIOLATIONS
     *
     * @param player    the player
     * @param broadcast the message
     */
    protected void broadcast(Player player, String broadcast) {
        Bukkit.broadcast(broadcast, Permissions.ARC_VIOLATIONS);
    }

    /**
     * Schedule this check
     *
     * @param runnable the runnable
     * @param every    timer
     * @param delay    initial delay
     */
    protected void scheduledCheck(Runnable runnable, long delay, long every) {
        Bukkit.getScheduler().runTaskTimer(Arc.plugin(), runnable, delay, every);
    }

    /**
     * Check if the player is exempt
     *
     * @param player the player
     * @return {@code true} if so
     */
    protected boolean exempt(Player player) {
        return Arc.arc().exemptions().isPlayerExempt(player, category(), type());
    }

    /**
     * @return {@code true} if the check is enabled.
     */
    public boolean enabled() {
        return configuration.enabled();
    }

    /**
     * @return the check name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the check type
     */
    public CheckType type() {
        return checkType;
    }

    /**
     * @return the category
     */
    public CheckCategory category() {
        return category;
    }

    /**
     * @return the check configuration
     */
    public CheckConfiguration configuration() {
        return configuration;
    }

}
