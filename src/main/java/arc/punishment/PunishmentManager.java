package arc.punishment;

import arc.Arc;
import arc.api.events.PlayerBanEvent;
import arc.check.Check;
import arc.configuration.ArcConfiguration;
import arc.configuration.Configurable;
import arc.configuration.ban.BanConfiguration;
import arc.configuration.kick.KickConfiguration;
import arc.configuration.types.BanLengthType;
import arc.configuration.types.ConfigurationString;
import arc.configuration.types.Placeholders;
import arc.permissions.Permissions;
import arc.utility.api.BukkitAccess;
import org.apache.commons.lang.time.DateUtils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.Closeable;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles punishment
 * TODO: Popular ban plugin support
 * TODO: Offline support
 */
public final class PunishmentManager extends Configurable implements Closeable {

    /**
     * A set of players who have pending bans.
     */
    private final Set<Player> pendingPlayerBans = ConcurrentHashMap.newKeySet();

    /**
     * A set of player who have pending kicks
     */
    private final Set<Player> pendingPlayerKicks = ConcurrentHashMap.newKeySet();

    /**
     * The ban configuration
     */
    private BanConfiguration banConfiguration;

    /**
     * The kick configuration
     */
    private KickConfiguration kickConfiguration;

    /**
     * Event related
     */
    private boolean enableEventApi;

    /**
     * Initialize
     *
     * @param configuration the arc config
     */
    public void initialize(ArcConfiguration configuration) {
        read(configuration);
    }

    @Override
    public void reload(ArcConfiguration configuration) {
        read(configuration);
    }

    /**
     * Read
     *
     * @param configuration the arc config
     */
    private void read(ArcConfiguration configuration) {
        this.banConfiguration = configuration.banConfiguration();
        this.kickConfiguration = configuration.kickConfiguration();

        enableEventApi = Arc.getInstance().getArcConfiguration().enableEventApi();
    }

    /**
     * Check if the player has a pending ban
     *
     * @param player the player
     * @return {@code true} if so
     */
    public boolean hasPendingBan(Player player) {
        return pendingPlayerBans.contains(player);
    }

    /**
     * Check if a player name has a pending ban
     *
     * @param name the name
     * @return {@code true} if so
     */
    public boolean hasPendingBan(String name) {
        return pendingPlayerBans.stream().anyMatch(player -> player.getName().equals(name));
    }

    /**
     * Check if the player has a pending kick
     *
     * @param player the player
     * @return {@code true} if so
     */
    public boolean hasPendingKick(Player player) {
        return pendingPlayerKicks.contains(player);
    }

    /**
     * Cancel a ban
     *
     * @param name the player name
     */
    public void cancelBan(String name) {
        pendingPlayerBans.removeIf(player -> player.getName().equals(name));
    }

    /**
     * Ban a player
     *
     * @param player the player
     * @param check  the check
     */
    public void banPlayer(Player player, Check check) {
        pendingPlayerBans.add(player);

        // get the date needed to ban the player.
        final int length = banConfiguration.globalBanLength();
        final BanLengthType lengthType = banConfiguration.globalBanLengthType();

        Date date = null;
        switch (lengthType) {
            case DAYS:
                date = DateUtils.addDays(new Date(), length);
                break;
            case YEARS:
                date = DateUtils.addYears(new Date(), length);
                break;
        }

        // build the message to send to violation viewers, then send it.
        final String violation = banConfiguration.globalViolationsBanMessage()
                .player(player)
                .check(check, null)
                .prefix()
                .time(banConfiguration.globalBanDelay())
                .value();
        BukkitAccess.broadcast(violation, Permissions.ARC_VIOLATIONS);

        // schedule the player ban
        final Date finalDate = date;

        if (enableEventApi) {
            final PlayerBanEvent event = new PlayerBanEvent(player, check, finalDate, banConfiguration.globalBanDelay());
            Arc.triggerEvent(event);

            if (event.isCancelled()) {
                pendingPlayerBans.remove(player);
                return;
            }

            final Date eventDate = event.date();
            final int eventDelay = event.delay();
            Bukkit.getScheduler().runTaskLater(Arc.getPlugin(), () -> ban(player, check, eventDate, length), eventDelay * 20L);
        } else {
            Bukkit.getScheduler().runTaskLater(Arc.getPlugin(), () -> ban(player, check, finalDate, length), banConfiguration.globalBanDelay() * 20L);
        }
    }

    /**
     * Ban the player
     *
     * @param player the player
     * @param check  the check
     * @param date   the ban date
     * @param time   the ban time, days, years, etc
     */
    private void ban(Player player, Check check, Date date, int time) {
        if (!hasPendingBan(player.getName())) return;
        final BanList.Type type = banConfiguration.globalBanType();
        if (type == BanList.Type.IP && player.getAddress() == null) {
            Arc.getPlugin().getLogger().warning("Failed to ban player " + player.getName());
            pendingPlayerBans.remove(player);
            return;
        }

        final String playerBan = type == BanList.Type.IP ? player.getAddress().getHostName() : player.getName();
        final String message = banConfiguration.globalBanMessage()
                .check(check, null)
                .value();

        // ban the player and kick them
        Bukkit.getBanList(type).addBan(playerBan, message, date, message);
        BukkitAccess.kickPlayer(player, message);

        pendingPlayerBans.remove(player);

        // broadcast the ban
        if (banConfiguration.globalBroadcastBan()) {
            final boolean hasTime = date != null;
            ConfigurationString configMessage = banConfiguration.globalBroadcastBanMessage()
                    .player(player)
                    .check(check, null)
                    .prefix();

            // replace the time placeholder
            if (hasTime) {
                configMessage.time(time);
            } else {
                configMessage.replace(Placeholders.TIME, "");
            }

            // broadcast
            final String broadcast = configMessage.type().value();
            BukkitAccess.broadcast(broadcast);
        }

    }

    /**
     * Kick a player
     *
     * @param player the player
     * @param check  the check
     */
    public void kickPlayer(Player player, Check check) {
        pendingPlayerKicks.add(player);
        final String violationsMessage = kickConfiguration.globalViolationsKickMessage()
                .check(check, null)
                .player(player)
                .prefix()
                .time(kickConfiguration.globalKickDelay())
                .value();
        BukkitAccess.broadcast(violationsMessage, Permissions.ARC_VIOLATIONS);
        Bukkit.getScheduler().runTaskLater(Arc.getPlugin(), () -> {
            final String message = kickConfiguration.globalKickMessage()
                    .check(check, null)
                    .value();
            BukkitAccess.kickPlayer(player, message);
            pendingPlayerKicks.remove(player);
        }, kickConfiguration.globalKickDelay() * 20L);
    }

    @Override
    public void close() {
        pendingPlayerBans.clear();
        pendingPlayerKicks.clear();
    }

}
