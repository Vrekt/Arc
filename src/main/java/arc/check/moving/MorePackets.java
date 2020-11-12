package arc.check.moving;

import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import com.comphenix.packetwrapper.WrapperPlayClientFlying;
import com.comphenix.packetwrapper.WrapperPlayClientPosition;
import com.comphenix.packetwrapper.WrapperPlayClientPositionLook;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Ensures too many packets aren't being sent at once.
 * TODO: Needs work!
 */
public final class MorePackets extends PacketCheck {

    /**
     * Max flying packets allowed
     * Max position packets allowed
     * Max packets allowed before kicking
     */
    private final int maxFlyingPackets, maxPositionPackets, maxPacketsToKick;

    public MorePackets() {
        super("MorePackets", CheckType.MORE_PACKETS);
        writeConfiguration(true, true, 0, true, 1, true, 20, false, 0);

        addConfigurationValue("max-flying-packets", 30);
        addConfigurationValue("max-position-packets", 30);
        addConfigurationValue("max-packets-kick", 50);
        maxFlyingPackets = getValueInt("max-flying-packets");
        maxPositionPackets = getValueInt("max-position-packets");
        maxPacketsToKick = getValueInt("max-packets-kick");

        if (enabled()) {
            registerListener(PacketType.Play.Client.FLYING, this::onFlying);
            registerListener(PacketType.Play.Client.POSITION, this::onPosition);
            registerListener(PacketType.Play.Client.POSITION_LOOK, this::onPositionLook);

            scheduledCheck(() -> {
                for (var player : Bukkit.getOnlinePlayers()) {
                    if (!exempt(player)) check(player, MovingData.get(player));
                }
            }, 20, 20);
        }
    }

    /**
     * Invoked when the client sends FLYING
     *
     * @param event the event
     */
    private void onFlying(PacketEvent event) {
        final var packet = new WrapperPlayClientFlying(event.getPacket());
        final var player = event.getPlayer();
        final var data = MovingData.get(player);
        final var packets = data.packets();

        data.wasClientOnGround(data.clientOnGround());
        data.clientOnGround(packet.getOnGround());

        packets.flyingPackets(packets.flyingPackets() + 1);
        if (packets.cancelFlyingPackets()) event.setCancelled(true);
    }

    /**
     * Invoked when the client sends POSITION
     *
     * @param event the event
     */
    private void onPosition(PacketEvent event) {
        final var packet = new WrapperPlayClientPosition(event.getPacket());
        final var player = event.getPlayer();
        final var data = MovingData.get(player);
        final var packets = data.packets();

        data.wasClientOnGround(data.clientOnGround());
        data.clientOnGround(packet.getOnGround());
        packets.positionPackets(packets.positionPackets() + 1);

        if (packets.cancelPositionPackets()) event.setCancelled(true);
    }

    /**
     * Invoked when the client sends POSITION_LOOK
     *
     * @param event the event
     */
    private void onPositionLook(PacketEvent event) {
        final var packet = new WrapperPlayClientPositionLook(event.getPacket());
        final var player = event.getPlayer();
        final var data = MovingData.get(player);
        final var packets = data.packets();

        data.wasClientOnGround(data.clientOnGround());
        data.clientOnGround(packet.getOnGround());

        // TODO: Separate tracker for POSITION_LOOK?
        packets.positionPackets(packets.positionPackets() + 1);
        if (packets.cancelPositionPackets()) event.setCancelled(true);
    }

    /**
     * Check this player
     *
     * @param player the player
     * @param data   the data
     */
    private void check(Player player, MovingData data) {
        final var result = new CheckResult();
        final var packets = data.packets();

        final var flyingPackets = packets.flyingPackets();
        final var positionPackets = packets.positionPackets();
        boolean failedFlying = false, failedPosition = false;

        if (flyingPackets >= maxFlyingPackets) {
            if (flyingPackets >= maxPacketsToKick && !packets.kick()) {
                kick(player);
                packets.kick(true);
            }
            result.setFailed("Too many flying packets p=" + flyingPackets + " max=" + maxFlyingPackets);
            failedFlying = true;
        } else {
            packets.cancelFlyingPackets(false);
        }

        if (positionPackets >= maxPositionPackets) {
            if (positionPackets >= maxPacketsToKick && !packets.kick()) {
                kick(player);
                packets.kick(true);
            }
            result.setFailed("Too many position packets p=" + positionPackets + " max=" + maxPositionPackets);
            failedPosition = true;
        } else {
            packets.cancelPositionPackets(false);
        }

        final var violation = result(player, result);
        if (violation.cancel()) {
            packets.cancelFlyingPackets(failedFlying);
            packets.cancelPositionPackets(failedPosition);
        }

        packets.flyingPackets(0);
        packets.positionPackets(0);
    }

}
