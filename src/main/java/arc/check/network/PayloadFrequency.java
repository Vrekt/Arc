package arc.check.network;

import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.packet.PacketData;
import com.comphenix.packetwrapper.WrapperPlayClientCustomPayload;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Inspects payload packets being sent by the player.
 */
public final class PayloadFrequency extends PacketCheck {

    /**
     * The kick broadcast message.
     */
    private final String kickBroadcastMessage = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Arc" + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE
            + "%player%" + ChatColor.WHITE + " was kicked for invalid payload packets. ";

    /**
     * Channels to monitor
     */
    private List<String> channels;

    /**
     * The max packet size for books
     * The max packet size for other types.
     * The max packets allowed every check
     */
    private int maxPacketSizeBooks, maxPacketSizeOthers, maxPacketsPerInterval;

    /**
     * If the player should be kicked for max packet size
     * If the player should be kicked for exceeding the max packets per interval
     */
    private boolean maxPacketSizeKick, maxPacketsPerIntervalKick;

    public PayloadFrequency() {
        super(CheckType.PAYLOAD_FREQUENCY);
        enabled(true).
                cancel(true).
                cancelLevel(0).
                notify(true).
                notifyEvery(1).
                ban(true).
                banLevel(5).
                kick(true).
                kickLevel(2).
                write();

        addConfigurationValue("max-packet-size-books", 4096);
        addConfigurationValue("max-packet-size-others", 32767);
        addConfigurationValue("check-interval-milliseconds", 500);
        addConfigurationValue("max-packets-per-interval", 1);
        addConfigurationValue("max-packet-size-kick", true);
        addConfigurationValue("max-packets-per-interval-kick", true);
        addConfigurationValue("channels", List.of("MC|BSign", "MC|BEdit"));

        if (enabled()) load();
    }

    /**
     * Check this player
     *
     * @param player the player
     * @param data   the packet data
     */
    private void check(Player player, PacketData data) {
        final var count = data.payloadPacketCount();
        final var result = new CheckResult();

        if (count > maxPacketsPerInterval) {
            result.setFailed("Too many payload packets per interval, count=" + count + " max=" + maxPacketsPerInterval);
            if (maxPacketsPerIntervalKick) {
                kick(player, kickBroadcastMessage.replace("%player%", player.getName()));
            }
        }

        final var violation = result(player, result);
        data.cancelPayloadPackets(violation.cancel());
        data.payloadPacketCount(0);
    }

    /**
     * Invoked when payload is received
     * TODO: Monitor outside configured channels?
     * TODO: Information may get omitted, need better system.
     *
     * @param event the event
     */
    private void onPayload(PacketEvent event) {
        // retrieve the player, if they are not online cancel the event and return.
        final var player = event.getPlayer();
        if (player == null || !player.isOnline()) {
            event.setCancelled(true);
            return;
        }

        // retrieve our data and the packet
        final var data = PacketData.get(player);
        if (data.cancelPayloadPackets()) event.setCancelled(true);

        final var packet = new WrapperPlayClientCustomPayload(event.getPacket());
        final var channel = packet.getChannel();
        final var result = new CheckResult();

        // if we have a valid channel to check
        if (channels.contains(channel)) {
            final var bytes = packet.getContents();
            final var max = (isBookChannel(channel) ? maxPacketSizeBooks : maxPacketSizeOthers);
            // check if the length is bigger than the allowed size
            if (bytes.length > max) {
                result.setFailed("Payload packet size too big, len=" + bytes.length + " max=" + max);
                if (maxPacketSizeKick) {
                    kick(player, kickBroadcastMessage.replace("%player%", player.getName()));
                }
            }

            data.incrementPayloadPacketCount();
        }

        final var violation = result(player, result);
        data.cancelPayloadPackets(violation.cancel());
    }

    /**
     * Check if the channel is a book channel
     *
     * @param channel the channel
     * @return {@code true} if so
     */
    private boolean isBookChannel(String channel) {
        return channel.equalsIgnoreCase("MC|BEdit") || channel.equalsIgnoreCase("MC|BSign");
    }

    @Override
    public void reloadConfig() {
        if (!enabled()) {
            unregisterPacketListeners();
            scheduled.cancel();
            scheduled = null;
        } else {
            load();
        }
    }

    @Override
    public void load() {
        maxPacketSizeBooks = getValueInt("max-packet-size-books");
        maxPacketSizeOthers = getValueInt("max-packet-size-others");
        maxPacketsPerInterval = getValueInt("max-packets-per-interval");
        maxPacketSizeKick = getValueBoolean("max-packet-size-kick");
        maxPacketsPerIntervalKick = getValueBoolean("max-packets-per-interval-kick");
        channels = getList("channels");

        final var checkInterval = getValueInt("check-interval-milliseconds");
        final var interval = (checkInterval / 1000) * 20;

        registerPacketListener(PacketType.Play.Client.CUSTOM_PAYLOAD, this::onPayload);
        scheduledCheck(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                check(player, PacketData.get(player));
            }
        }, interval, interval);

    }
}
