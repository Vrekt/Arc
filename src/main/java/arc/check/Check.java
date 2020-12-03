package arc.check;

import arc.Arc;
import bridge.Version;
import arc.check.result.CheckResult;
import arc.configuration.ArcConfiguration;
import arc.configuration.check.CheckConfiguration;
import arc.configuration.check.CheckConfigurationWriter;
import arc.permissions.Permissions;
import arc.violation.result.ViolationResult;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * Represents a check.
 */
public abstract class Check {

    /**
     * The check type
     */
    private final CheckType checkType;

    /**
     * The check configuration
     */
    protected CheckConfiguration configuration;

    /**
     * The configuration writer.
     */
    protected final CheckConfigurationWriter writer = new CheckConfigurationWriter();

    /**
     * The scheduled task.
     */
    protected BukkitTask scheduled;

    /**
     * If this check is permanently disabled.
     */
    protected boolean permanentlyDisabled;

    /**
     * Initialize the check
     *
     * @param checkType the type
     */
    protected Check(CheckType checkType) {
        this.checkType = checkType;
        writer.name(checkType.getName().toLowerCase());
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
     * Get a value
     *
     * @param valueName the value name
     * @return the value
     */
    protected long getValueLong(String valueName) {
        return configuration.section().getLong(valueName);
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
     *
     * @param player  the player
     * @param message the message to broadcast
     */
    protected void kick(Player player, String message) {
        final ArcConfiguration config = Arc.arc().configuration();
        Bukkit.getScheduler().runTaskLater(Arc.plugin(), ()
                -> {
            player.kickPlayer(config.kickConfiguration().kickMessage().replace("%check%", getName()));
            Bukkit.broadcast(message, Permissions.ARC_VIOLATIONS);
        }, config.kickConfiguration().kickDelay());
    }

    /**
     * Schedule this check
     *
     * @param runnable the runnable
     * @param every    timer
     * @param delay    initial delay
     */
    protected void scheduledCheck(Runnable runnable, long delay, long every) {
        scheduled = Bukkit.getScheduler().runTaskTimer(Arc.plugin(), runnable, delay, every);
    }

    /**
     * Check if the player is exempt
     *
     * @param player the player
     * @return {@code true} if so
     */
    protected boolean exempt(Player player) {
        return Arc.arc().exemptions().isPlayerExempt(player, checkType);
    }

    /**
     * Check if the player is exempt from a sub-type
     *
     * @param player  player
     * @param subtype the type
     * @return {@code true} if so
     */
    protected boolean exemptSubType(Player player, String subtype) {
        return player.hasPermission(Permissions.ARC_BYPASS + "." + checkType.category().name().toLowerCase() + "." + getName() + "." + subtype);
    }

    /**
     * Disable this check if the current version is newer than {@code version}
     *
     * @param version the other version
     * @return {@code true} if the check is disabled.
     */
    protected boolean disableIfNewerThan(Version version) {
        if (Arc.version().isNewerThan(version)) {
            permanentlyDisabled = true;
            return true;
        }
        return false;
    }

    /**
     * Disable this check if the current version is older than {@code version}
     *
     * @param version the version
     * @return {@code true} if the check is disabled.
     */
    protected boolean disableIfOlderThan(Version version) {
        if (Arc.version().isOlderThan(version)) {
            permanentlyDisabled = true;
            return true;
        }
        return false;
    }

    /**
     * Reload the configuration.
     */
    public void reloadConfigInternal(FileConfiguration configuration) {
        if (permanentlyDisabled) return;

        this.configuration.reload(configuration.getConfigurationSection(getName().toLowerCase()));
        reloadConfig();
    }

    /**
     * Reload the check implementation config.
     */
    public abstract void reloadConfig();

    /**
     * Load the check
     */
    public abstract void load();

    /**
     * Unload the check if needed.
     */
    public void unload() {
    }

    /**
     * @return {@code true} if the check is enabled.
     */
    public boolean enabled() {
        return !permanentlyDisabled && configuration.enabled();
    }

    /**
     * @return the check name.
     */
    public String getName() {
        return checkType.getName();
    }

    /**
     * @return the check type
     */
    public CheckType type() {
        return checkType;
    }

    /**
     * @return the check configuration
     */
    public CheckConfiguration configuration() {
        return configuration;
    }

    /**
     * Cancel the scheduled task.
     */
    protected void cancelScheduled() {
        if (scheduled != null
                && Bukkit.getScheduler().isCurrentlyRunning(scheduled.getTaskId())) {
            scheduled.cancel();
            scheduled = null;
        }
    }

}
