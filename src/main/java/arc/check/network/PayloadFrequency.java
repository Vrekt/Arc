package arc.check.network;

import arc.Arc;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.check.types.CheckType;
import arc.data.packet.PacketData;
import arc.world.WorldManager;
import com.comphenix.packetwrapper.WrapperPlayClientCustomPayload;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Inspects payload packets being sent by the player.
 */
public final class PayloadFrequency extends PacketCheck {

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

    /**
     * Check only enables if the version is legacy.
     */
    public PayloadFrequency() {
        super(CheckType.PAYLOAD_FREQUENCY);

        isEnabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(true)
                .banLevel(5)
                .kick(true)
                .kickLevel(2)
                .build();

        addConfigurationValue("max-packet-size-books", 4096);
        addConfigurationValue("max-packet-size-others", 32767);
        addConfigurationValue("check-interval-milliseconds", 1000);
        addConfigurationValue("max-packets-per-interval", 1);
        addConfigurationValue("max-packet-size-kick", true);
        addConfigurationValue("max-packets-per-interval-kick", true);
        addConfigurationValue("channels", Lists.newArrayList("MC|BSign", "MC|BEdit"));

        if (isEnabled()) load();
    }

    /**
     * Check this player
     *
     * @param player the player
     * @param data   the packet data
     */
    private void check(Player player, PacketData data) {
        final int count = data.payloadPacketCount();
        final CheckResult result = new CheckResult();

        if (count > maxPacketsPerInterval) {
            result.setFailed("Too many payload packets per interval.")
                    .withParameter("count", count)
                    .withParameter("max", maxPacketsPerInterval);

            if (maxPacketsPerIntervalKick
                    && !Arc.getInstance().getPunishmentManager().hasPendingKick(player)) {
                Arc.getInstance().getPunishmentManager().kickPlayer(player, this);
            }
        }

        data.cancelPayloadPackets(checkViolation(player, result));
        data.payloadPacketCount(0);
    }

    /**
     * Invoked when payload is received
     * TODO: Monitor outside configured channels?
     *
     * @param event the event
     */
    private void onPayload(PacketEvent event) {
        // retrieve the player, if they are not online cancel the event and return.
        final Player player = event.getPlayer();
        if (player == null || !player.isOnline()) {
            event.setCancelled(true);
            return;
        }

        if (exempt(player)) return;
        final PacketData data = PacketData.get(player);
        data.incrementPayloadPacketCount();

        if (data.cancelPayloadPackets()) {
            event.setCancelled(true);
            return;
        }

        final WrapperPlayClientCustomPayload packet = new WrapperPlayClientCustomPayload(event.getPacket());
        final String channel = packet.getChannel();
        final CheckResult result = new CheckResult();

        // if we have a valid channel to check
        if (channels.contains(channel)) {
            final byte[] bytes = packet.getContents();
            final int max = (isBookChannel(channel) ? maxPacketSizeBooks : maxPacketSizeOthers);
            // check if the length is bigger than the allowed size
            if (bytes.length >= max) {
                result.setFailed("Payload packet size too big.")
                        .withParameter("length", bytes.length)
                        .withParameter("max", max);

                if (maxPacketSizeKick
                        && !Arc.getInstance().getPunishmentManager().hasPendingKick(player)) {
                    Arc.getInstance().getPunishmentManager().kickPlayer(player, this);
                }
            }
        }

        data.cancelPayloadPackets(checkViolation(player, result));
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
        load();
    }

    @Override
    public void load() {
        maxPacketSizeBooks = configuration.getInt("max-packet-size-books");
        maxPacketSizeOthers = configuration.getInt("max-packet-size-others");
        maxPacketsPerInterval = configuration.getInt("max-packets-per-interval");
        maxPacketSizeKick = configuration.getBoolean("max-packet-size-kick");
        maxPacketsPerIntervalKick = configuration.getBoolean("max-packets-per-interval-kick");
        channels = configuration.getList("channels");

        // PEMDAS!
        final long checkInterval = configuration.getLong("check-interval-milliseconds") * 20 / 1000;
        registerPacketListener(PacketType.Play.Client.CUSTOM_PAYLOAD, this::onPayload);
        schedule(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!WorldManager.isEnabledInWorld(player)) continue;
                if (!exempt(player)) check(player, PacketData.get(player));
            }
        }, checkInterval, checkInterval);
    }

    @Override
    public void unload() {
        unregisterPacketListeners();
        cancelScheduled();
    }
}
