package arc.check.network;

import arc.check.CheckCategory;
import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.packet.PacketData;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Checks if the player is sending too many swing packets.
 */
public final class SwingFrequency extends PacketCheck {

    /**
     * The kick broadcast message
     */
    private final String kickBroadcastMessage = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Arc" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE
            + "%player%" + ChatColor.WHITE + " was kicked for sending too many swing packets.";

    /**
     * Max packets and max packets to kick.
     */
    private final int maxPackets, maxPacketsKick;

    public SwingFrequency() {
        super("SwingFrequency", CheckType.SWING_FREQUENCY, CheckCategory.NETWORK);
        enabled(true).
                cancel(true).
                cancelLevel(0).
                notify(true).
                notifyEvery(1).
                ban(true).
                banLevel(10).
                kick(true).
                kickLevel(5).
                write();

        addConfigurationValue("max-packets", 20);
        addConfigurationValue("max-packets-kick", 100);

        maxPackets = getValueInt("max-packets");
        maxPacketsKick = getValueInt("max-packets-kick");

        if (enabled()) {
            registerListener(PacketType.Play.Client.ARM_ANIMATION, this::onArmAnimation);
            scheduledCheck(() -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!exempt(player)) check(player, PacketData.get(player));
                }
            }, 20, 20);
        }
    }

    /**
     * Check this player
     *
     * @param player the player
     * @param data   the packet data
     */
    private void check(Player player, PacketData data) {
        final var result = new CheckResult();

        if (data.swingPacketCount() > maxPackets) {
            result.setFailed("Too many swing packets per second, p=" + data.swingPacketCount() + " m=" + maxPackets);
        } else if (data.swingPacketCount() > maxPacketsKick) {
            kick(player, kickBroadcastMessage.replace("%player%", player.getName()));
        } else {
            data.cancelSwingPackets(false);
            data.swingPacketCount(0);
        }

        final var violation = result(player, result);
        if (violation.cancel()) {
            data.cancelSwingPackets(true);
        }
    }

    /**
     * Invoked when the player sends arm animation
     *
     * @param event the event
     */
    private void onArmAnimation(PacketEvent event) {
        final var player = event.getPlayer();
        final var data = PacketData.get(player);
        if (data.cancelSwingPackets()) {
            event.setCancelled(true);
        }
        data.incrementSwingPacketCount();
    }

}