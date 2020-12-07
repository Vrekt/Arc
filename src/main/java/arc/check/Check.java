package arc.check;

import arc.Arc;
import arc.check.result.CheckResult;
import arc.check.timings.CheckTiming;
import arc.configuration.ArcConfiguration;
import arc.configuration.Reloadable;
import arc.configuration.check.CheckConfiguration;
import arc.configuration.check.CheckConfigurationBuilder;
import arc.permissions.Permissions;
import arc.violation.result.ViolationResult;
import bridge.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Represents a check.
 */
public abstract class Check implements Reloadable {

    /**
     * The check type
     */
    private final CheckType checkType;

    /**
     * The check timings
     */
    private CheckTiming timing;

    /**
     * The configuration builder;
     */
    protected final CheckConfigurationBuilder builder;

    /**
     * The check configuration
     */
    protected CheckConfiguration configuration;

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
        this.builder = new CheckConfigurationBuilder(checkType);
    }

    /**
     * If timings should be used.
     */
    protected void useTimings() {
        if (timing == null) {
            this.timing = new CheckTiming(checkType);
        }
    }

    /**
     * Set if this check is enabled.
     *
     * @param enabled enabled
     * @return this
     */
    public Check enabled(boolean enabled) {
        builder.enabled(enabled);
        return this;
    }

    /**
     * Set if this check should cancel
     *
     * @param cancel cancel
     * @return this
     */
    public Check cancel(boolean cancel) {
        builder.cancel(cancel);
        return this;
    }

    /**
     * Set the cancel level
     *
     * @param level level
     * @return this
     */
    public Check cancelLevel(int level) {
        builder.cancelLevel(level);
        return this;
    }

    /**
     * Set if this check should notify
     *
     * @param notify notify
     * @return this
     */
    public Check notify(boolean notify) {
        builder.notify(notify);
        return this;
    }

    /**
     * Set the notify level
     *
     * @param level level
     * @return this
     */
    public Check notifyEvery(int level) {
        builder.notifyEvery(level);
        return this;
    }

    /**
     * Set if this check should ban
     *
     * @param ban ban
     * @return this
     */
    public Check ban(boolean ban) {
        builder.ban(ban);
        if (!ban) builder.banLevel(0);
        return this;
    }

    /**
     * Set the ban level
     *
     * @param level level
     * @return this
     */
    public Check banLevel(int level) {
        builder.banLevel(level);
        return this;
    }

    /**
     * Set if this check should kick
     *
     * @param kick kick
     * @return this
     */
    public Check kick(boolean kick) {
        builder.kick(kick);
        if (!kick) builder.kickLevel(0);
        return this;
    }

    /**
     * Set the kick level
     *
     * @param level level
     * @return this
     */
    public Check kickLevel(int level) {
        builder.kickLevel(level);
        return this;
    }

    /**
     * Build the configuration
     */
    public void build() {
        configuration = builder.build();
    }

    /**
     * Add a sub-type section.
     *
     * @param types the types to create
     */
    protected void createSubTypeSections(CheckSubType... types) {
        for (CheckSubType type : types) configuration.createSubTypeSection(type);
    }

    /**
     * Add a configuration value
     *
     * @param valueName the value name
     * @param value     the value
     */
    protected void addConfigurationValue(String valueName, Object value) {
        configuration.addConfigurationValue(valueName, value);
    }

    /**
     * Add a configuration value to a sub-type
     *
     * @param type      the type
     * @param valueName the value name
     * @param value     the value
     */
    protected void addConfigurationValue(CheckSubType type, String valueName, Object value) {
        configuration.addConfigurationValue(type, valueName, value);
    }

    /**
     * Process the check result.
     *
     * @param result the result
     */
    protected ViolationResult checkViolation(Player player, CheckResult result) {
        if (result.failed()) return Arc.arc().violations().violation(player, this, result);
        return ViolationResult.EMPTY;
    }

    /**
     * Start timing
     */
    protected void start(Player player) {
        timing.start(player);
    }

    /**
     * Stop timing
     */
    protected void stop(Player player) {
        timing.stop(player);
    }

    /**
     * @return the timing
     */
    public CheckTiming timing() {
        return timing;
    }

    /**
     * @return {@code true} if there are timings
     */
    public boolean hasAnyTimings() {
        return timing != null && timing.hasAny();
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
     * Schedule
     *
     * @param runnable the runnable
     * @param every    timer
     * @param delay    initial delay
     */
    protected void schedule(Runnable runnable, long delay, long every) {
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
     * Check if the player is exempt
     *
     * @param player  the player
     * @param subType the check sub-type
     * @return {@code true} if so
     */
    protected boolean exempt(Player player, CheckSubType subType) {
        return Arc.arc().exemptions().isPlayerExempt(player, subType);
    }

    /**
     * Disables this check if the {@link Version} is newer than {@code Version.VERSION_1_8}
     *
     * @return {@code true} if the check is disabled
     */
    protected boolean disableIfNewerThan18() {
        if (Arc.version().isNewerThan(Version.VERSION_1_8)) {
            permanentlyDisabled = true;
            return true;
        }
        return false;
    }

    @Override
    public void reloadConfiguration(ArcConfiguration configuration) {
        if (permanentlyDisabled) return;
        this.configuration.reloadConfiguration(configuration);
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
