package arc.check.network;

import arc.Arc;
import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.packet.PacketData;
import arc.violation.result.ViolationResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Checks if the player is sending too many swing packets.
 */
public final class SwingFrequency extends PacketCheck {

    /**
     * Max packets and max packets to kick.
     */
    private int maxPackets, maxPacketsKick;

    /**
     * Kick broadcast message
     */
    private String kickBroadcastMessage;

    public SwingFrequency() {
        super(CheckType.SWING_FREQUENCY);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(true)
                .banLevel(10)
                .kick(true)
                .kickLevel(5)
                .build();

        addConfigurationValue("max-packets", 50);
        addConfigurationValue("max-packets-kick", 100);
        if (enabled()) load();
    }

    /**
     * Check this player
     *
     * @param player the player
     * @param data   the packet data
     */
    private void check(Player player, PacketData data) {
        final CheckResult result = new CheckResult();

        if (data.swingPacketCount() > maxPackets) {
            result.setFailed("Too many swing packets per second, p=" + data.swingPacketCount() + " m=" + maxPackets);
        } else if (data.swingPacketCount() > maxPacketsKick) {
            kick(player, kickBroadcastMessage.replace("%player%", player.getName()));
        } else {
            data.cancelSwingPackets(false);
        }

        data.swingPacketCount(0);

        final ViolationResult violation = checkViolation(player, result);
        if (violation.cancel()) {
            data.cancelSwingPackets(true);
        }
    }

    @Override
    public void reloadConfig() {
        unload();

        if (enabled()) load();
    }

    @Override
    public void load() {
        maxPackets = configuration.getInt("max-packets");
        maxPacketsKick = configuration.getInt("max-packets-kick");
        kickBroadcastMessage = Arc.arc().configuration().prefix() + ChatColor.RED +
                " %player% was kicked for sending too many swing packets.";

        schedule(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!exempt(player)) check(player, PacketData.get(player));
            }
        }, 20, 20);
    }

    @Override
    public void unload() {
        cancelScheduled();
    }
}
