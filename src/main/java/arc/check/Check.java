package arc.check;

import arc.Arc;
import arc.check.result.CancelType;
import arc.check.result.CheckResult;
import arc.check.types.CheckSubType;
import arc.check.types.CheckType;
import arc.configuration.ArcConfiguration;
import arc.configuration.Configurable;
import arc.configuration.check.CheckConfiguration;
import arc.configuration.check.CheckConfigurationBuilder;
import arc.exemption.ExemptionManager;
import arc.exemption.type.ExemptionType;
import arc.utility.api.BukkitAccess;
import arc.violation.ViolationManager;
import arc.violation.result.ViolationResult;
import bridge.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitTask;

/**
 * Represents a check.
 */
public abstract class Check extends Configurable {

    /**
     * Exemptions
     */
    private static final ExemptionManager EXEMPTION_MANAGER = Arc.getInstance().getExemptionManager();

    /**
     * Violations
     */
    private static final ViolationManager VIOLATION_MANAGER = Arc.getInstance().getViolationManager();

    /**
     * Configuration
     */
    private static final ArcConfiguration CONFIGURATION = Arc.getInstance().getArcConfiguration();

    /**
     * The check type
     */
    private final CheckType checkType;

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
     * Send a debug message
     *
     * @param player  the player
     * @param message the message
     */
    protected void debug(Player player, String message) {
        if (CONFIGURATION.enableDebugMessages()) BukkitAccess.sendMessage(player, message);
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
        configuration.addConfigurationValueTo(type, valueName, value);
    }

    /**
     * Process the check result.
     *
     * @param result the result
     */
    protected ViolationResult checkViolationWithResult(Player player, CheckResult result) {
        if (result.failed()) return VIOLATION_MANAGER.violation(player, this, result);
        return ViolationResult.EMPTY;
    }

    /**
     * Process the provided {@code result}
     *
     * @param player the player
     * @param result the result
     * @return {@code true} if the check should cancel.
     */
    protected boolean checkViolation(Player player, CheckResult result) {
        return checkViolationWithResult(player, result).cancel();
    }

    /**
     * Process the check result.
     *
     * @param player the player
     * @param result the result
     * @param cancel the cancel location
     * @param type   the type of cancel
     */
    protected ViolationResult checkViolation(Player player, CheckResult result, Location cancel, CancelType type) {
        if (result.failed()) {
            final ViolationResult vr = VIOLATION_MANAGER.violation(player, this, result);
            if (vr.cancel()) result.cancelTo(cancel, type);
            return vr;
        }
        return ViolationResult.EMPTY;
    }

    /**
     * Set back the player
     *
     * @param player the player
     * @param where  where to
     */
    protected void setbackPlayer(Player player, Location where) {
        player.teleport(where, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    /**
     * Schedule
     *
     * @param runnable the runnable
     * @param every    timer
     * @param delay    initial delay
     */
    protected void schedule(Runnable runnable, long delay, long every) {
        if (every == 0) throw new UnsupportedOperationException("Scheduled delay cannot be every tick.");
        scheduled = Bukkit.getScheduler().runTaskTimer(Arc.getPlugin(), runnable, delay, every);
    }

    /**
     * Check if the player is exempt
     *
     * @param player the player
     * @return {@code true} if so
     */
    protected boolean exempt(Player player) {
        if (player == null || !player.isOnline()) return true;
        return EXEMPTION_MANAGER.isPlayerExempt(player, checkType);
    }

    /**
     * Check if the player is exempt
     *
     * @param player  the player
     * @param subType the check sub-type
     * @return {@code true} if so
     */
    protected boolean exempt(Player player, CheckSubType subType) {
        if (player == null || !player.isOnline()) return true;
        return EXEMPTION_MANAGER.isPlayerExempt(player, subType);
    }

    /**
     * Check if a player is exempt
     *
     * @param player the player
     * @param type   the type
     * @return {@code true} if so
     */
    protected boolean exempt(Player player, ExemptionType type) {
        if (player == null || !player.isOnline()) return true;
        return EXEMPTION_MANAGER.isPlayerExempt(player, type);
    }

    /**
     * Disables this check if thr provided {@code version} is newer than current
     *
     * @param version the version
     * @return {@code true} if the check is disabled
     */
    protected boolean disableIfNewerThan(Version version) {
        if (Arc.getMCVersion().isNewerThan(version)) {
            permanentlyDisabled = true;
            return true;
        }
        return false;
    }

    @Override
    public void reload(ArcConfiguration configuration) {
        if (permanentlyDisabled) return;
        unload();

        this.configuration.reload(configuration);
        if (this.configuration.enabled()) {
            reloadConfig();
        }
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
     * @return the pretty name.
     */
    public String getPrettyName() {
        return checkType.getPrettyName();
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
