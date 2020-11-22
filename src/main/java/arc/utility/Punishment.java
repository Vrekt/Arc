package arc.utility;

import arc.Arc;
import arc.check.Check;
import arc.configuration.punishment.ban.BanConfiguration;
import arc.configuration.punishment.ban.BanLengthType;
import arc.configuration.punishment.kick.KickConfiguration;
import arc.permissions.Permissions;
import org.apache.commons.lang3.time.DateUtils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Handles punishment
 */
public final class Punishment {

    /**
     * Keeps track of bans.
     */
    private static final List<Player> PENDING_PLAYER_BANS = new CopyOnWriteArrayList<>();

    /**
     * Keeps track of kicks
     */
    private static final List<UUID> PENDING_PLAYER_KICKS = new CopyOnWriteArrayList<>();

    /**
     * Check if the player has a pending ban
     *
     * @param player the player
     * @return {@code true} if so
     */
    public static boolean hasPendingBan(Player player) {
        return PENDING_PLAYER_BANS.contains(player);
    }

    /**
     * Check if a player name has a pending ban
     *
     * @param name the name
     * @return {@code true} if so
     */
    public static boolean hasPendingBan(String name) {
        return PENDING_PLAYER_BANS.stream().anyMatch(player -> player.getName().equals(name));
    }

    /**
     * Cancel a ban
     *
     * @param name the player name
     */
    public static void cancelBan(String name) {
        PENDING_PLAYER_BANS.removeIf(player -> player.getName().equals(name));
    }

    /**
     * Ban a player
     *
     * @param player the player
     * @param check  the check banned for
     */
    public static void banPlayer(Player player, Check check, BanConfiguration banConfiguration) {
        PENDING_PLAYER_BANS.add(player);

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

        final var banMessage = replaceConfigurableMessage(banConfiguration.banMessage(), Map.of("%check%", check.getName()));
        final var broadcastBanMessage = replaceConfigurableMessage(banConfiguration.banBroadcastMessage(), Map.of("%player%", player.getName(), "%check%", check.getName()));

        // finally, schedule the players ban.
        scheduleBan(player, banConfiguration.banType(), banMessage, broadcastBanMessage, check.getName(), banDate, banConfiguration.broadcastBan(), banDelay);
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
    public static void scheduleBan(Player player, BanList.Type banType, String banMessage, String broadcastMessage, String checkName, Date banLength, boolean broadcastBan, int banDelay) {
        Bukkit.getScheduler().runTaskLater(Arc.plugin(), () -> {
            if (!Punishment.hasPendingBan(player)) {
                return;
            }
            // add the ban to the list.
            final var isIpBan = banType == BanList.Type.IP;
            Bukkit.getBanList(banType).addBan(isIpBan ? player.getAddress().getHostName() : player.getName(), banMessage, banLength, banMessage);

            // kick the player and remove them from pending bans.
            player.kickPlayer(banMessage);
            PENDING_PLAYER_BANS.remove(player);

            // broadcast the ban if applicable.
            if (broadcastBan) {
                final String banBroadcastMessage = replaceConfigurableMessage(broadcastMessage,
                        Map.of("%player%", player.getName(), "%check%", checkName));
                Bukkit.broadcastMessage(banBroadcastMessage);
            }
        }, banDelay * 20);
    }

    /**
     * Check if the player has a pending kick
     *
     * @param player the player
     * @return {@code true} if so
     */
    public static boolean hasPendingKick(Player player) {
        return PENDING_PLAYER_KICKS.contains(player.getUniqueId());
    }

    /**
     * Kick the player
     *
     * @param player the player
     * @param check  the check kicked for
     */
    public static void kickPlayer(Player player, Check check, KickConfiguration kickConfiguration) {
        PENDING_PLAYER_KICKS.add(player.getUniqueId());
        final var kickMessage = replaceConfigurableMessage(kickConfiguration.kickMessage(), Map.of("%check%", check.getName()));
        Bukkit.getScheduler().runTaskLater(Arc.plugin(), () -> {
            player.kickPlayer(kickMessage);
            PENDING_PLAYER_KICKS.remove(player.getUniqueId());
        }, kickConfiguration.kickDelay() * 20);
    }

    /**
     * Replace placeholders within a configurable message.
     *
     * @param message the message
     * @param entries the entries
     * @return the string
     */
    private static String replaceConfigurableMessage(String message, Map<String, String> entries) {
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return message;
    }

}
