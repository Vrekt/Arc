package arc.check.moving;

import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import arc.data.moving.packets.MovingPacketData;
import arc.violation.result.ViolationResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Ensures too many packets aren't being sent at once.
 * TODO: Needs work!
 */
public final class MorePackets extends Check {

    /**
     * The kick broadcast message
     */
    private final String kickBroadcastMessage = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Arc" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE
            + "%player%" + ChatColor.WHITE + " was kicked for sending too many packets. ";

    /**
     * Max flying packets allowed
     * Max position packets allowed
     * Max packets allowed before kicking
     */
    private int maxFlyingPackets, maxPositionPackets, maxPacketsToKick;

    public MorePackets() {
        super(CheckType.MORE_PACKETS);
        enabled(true).
                cancel(true).
                cancelLevel(0).
                notify(true).
                notifyEvery(1).
                ban(true).
                banLevel(20).
                kick(false).
                write();

        addConfigurationValue("max-flying-packets", 30);
        addConfigurationValue("max-position-packets", 30);
        addConfigurationValue("max-packets-kick", 50);

        if (enabled()) load();
    }

    /**
     * Check this player
     *
     * @param player the player
     * @param data   the data
     */
    private void check(Player player, MovingData data) {
        final CheckResult result = new CheckResult();
        final MovingPacketData packets = data.packets();
        // we were exempt for awhile or some type of lag occurred so reset.
        if (System.currentTimeMillis() - packets.lastCheck() >= 2000) {
            packets.flyingPackets(0);
            packets.positionPackets(0);
        }

        final int flyingPackets = packets.flyingPackets();
        final int positionPackets = packets.positionPackets();
        boolean failedFlying = false, failedPosition = false;

        if (flyingPackets >= maxFlyingPackets) {
            if (flyingPackets >= maxPacketsToKick && !packets.kick()) {
                kick(player, kickBroadcastMessage.replace("%player%", player.getName()));
                packets.kick(true);
            }
            result.setFailed("Too many flying packets p=" + flyingPackets + " max=" + maxFlyingPackets);
            failedFlying = true;
        } else {
            packets.cancelFlyingPackets(false);
        }

        if (positionPackets >= maxPositionPackets) {
            if (positionPackets >= maxPacketsToKick && !packets.kick()) {
                kick(player, kickBroadcastMessage.replace("%player%", player.getName()));
                packets.kick(true);
            }
            result.setFailed("Too many position packets p=" + positionPackets + " max=" + maxPositionPackets);
            failedPosition = true;
        } else {
            packets.cancelPositionPackets(false);
        }

        final ViolationResult violation = result(player, result);
        if (violation.cancel()) {
            packets.cancelFlyingPackets(failedFlying);
            packets.cancelPositionPackets(failedPosition);
        }

        packets.flyingPackets(0);
        packets.positionPackets(0);
        packets.lastCheck(System.currentTimeMillis());
    }

    @Override
    public void reloadConfig() {
        unload();

        if (enabled()) {
            load();
        }
    }

    @Override
    public void load() {
        maxFlyingPackets = getValueInt("max-flying-packets");
        maxPositionPackets = getValueInt("max-position-packets");
        maxPacketsToKick = getValueInt("max-packets-kick");

        scheduledCheck(() -> {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                if (!exempt(player)) check(player, MovingData.get(player));
            }
        }, 20, 20);
    }

    @Override
    public void unload() {
        cancelScheduled();
    }
}
