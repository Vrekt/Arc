package arc.violation;

import arc.Arc;
import arc.check.Check;
import arc.check.result.CheckResult;
import arc.configuration.punishment.ban.BanLengthType;
import arc.utility.ChatUtil;
import arc.violation.result.ViolationResult;
import org.apache.commons.lang3.time.DateUtils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
     * Keeps track of bans.
     * TODO: Cancel bans
     */
    private final List<UUID> playerBans = new CopyOnWriteArrayList<>();

    /**
     * Invoked when a player joins
     *
     * @param player the player
     */
    public void onPlayerJoin(Player player) {
        history.put(player.getUniqueId(), new Violations());
    }

    /**
     * Invoked when a player leaves
     *
     * @param player the player
     */
    public void onPlayerLeave(Player player) {
        final var violations = history.get(player.getUniqueId());
        violations.dispose();
        history.remove(player.getUniqueId());
    }

    /**
     * Process a violation
     *
     * @param player the player
     * @param result the result
     * @return the result
     */
    public ViolationResult violation(Player player, Check check, CheckResult result) {
        final var violationResult = new ViolationResult();
        final var violations = history.get(player.getUniqueId());
        final var level = violations.incrementViolationLevel(check.getName());

        // handle violation
        if (check.configuration().notifyViolation() && check.configuration().shouldNotify(level)) {
            violationResult.addResult(ViolationResult.Result.NOTIFY);
            final var violationMessage = translate(Arc.arc().configuration().violationMessage(), player.getName(), check.getName(), level, result.information());
            Arc.arc().permissions().violationViewers().forEach(viewer -> viewer.sendMessage(violationMessage));
        }

        // cancel if needed.
        if (check.configuration().shouldCancel(level)) {
            violationResult.addResult(ViolationResult.Result.CANCEL);
        }

        // handling banning this player
        if (check.configuration().shouldBan(level)) {
            banPlayer(player, check);
        }

        // handling kicking this player
        if (check.configuration().shouldKick(level)) {
            kickPlayer(player, check);
        }

        return violationResult;
    }

    /**
     * Ban a player
     *
     * @param player the player
     * @param check  the check banned for
     */
    private void banPlayer(final Player player, final Check check) {
        playerBans.add(player.getUniqueId());
        // retrieve all the ban configuration properties
        final var banConfig = Arc.arc().configuration().banConfiguration();
        final var banMessage = translate(banConfig.banMessage(), check.getName());
        final var banLengthType = banConfig.banLengthType();
        final var banLength = banConfig.banLength();
        final var banDelay = banConfig.banDelay();
        final var now = new Date();

        // configure the ban length based on the type selected.
        final var banTime = banLengthType == BanLengthType.DAYS ?
                DateUtils.addDays(now, banLength) :
                banLengthType == BanLengthType.YEARS ?
                        DateUtils.addYears(now, banLength) : null;

        // notify violation watchers.
        // TODO: Configurable.
        ChatUtil.broadcastToViolations(ChatColor.BLUE + player.getName() + ChatColor.WHITE +
                " will be banned for " + ChatColor.RED + check.getName() + ChatColor.WHITE +
                " in " + ChatColor.RED + banDelay + ChatColor.WHITE + " seconds.");

        // ban the player later with the ban delay, multiply by *20 since its in ticks.
        Bukkit.getScheduler().runTaskLater(Arc.plugin(), () -> {
            // add the ban to the list.
            if (banConfig.banType() == BanList.Type.NAME) {
                Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), banMessage, banTime, banMessage);
            } else {
                Bukkit.getBanList(BanList.Type.IP).addBan(player.getAddress().getHostName(), banMessage, banTime, banMessage);
            }
            // kick the player.
            player.kickPlayer(banMessage);
            playerBans.remove(player.getUniqueId());

            // broadcast the ban if applicable.
            if (banConfig.broadcastBan()) {
                Bukkit.broadcastMessage(translate(banConfig.banBroadcastMessage(), player.getName(), check.getName(), -1, null));
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
        final var kickConfig = Arc.arc().configuration().kickConfiguration();
        Bukkit.getScheduler().runTaskLater(Arc.plugin(), () -> player.kickPlayer(translate(kickConfig.kickMessage(), check.getName())), kickConfig.kickDelay() * 20);
    }

    /**
     * Replace placeholders within the configuration
     *
     * @param message     the message
     * @param player      the player name
     * @param check       the check name
     * @param level       the violation level
     * @param information the information
     * @return the string
     */
    private String translate(String message, String player, String check, int level, String information) {
        message = message.replace("%player%", player);
        message = message.replace("%check%", check);
        message = message.replace("%level%", level == -1 ? "" : level + "");
        message = message.replace("%information%", information == null ? "" : "\n" + information);
        return message;
    }

    /**
     * Replace placeholders with ban message
     *
     * @param message the message
     * @param check   the check
     * @return the string
     */
    private String translate(String message, String check) {
        message = message.replace("%check%", check);
        return message;
    }

}
