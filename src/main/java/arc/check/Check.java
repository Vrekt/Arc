package arc.check;

import arc.Arc;
import arc.check.result.CheckResult;
import arc.configuration.check.CheckConfiguration;
import arc.configuration.check.CheckConfigurationWriter;
import arc.permissions.Permissions;
import arc.violation.result.ViolationResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
     * The check configuration
     */
    protected CheckConfiguration configuration;

    /**
     * Initialize the check
     *
     * @param name the name
     */
    protected Check(String name, CheckType checkType) {
        this.name = name;
        this.checkType = checkType;
    }

    /**
     * Write the default check configuration for this check
     *
     * @param cancel      {@code true} if this check should cancel
     * @param cancelLevel the level at which to cancel at
     * @param notify      {@code true} if this check should notify violations
     * @param notifyLevel the level at which to notify
     * @param ban         {@code true} if this check should ban
     * @param banLevel    the level at which to ban
     * @param kick        {@code true} if this check should kick
     * @param kickLevel   the level at which to kick
     */
    protected void writeConfiguration(boolean cancel, int cancelLevel, boolean notify, int notifyLevel, boolean ban, int banLevel, boolean kick, int kickLevel) {
        configuration = new CheckConfigurationWriter()
                .name(getName())
                .cancel(cancel)
                .cancelLevel(cancelLevel)
                .notify(notify)
                .notifyEvery(notifyLevel)
                .ban(ban)
                .banLevel(banLevel)
                .kick(kick)
                .kickLevel(kickLevel)
                .finish();
    }

    /**
     * Convenience method for checks that don't kick or ban.
     *
     * @param cancel      {@code true} if this check should cancel
     * @param cancelLevel the level at which to cancel at
     * @param notify      {@code true} if this check should notify violations
     * @param notifyLevel the level at which to notify
     */
    protected void writeConfiguration(boolean cancel, int cancelLevel, boolean notify, int notifyLevel) {
        writeConfiguration(cancel, cancelLevel, notify, notifyLevel, false, 0, false, 0);
    }

    /**
     * Add a configuration value
     *
     * @param valueName the value name
     * @param value     the value
     */
    protected void addConfigurationValue(String valueName, Object value) {
        if (containsValue(valueName)) return;
        Arc.plugin().getConfig().set(name + "." + valueName, value);
    }

    /**
     * Check if a value exists.
     *
     * @param valueName the value name
     * @return {@code true} if so.
     */
    protected boolean containsValue(String valueName) {
        return Arc.plugin().getConfig().contains(name + "." + valueName);
    }

    /**
     * Get a value
     *
     * @param valueName the value name
     * @return the value
     */
    protected int getValueInt(String valueName) {
        return Arc.plugin().getConfig().getInt(name + "." + valueName);
    }

    /**
     * Get a value
     *
     * @param valueName the value name
     * @return the value
     */
    protected double getValueDouble(String valueName) {
        return Arc.plugin().getConfig().getDouble(name + "." + valueName);
    }

    /**
     * Get a value
     *
     * @param valueName the value name
     * @return the value
     */
    protected boolean getValueBoolean(String valueName) {
        return Arc.plugin().getConfig().getBoolean(name + "." + valueName);
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
     * @param player the player
     */
    protected void kick(Player player) {
        final var config = Arc.arc().configuration();
        Bukkit.getScheduler().runTaskLater(Arc.plugin(), ()
                -> {
            player.kickPlayer(config.kickConfiguration().kickMessage());
            broadcast(player);
        }, config.kickConfiguration().kickDelay());
    }

    /**
     * Broadcast to players who have the permission ARC_VIOLATIONS
     *
     * @param player the player
     */
    protected void broadcast(Player player) {
        Bukkit.broadcast(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Arc" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE
                + player.getName() + ChatColor.WHITE + " was kicked for sending too many packets. ", Permissions.ARC_VIOLATIONS);
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
        return Arc.arc().exemptions().isPlayerExempt(player, type());
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
     * @return the check configuration
     */
    public CheckConfiguration configuration() {
        return configuration;
    }

}
