package arc.violation;

import arc.Arc;
import arc.api.events.PlayerViolationEvent;
import arc.api.events.PostPlayerViolationEvent;
import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CheckResult;
import arc.configuration.ArcConfiguration;
import arc.configuration.Configurable;
import arc.permissions.Permissions;
import arc.punishment.PunishmentManager;
import arc.violation.result.ViolationResult;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.io.Closeable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Manages violations
 */
public final class ViolationManager extends Configurable implements Closeable {

    /**
     * Violation history
     */
    private final Map<UUID, Violations> history = new ConcurrentHashMap<>();

    /**
     * A list of players who can view violations/debug information
     */
    private final Set<Player> violationViewers = ConcurrentHashMap.newKeySet();

    /**
     * Keeps track of when to expire history
     */
    private Cache<UUID, Violations> historyCache;

    /**
     * The arc configuration.
     */
    private ArcConfiguration configuration;

    /**
     * The punishment manager
     */
    private PunishmentManager punishmentManager;

    /**
     * Initialize
     *
     * @param configuration the configuration
     */
    public void initialize(ArcConfiguration configuration) {
        this.configuration = configuration;
        this.punishmentManager = Arc.arc().punishment();

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
        final int level = violations.incrementViolationLevel(check.type());

        // call our violation event.
        if (configuration.enableEventApi()) {

            final PlayerViolationEvent event = new PlayerViolationEvent(player, check.type(), level, result);
            Arc.triggerEvent(event);
            // if the event is cancelled, remove the violation level and return no result.
            if (event.isCancelled()) {
                // reverse.
                violations.decreaseViolationLevel(check.type());
                return ViolationResult.EMPTY;
            }
        }

        // create a new result if we haven't cancelled.
        final ViolationResult violationResult = new ViolationResult();

        // handle violation
        if (check.configuration().shouldNotify(level)) {
            // add that we are going to notify.
            violationResult.addResult(ViolationResult.Result.NOTIFY);
            // replace the place holders within the message

            final String violationMessage = configuration.violationNotifyMessage()
                    .player(player)
                    .check(check, result.hasSubType() ? "(" + result.subType().prettyName() + ")" : null)
                    .level(level)
                    .prefix()
                    .value();

            // build the text component and then send to all viewers.
            final TextComponent component = new TextComponent(violationMessage);
            Arc.bridge().api().addHoverEvent(component, result.information());

            violationViewers.forEach(viewer -> Arc.bridge().api().sendMessage(player, component));
        }

        // add a cancel result if this check should cancel.
        if (check.configuration().shouldCancel(level)) violationResult.addResult(ViolationResult.Result.CANCEL);

        // ban the player if this check should ban
        if (check.configuration().shouldBan(level) && !punishmentManager.hasPendingBan(player)) {
            violationResult.addResult(ViolationResult.Result.BAN);
            punishmentManager.banPlayer(player, check);
        }

        // kick the player if this check should kick.
        if (check.configuration().shouldKick(level) && !punishmentManager.hasPendingKick(player)) {
            violationResult.addResult(ViolationResult.Result.KICK);
            punishmentManager.kickPlayer(player, check);
        }

        // fire the post event
        if (configuration.enableEventApi()) {
            final PostPlayerViolationEvent postEvent = new PostPlayerViolationEvent(player, violationResult, check.type(), level, result.information());
            Arc.triggerEvent(postEvent);
        }

        return violationResult;
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
        return history.get(player.getUniqueId()).getViolationLevel(check);
    }

    @Override
    public void reload(ArcConfiguration configuration) {
        this.configuration = configuration;

        historyCache.invalidateAll();
        historyCache = CacheBuilder.newBuilder()
                .expireAfterWrite(configuration.violationDataTimeout(), TimeUnit.MINUTES)
                .build();
    }

    @Override
    public void close() {
        history.clear();
        violationViewers.clear();
        if (historyCache != null) historyCache.invalidateAll();
    }

}
