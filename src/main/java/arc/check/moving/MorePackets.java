package arc.check.moving;

import arc.Arc;
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
     * Max flying packets allowed
     * Max position packets allowed
     * Max packets allowed before kicking
     */
    private int maxFlyingPackets, maxPositionPackets, maxPacketsToKick;

    /**
     * The kick broadcast message.
     */
    private String kickBroadcastMessage;

    public MorePackets() {
        super(CheckType.MORE_PACKETS);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(true)
                .banLevel(20)
                .kick(false)
                .build();

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

            result.setFailed("Too many flying packets");
            result.parameter("packets", flyingPackets);
            result.parameter("max", maxFlyingPackets);

            failedFlying = true;
        } else {
            packets.cancelFlyingPackets(false);
        }

        if (positionPackets >= maxPositionPackets) {
            if (positionPackets >= maxPacketsToKick && !packets.kick()) {
                kick(player, kickBroadcastMessage.replace("%player%", player.getName()));
                packets.kick(true);
            }

            result.setFailed("Too many position packets");
            result.parameter("packets", positionPackets);
            result.parameter("max", maxPositionPackets);

            failedPosition = true;
        } else {
            packets.cancelPositionPackets(false);
        }

        final ViolationResult violation = checkViolation(player, result);
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
        maxFlyingPackets = configuration.getInt("max-flying-packets");
        maxPositionPackets = configuration.getInt("max-position-packets");
        maxPacketsToKick = configuration.getInt("max-packets-kick");
        kickBroadcastMessage = Arc.arc().configuration().prefix() + ChatColor.RED +
                " %player% was kicked for sending too many swing packets.";

        schedule(() -> {
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
