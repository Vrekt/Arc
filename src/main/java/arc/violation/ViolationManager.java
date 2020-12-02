package arc.violation;

import arc.Arc;
import arc.api.events.PlayerViolationEvent;
import arc.api.events.PostPlayerViolationEvent;
import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CheckResult;
import arc.configuration.ArcConfiguration;
import arc.configuration.Reloadable;
import arc.configuration.punishment.ban.BanConfiguration;
import arc.configuration.punishment.kick.KickConfiguration;
import arc.permissions.Permissions;
import arc.utility.Punishment;
import arc.violation.result.ViolationResult;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.io.Closeable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Manages violations
 */
public final class ViolationManager implements Closeable, Reloadable {

    /**
     * Violation history
     */
    private final Map<UUID, Violations> history = new ConcurrentHashMap<>();

    /**
     * A list of players who can view violations/debug information
     */
    private final Map<Player, Boolean> violationViewers = new ConcurrentHashMap<>();

    /**
     * Keeps track of when to expire history
     */
    private Cache<UUID, Violations> historyCache;

    /**
     * The arc configuration.
     */
    private ArcConfiguration configuration;

    /**
     * The ban configuration.
     */
    private BanConfiguration banConfiguration;

    /**
     * The kick configuration.
     */
    private KickConfiguration kickConfiguration;


    /**
     * Initialize
     *
     * @param configuration the configuration
     */
    public void initialize(ArcConfiguration configuration) {
        this.configuration = configuration;
        this.banConfiguration = configuration.banConfiguration();
        this.kickConfiguration = configuration.kickConfiguration();

        historyCache = CacheBuilder.newBuilder()
                .expireAfterWrite(configuration.violationDataTimeout(), TimeUnit.MINUTES)
                .build();
    }

    /**
     * Invoked when a player joins
     *
     * @param player the player
     */
    public void onPlayerJoin(Player player) {
        final Violations cached = historyCache.getIfPresent(player.getUniqueId());
        if (cached != null) {
            historyCache.invalidate(player.getUniqueId());
            history.put(player.getUniqueId(), cached);
        } else {
            history.put(player.getUniqueId(), new Violations());
        }
        if (Permissions.canViewViolations(player)) violationViewers.put(player, Boolean.FALSE);
    }

    /**
     * Invoked when a player leaves
     *
     * @param player the player
     */
    public void onPlayerLeave(Player player) {
        historyCache.put(player.getUniqueId(), history.get(player.getUniqueId()));
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
        final Violations violations = history.get(player.getUniqueId());
        final int level = violations.incrementViolationLevel(check.getName());

        // call our violation event.
        final PlayerViolationEvent event = new PlayerViolationEvent(player, check.type(), level, result.information());
        triggerSync(event);
        // if the event is cancelled, remove the violation level and return no result.
        if (event.isCancelled()) {
            // reverse.
            violations.decreaseViolationLevel(check.getName());
            return ViolationResult.EMPTY;
        }

        // create a new result if we haven't cancelled.
        final ViolationResult violationResult = new ViolationResult();

        // handle violation
        if (check.configuration().notifyViolation()
                && check.configuration().shouldNotify(level)) {
            violationResult.addResult(ViolationResult.Result.NOTIFY);
            // grab our violation message and then replace placeholders.
            final String rawMessage = configuration.violationMessage();
            final boolean hasAppend = result.appendName() != null;
            // the violation message,
            final String messageNoInfo = replaceConfigurableMessage(rawMessage,
                    ImmutableMap.of("%player%", player.getName(), "%check%", (
                            hasAppend ? check.getName() + ChatColor.GRAY + " " + result.appendName() + " "
                                    : check.getName()), "%level%", level + "", "%prefix%", configuration.prefix()));

            // notify, check debug status too.
            violationViewers.forEach((viewer, debug) -> {
                final String messageInfo = messageNoInfo.replace("%information%", debug ? result.information() : "");
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
        final PostPlayerViolationEvent postEvent = new PostPlayerViolationEvent(player, violationResult, check.type(), level, result.information());
        triggerSync(postEvent);

        return violationResult;
    }

    /**
     * Trigger a bukkit event sync.
     * TODO: Could cause a problem with a-lot of violations?
     * TODO: Maybe move it into a queue with a thread constantly processing them.
     *
     * @param event the event
     */
    private void triggerSync(Event event) {
        Bukkit.getScheduler().runTask(Arc.arc(), () -> Bukkit.getServer().getPluginManager().callEvent(event));
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
     * Get the violation level
     *
     * @param player the player
     * @param check  the check
     * @return the level
     */
    public int getViolationLevel(Player player, CheckType check) {
        return history.get(player.getUniqueId()).getViolationLevel(check.getName());
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

    @Override
    public void reloadConfiguration(ArcConfiguration configuration) {
        this.configuration = configuration;
        this.kickConfiguration = configuration.kickConfiguration();
        this.banConfiguration = configuration.banConfiguration();

        historyCache.invalidateAll();
        historyCache = CacheBuilder.newBuilder()
                .expireAfterWrite(configuration.violationDataTimeout(), TimeUnit.MINUTES)
                .build();
    }

    @Override
    public void close() {
        history.clear();
        violationViewers.clear();
        historyCache.invalidateAll();
    }

}
