package arc.violation;

import arc.Arc;
import arc.api.events.PlayerViolationEvent;
import arc.api.events.PostPlayerViolationEvent;
import arc.check.Check;
import arc.check.result.CheckResult;
import arc.configuration.ArcConfiguration;
import arc.configuration.punishment.ban.BanConfiguration;
import arc.configuration.punishment.ban.BanLengthType;
import arc.configuration.punishment.kick.KickConfiguration;
import arc.permissions.Permissions;
import arc.violation.result.ViolationResult;
import org.apache.commons.lang3.time.DateUtils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages violations
 */
public final class ViolationManager {

    /**
     * Violation history
     */
    private final Map<UUID, Violations> history = new ConcurrentHashMap<>();

    /**
     * A list of players who can view violations.
     */
    private final List<Player> violationViewers = new CopyOnWriteArrayList<>();

    /**
     * Keeps track of bans.
     * TODO: Cancel bans
     */
    private final List<UUID> pendingPlayerBans = new CopyOnWriteArrayList<>();

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
        if (Permissions.canViewViolations(player)) violationViewers.add(player);
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
            final var message = replaceConfigurableMessage(rawMessage,
                    Map.of("%player%", player.getName(), "%check%", check.getName(), "%level%", level + "", "%information%", result.information()));

            // notify
            violationViewers.forEach(viewer -> viewer.sendMessage(message));
        }

        // add a cancel result if this check should cancel.
        if (check.configuration().shouldCancel(level)) violationResult.addResult(ViolationResult.Result.CANCEL);

        // ban the player if this check should ban
        if (check.configuration().shouldBan(level)) {
            violationResult.addResult(ViolationResult.Result.BAN);
            banPlayer(player, check);
        }

        // kick the player if this check should kick.
        if (check.configuration().shouldKick(level)) {
            violationResult.addResult(ViolationResult.Result.KICK);
            kickPlayer(player, check);
        }

        // fire the post event
        final var postEvent = new PostPlayerViolationEvent(player, violationResult, check.type(), level, result.information());
        Bukkit.getPluginManager().callEvent(postEvent);

        return violationResult;
    }

    /**
     * Ban a player
     *
     * @param player the player
     * @param check  the check banned for
     */
    private void banPlayer(final Player player, final Check check) {
        pendingPlayerBans.add(player.getUniqueId());

        // grab basic configuration values.
        final var banLengthType = banConfiguration.banLengthType();
        final var banDelay = banConfiguration.banDelay();
        final var now = new Date();

        // retrieve the date of how long the player should be banned.
        final var banDate = banLengthType == BanLengthType.DAYS ?
                DateUtils.addDays(now, banConfiguration.banLength()) :
                banLengthType == BanLengthType.YEARS ?
                        DateUtils.addYears(now, banConfiguration.banLength()) : null;

        // notify violation watchers of the ban.
        final String banViolationMessage = replaceConfigurableMessage(banConfiguration.banMessageToViolations(),
                Map.of("%player%", player.getName(), "%check%", check.getName(), "%time%", banDelay + ""));
        Bukkit.broadcast(banViolationMessage, Permissions.ARC_VIOLATIONS);

        // finally, schedule the players ban.
        scheduleBan(player, banConfiguration.banType(), banConfiguration.banMessage(), banConfiguration.banBroadcastMessage(), check.getName(), banDate, banConfiguration.broadcastBan(), banDelay);
    }

    /**
     * Schedule a player ban
     *
     * @param player           the player
     * @param banType          the type of ban
     * @param banMessage       the ban message
     * @param broadcastMessage the message to broadcast
     * @param checkName        the check name the player was banned for
     * @param banLength        how long the ban is
     * @param broadcastBan     if the ban should be broadcasted
     * @param banDelay         the delay before banning the player.
     */
    private void scheduleBan(Player player, BanList.Type banType, String banMessage, String broadcastMessage, String checkName, Date banLength, boolean broadcastBan, int banDelay) {
        Bukkit.getScheduler().runTaskLater(Arc.plugin(), () -> {
            // add the ban to the list.
            final var isIpBan = banType == BanList.Type.IP;
            Bukkit.getBanList(banType).addBan(isIpBan ? player.getAddress().getHostName() : player.getName(), banMessage, banLength, banMessage);

            // kick the player and remove them from pending bans.
            player.kickPlayer(banMessage);
            pendingPlayerBans.remove(player.getUniqueId());

            // broadcast the ban if applicable.
            if (broadcastBan) {
                final String banBroadcastMessage = replaceConfigurableMessage(broadcastMessage,
                        Map.of("%player%", player.getName(), "%check%", checkName));
                Bukkit.broadcastMessage(banBroadcastMessage);
            }
        }, banDelay * 20);
    }

    /**
     * Kick the player
     *
     * @param player the player
     * @param check  the check kicked for
     */
    private void kickPlayer(Player player, Check check) {
        final var kickMessage = replaceConfigurableMessage(kickConfiguration.kickMessage(), Map.of("%check%", check.getName()));
        Bukkit.getScheduler().runTaskLater(Arc.plugin(), () -> player.kickPlayer(kickMessage), kickConfiguration.kickDelay() * 20);
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
