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
import arc.utility.chat.PlaceholderStringReplacer;
import arc.violation.result.ViolationResult;
import bridge.Version;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.io.Closeable;
import java.util.Map;
import java.util.Set;
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
    private final Set<Player> violationViewers = ConcurrentHashMap.newKeySet();

    /**
     * If sync events should be used.
     */
    private boolean useSyncEvents;

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

        useSyncEvents = Arc.version().isNewerThan(Version.VERSION_1_8);
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
        if (Permissions.canViewViolations(player)) violationViewers.add(player);
    }

    /**
     * Invoked when a player leaves
     *
     * @param player the player
     */
    public void onPlayerLeave(Player player) {
        historyCache.put(player.getUniqueId(), history.get(player.getUniqueId()));
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
        final Violations violations = history.get(player.getUniqueId());
        final int level = violations.incrementViolationLevel(check.getName());

        // call our violation event.
        if (configuration.enableEventApi()) {
            final PlayerViolationEvent event = new PlayerViolationEvent(player, check.type(), level, result);
            triggerSync(event);
            // if the event is cancelled, remove the violation level and return no result.
            if (event.isCancelled()) {
                // reverse.
                violations.decreaseViolationLevel(check.getName());
                return ViolationResult.EMPTY;
            }
        }

        // create a new result if we haven't cancelled.
        final ViolationResult violationResult = new ViolationResult();

        // handle violation
        if (check.configuration().notifyViolation()
                && check.configuration().shouldNotify(level)) {
            // add that we are going to notify.
            violationResult.addResult(ViolationResult.Result.NOTIFY);
            // replace the place holders within the message
            final String violationMessage = new PlaceholderStringReplacer(configuration.violationMessage())
                    .replacePlayer(player)
                    .replaceCheck(check, result.hasSubType() ? "(" + result.subType().fancyName() + ")" : null)
                    .replaceLevel(level)
                    .replacePrefix(configuration.prefix())
                    .build();

            // build the text component and then send to all viewers.
            final TextComponent component = new TextComponent(violationMessage);
            Arc.bridge().chat().addHoverEvent(component, result.information());
            violationViewers.forEach(viewer -> viewer.spigot().sendMessage(component));
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
        if (configuration.enableEventApi()) {
            final PostPlayerViolationEvent postEvent = new PostPlayerViolationEvent(player, violationResult, check.type(), level, result.information());
            triggerSync(postEvent);
        }

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
        if (useSyncEvents) {
            Bukkit.getScheduler().runTask(Arc.arc(), () -> Bukkit.getServer().getPluginManager().callEvent(event));
        } else {
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
    }

    /**
     * Check if the player can view violations
     *
     * @param player the player
     * @return {@code true} if so
     */
    public boolean isViolationViewer(Player player) {
        return violationViewers.contains(player);
    }

    /**
     * Toggle violations viewer
     *
     * @param player the player
     * @return {@code true} if the player is now a viewer.
     */
    public boolean toggleViolationsViewer(Player player) {
        if (isViolationViewer(player)) {
            violationViewers.remove(player);
            return false;
        } else {
            violationViewers.add(player);
            return true;
        }
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
