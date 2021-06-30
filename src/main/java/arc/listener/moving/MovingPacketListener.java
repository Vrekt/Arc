package arc.listener.moving;

import arc.Arc;
import arc.check.types.CheckType;
import arc.check.moving.*;
import arc.data.moving.MovingData;
import arc.listener.AbstractPacketListener;
import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;

/**
 * Listens for player movement packets.
 */
public final class MovingPacketListener extends AbstractPacketListener {

    /**
     * The MorePackets check
     */
    private final MorePackets morePackets;

    public MovingPacketListener() {
        morePackets = (MorePackets) Arc.getInstance().getCheckManager().getCheck(CheckType.MORE_PACKETS);
    }

    @Override
    public void register(ProtocolManager protocol) {
        listener(protocol, PacketType.Play.Client.LOOK, this::onLook);
        listener(protocol, PacketType.Play.Client.FLYING, this::onFlying);
        listener(protocol, PacketType.Play.Client.POSITION, this::onPosition);
        listener(protocol, PacketType.Play.Client.POSITION_LOOK, this::onPositionLook);
        listener(protocol, PacketType.Play.Client.ENTITY_ACTION, this::onEntityAction);
    }

    /**
     * Invoked when a flying packet is received.
     *
     * @param event the event
     */
    private void onFlying(PacketEvent event) {
        final Player player = event.getPlayer();
        if (!player.isOnline()) {
            event.setCancelled(true);
            return;
        }

        final MovingData data = MovingData.get(player);
        final WrapperPlayClientFlying packet = new WrapperPlayClientFlying(event.getPacket());
        data.lastFlyingPacket(System.currentTimeMillis());
        data.flyingPackets(data.flyingPackets() + 1);
        // do not update ground if this packet is cancelled.
        if (morePackets.cancelPacket(data, true, false, false)) {
            event.setCancelled(true);
            return;
        }

        updateClientGround(data, packet.getOnGround(), true, false);
    }

    /**
     * Invoked when a position packet is received.
     *
     * @param event the event
     */
    private void onPosition(PacketEvent event) {
        final Player player = event.getPlayer();
        if (!player.isOnline()) {
            event.setCancelled(true);
            return;
        }

        final MovingData data = MovingData.get(player);
        final WrapperPlayClientPosition packet = new WrapperPlayClientPosition(event.getPacket());
        data.positionPackets(data.positionPackets() + 1);

        // do not update ground/pos if this packet is cancelled.
        if (morePackets.cancelPacket(data, false, true, false)) {
            event.setCancelled(true);
            return;
        }

        updateClientGround(data, packet.getOnGround(), false, false);
    }

    /**
     * Invoked when a position look packet is received
     *
     * @param event the event
     */
    private void onPositionLook(PacketEvent event) {
        final Player player = event.getPlayer();
        if (!player.isOnline()) {
            event.setCancelled(true);
            return;
        }

        final MovingData data = MovingData.get(player);
        final WrapperPlayClientPositionLook packet = new WrapperPlayClientPositionLook(event.getPacket());
        data.positionLookPackets(data.positionLookPackets() + 1);

        // do not update ground/pos if this packet is cancelled.
        if (morePackets.cancelPacket(data, false, true, false)) {
            event.setCancelled(true);
            return;
        }

        updateClientGround(data, packet.getOnGround(), false, false);
    }

    /**
     * Invoked when a look packet is received
     *
     * @param event the event
     */
    private void onLook(PacketEvent event) {
        final Player player = event.getPlayer();
        if (!player.isOnline()) {
            event.setCancelled(true);
            return;
        }

        final MovingData data = MovingData.get(player);
        final WrapperPlayClientLook packet = new WrapperPlayClientLook(event.getPacket());
        data.lookPackets(data.lookPackets() + 1);

        // do not update ground if this packet is cancelled.
        if (morePackets.cancelPacket(data, false, false, true)) {
            event.setCancelled(true);
            return;
        }
        updateClientGround(data, packet.getOnGround(), false, true);
    }

    /**
     * Invoked when the client sends ENTITY_ACTION
     *
     * @param event the event
     */
    private void onEntityAction(PacketEvent event) {
        final WrapperPlayClientEntityAction packet = new WrapperPlayClientEntityAction(event.getPacket());
        final MovingData data = MovingData.get(event.getPlayer());

        switch (packet.getAction()) {
            case START_SNEAKING:
                data.sneaking(true);
                break;
            case STOP_SNEAKING:
                data.sneaking(false);
                break;
            case START_SPRINTING:
                data.sprinting(true);
                break;
            case STOP_SPRINTING:
                data.sprinting(false);
                break;
        }
    }

    /**
     * Update client ground status
     * TODO: I think this is wrong.
     *
     * @param data     their data
     * @param onGround if the client is on ground.
     * @param isFlying if the packet is flying
     * @param isLook   if the packet is look.
     */
    private void updateClientGround(MovingData data, boolean onGround, boolean isFlying, boolean isLook) {
        if (!isFlying && !isLook) data.clientPositionOnGround(onGround);

        final boolean currentOnGround = data.clientOnGround();
        data.wasClientOnGround(currentOnGround);

        if (!isFlying && (onGround != currentOnGround)) {
            final long delta = System.currentTimeMillis() - data.lastFlyingPacket();
            if (delta >= 300) data.clientOnGround(onGround);
        } else {
            data.clientOnGround(onGround);
        }
    }

}
