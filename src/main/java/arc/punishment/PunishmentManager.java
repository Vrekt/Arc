package arc.punishment;

import arc.Arc;
import arc.api.events.PlayerPendingBanEvent;
import arc.api.events.PlayerPendingKickEvent;
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
 * TODO: Offline support
 */
public final class PunishmentManager implements Configurable, Closeable {

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
     * Ban plugin support.
     */
    private boolean enableEventApi, useLiteBans;

    @Override
    public void loadConfiguration(ArcConfiguration configuration) {
        readFromArc(configuration);
    }

    @Override
    public void reloadConfiguration(ArcConfiguration configuration) {
        readFromArc(configuration);
    }

    @Override
    public void readFromArc(ArcConfiguration configuration) {
        this.banConfiguration = configuration.getBanConfiguration();
        this.kickConfiguration = configuration.getKickConfiguration();

        enableEventApi = configuration.enableEventApi();
        useLiteBans = configuration.useLiteBans();
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
     * Cancel a kick
     *
     * @param player the player
     */
    public void cancelKick(Player player) {
        pendingPlayerKicks.remove(player);
    }

    /**
     * Ban a player
     *
     * @param player the player
     * @param check  the check
     */
    public void banPlayer(Player player, Check check) {
        if (useLiteBans) {
            banPlayerUsingLiteBans(player, check);
        } else {
            banPlayerNormal(player, check);
        }
    }

    /**
     * Ban a player using lite bans.
     *
     * @param player the player
     * @param check  the check
     */
    private void banPlayerUsingLiteBans(Player player, Check check) {
        pendingPlayerBans.add(player);

        // get the date needed to ban the player.
        final int length = banConfiguration.globalBanLength();
        final BanLengthType lengthType = banConfiguration.globalBanLengthType();

        // get ban date.
        final Date date = lengthType == BanLengthType.PERM ? null
                : lengthType == BanLengthType.DAYS ? DateUtils.addDays(new Date(), length)
                : DateUtils.addYears(new Date(), length);

        if (enableEventApi) {
            final PlayerPendingBanEvent event = new PlayerPendingBanEvent(player, check, date, banConfiguration.globalBanDelay(), useLiteBans);
            Arc.triggerEvent(event);

            if (event.isCancelled()) {
                pendingPlayerBans.remove(player);
                return;
            }
            Bukkit.getScheduler().runTaskLater(Arc.getPlugin(), () -> banPlayerUsingLiteBans(player, check, lengthType, length), event.getDelay() * 20L);
        } else {
            Bukkit.getScheduler().runTaskLater(Arc.getPlugin(), () -> banPlayerUsingLiteBans(player, check, lengthType, length), banConfiguration.globalBanDelay() * 20L);
        }

        // build the message to send to violation viewers, then send it.
        final String violation = banConfiguration.globalViolationsBanMessage()
                .player(player)
                .check(check, null)
                .prefix()
                .time(banConfiguration.globalBanDelay())
                .value();
        BukkitAccess.broadcast(violation, Permissions.ARC_VIOLATIONS);
    }


    /**
     * Ban a player normally.
     *
     * @param player the player
     * @param check  the check
     */
    private void banPlayerNormal(Player player, Check check) {
        pendingPlayerBans.add(player);

        // get the date needed to ban the player.
        final int length = banConfiguration.globalBanLength();
        final BanLengthType lengthType = banConfiguration.globalBanLengthType();

        final Date date = lengthType == BanLengthType.PERM ? null
                : lengthType == BanLengthType.DAYS ? DateUtils.addDays(new Date(), length)
                : DateUtils.addYears(new Date(), length);

        if (enableEventApi) {
            final PlayerPendingBanEvent event = new PlayerPendingBanEvent(player, check, date, banConfiguration.globalBanDelay(), useLiteBans);
            Arc.triggerEvent(event);

            if (event.isCancelled()) {
                pendingPlayerBans.remove(player);
                return;
            }

            Bukkit.getScheduler().runTaskLater(Arc.getPlugin(), () -> banPlayerNormal(player, check, event.getDate(), length), event.getDelay() * 20L);
        } else {
            Bukkit.getScheduler().runTaskLater(Arc.getPlugin(), () -> banPlayerNormal(player, check, date, length), banConfiguration.globalBanDelay() * 20L);
        }

        // build the message to send to violation viewers, then send it.
        final String violation = banConfiguration.globalViolationsBanMessage()
                .player(player)
                .check(check, null)
                .prefix()
                .time(banConfiguration.globalBanDelay())
                .value();

        BukkitAccess.broadcast(violation, Permissions.ARC_VIOLATIONS);
    }

    /**
     * Ban the player
     *
     * @param player the player
     * @param check  the check
     * @param date   the ban date
     * @param time   the ban time, days, years, etc
     */
    private void banPlayerNormal(Player player, Check check, Date date, int time) {
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
        if (player.isOnline()) BukkitAccess.kickPlayer(player, message);

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
     * Ban a player use lite bans
     *
     * @param player        the player
     * @param check         the check
     * @param banLengthType the length type
     * @param length        the length
     */
    private void banPlayerUsingLiteBans(Player player, Check check, BanLengthType banLengthType, int length) {
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

        // Do not forget the space!
        final String command = banConfiguration.getLiteBansCommand()
                .command(type == BanList.Type.IP ? "/ipban " : "/ban ")
                .playerOrIpAddress(playerBan)
                .reason(message)
                .length(banLengthType, length)
                .toString();

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        pendingPlayerBans.remove(player);

        // broadcast the ban
        if (banConfiguration.globalBroadcastBan()) {
            final boolean hasTime = banLengthType != BanLengthType.PERM;
            ConfigurationString configMessage = banConfiguration.globalBroadcastBanMessage()
                    .player(player)
                    .check(check, null)
                    .prefix();

            // replace the time placeholder
            if (hasTime) {
                configMessage.time(length);
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

        long timeDelay = kickConfiguration.globalKickDelay() * 20L;
        if (enableEventApi) {
            final PlayerPendingKickEvent event = new PlayerPendingKickEvent(player, check, timeDelay);
            Arc.triggerEvent(event);

            if (event.isCancelled()) {
                pendingPlayerKicks.remove(player);
                return;
            }

            timeDelay = event.getDelay();
        }

        BukkitAccess.broadcast(violationsMessage, Permissions.ARC_VIOLATIONS);
        Bukkit.getScheduler().runTaskLater(Arc.getPlugin(), () -> {
            final String message = kickConfiguration.globalKickMessage()
                    .check(check, null)
                    .value();
            pendingPlayerKicks.remove(player);
            BukkitAccess.kickPlayer(player, message);
        }, timeDelay);
    }

    @Override
    public void close() {
        pendingPlayerBans.clear();
        pendingPlayerKicks.clear();
    }

}
