package arc.violation;

import arc.api.events.PlayerViolationEvent;
import arc.api.events.PostPlayerViolationEvent;
import arc.check.Check;
import arc.check.result.CheckResult;
import arc.configuration.ArcConfiguration;
import arc.configuration.punishment.ban.BanConfiguration;
import arc.configuration.punishment.kick.KickConfiguration;
import arc.permissions.Permissions;
import arc.utility.Punishment;
import arc.violation.result.ViolationResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages violations
 */
public final class ViolationManager {

    /**
     * Violation history
     */
    private final Map<UUID, Violations> history = new ConcurrentHashMap<>();

    /**
     * A list of players who can view violations/debug information
     */
    private final Map<Player, Boolean> violationViewers = new ConcurrentHashMap<>();

    /**
     * The arc configuration.
     */
    private final ArcConfiguration configuration;

    /**
     * The ban configuration.
     */
    private final BanConfiguration banConfiguration;

    /**
     * The kick configuration.
     */
    private final KickConfiguration kickConfiguration;

    /**
     * Initialize
     *
     * @param configuration the configuration.
     */
    public ViolationManager(ArcConfiguration configuration) {
        this.configuration = configuration;
        this.banConfiguration = configuration.banConfiguration();
        this.kickConfiguration = configuration.kickConfiguration();
    }

    /**
     * Invoked when a player joins
     *
     * @param player the player
     */
    public void onPlayerJoin(Player player) {
        history.put(player.getUniqueId(), new Violations());
        if (Permissions.canViewViolations(player)) violationViewers.put(player, Boolean.FALSE);
    }

    /**
     * Invoked when a player leaves
     *
     * @param player the player
     */
    public void onPlayerLeave(Player player) {
        history.get(player.getUniqueId()).dispose();
        history.remove(player.getUniqueId());
        violationViewers.remove(player);
    }

    /**
     * Process a violation
     *
     * @param player the player
     * @param result the result
     * @return the result
     */
    public ViolationResult violation(Player player, Check check, CheckResult result) {
        final var violations = history.get(player.getUniqueId());
        final var level = violations.incrementViolationLevel(check.getName());

        // call our violation event.
        final var event = new PlayerViolationEvent(player, check.type(), level, result.information());
        Bukkit.getPluginManager().callEvent(event);
        // if the event is cancelled, remove the violation level and return no result.
        if (event.isCancelled()) {
            // reverse.
            violations.decreaseViolationLevel(check.getName());
            return ViolationResult.EMPTY;
        }

        // create a new result if we haven't cancelled.
        final var violationResult = new ViolationResult();

        // handle violation
        if (check.configuration().notifyViolation()
                && check.configuration().shouldNotify(level)) {
            violationResult.addResult(ViolationResult.Result.NOTIFY);
            // grab our violation message and then replace placeholders.
            final var rawMessage = configuration.violationMessage();
            final var hasAppend = result.appendName() != null;
            // the violation message,
            final var messageNoInfo = replaceConfigurableMessage(rawMessage,
                    Map.of("%player%", player.getName(), "%check%", (
                            hasAppend ? check.getName() + ChatColor.GRAY + " " + result.appendName() + " "
                                    : check.getName()), "%level%", level + "", "%prefix%", configuration.prefix()));

            // notify, check debug status too.
            violationViewers.forEach((viewer, debug) -> {
                final var messageInfo = messageNoInfo.replace("%information%", debug ? result.information() : "");
                viewer.sendMessage(messageInfo);
            });
        }

        // add a cancel result if this check should cancel.
        if (check.configuration().shouldCancel(level)) violationResult.addResult(ViolationResult.Result.CANCEL);

        // ban the player if this check should ban
        if (check.configuration().shouldBan(level)
                && !Punishment.hasPendingBan(player)) {
            violationResult.addResult(ViolationResult.Result.BAN);
            Punishment.banPlayer(player, check, banConfiguration);
        }

        // kick the player if this check should kick.
        if (check.configuration().shouldKick(level)
                && !Punishment.hasPendingKick(player)) {
            violationResult.addResult(ViolationResult.Result.KICK);
            Punishment.kickPlayer(player, check, kickConfiguration);
        }

        // fire the post event
        final var postEvent = new PostPlayerViolationEvent(player, violationResult, check.type(), level, result.information());
        Bukkit.getPluginManager().callEvent(postEvent);

        return violationResult;
    }

    /**
     * Check if the player can view violations
     *
     * @param player the player
     * @return {@code true} if so
     */
    public boolean isViolationViewer(Player player) {
        return violationViewers.containsKey(player);
    }

    /**
     * Add a violation viewer
     *
     * @param player the player
     */
    public void addViolationViewer(Player player) {
        violationViewers.put(player, false);
    }

    /**
     * Remove a violation viewer
     *
     * @param player the player
     */
    public void removeViolationViewer(Player player) {
        violationViewers.remove(player);
    }

    /**
     * Check if the player can view debug information
     *
     * @param player the player
     * @return {@code true} if so
     */
    public boolean isDebugViewer(Player player) {
        return violationViewers.containsKey(player) && violationViewers.get(player);
    }

    /**
     * Toggle debug viewer
     *
     * @param player the player
     * @param state  the state
     */
    public void toggleDebugViewer(Player player, boolean state) {
        violationViewers.put(player, state);
    }

    /**
     * Replace placeholders within a configurable message.
     *
     * @param message the message
     * @param entries the entries
     * @return the string
     */
    private String replaceConfigurableMessage(String message, Map<String, String> entries) {
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return message;
    }

}
