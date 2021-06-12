package arc.listener.moving;

import arc.Arc;
import arc.check.CheckType;
import arc.check.moving.*;
import arc.data.moving.MovingData;
import arc.listener.AbstractPacketListener;
import arc.permissions.Permissions;
import arc.utility.MovingUtil;
import com.comphenix.packetwrapper.*;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;

/**
 * Listens for player movement packets.
 */
public final class MovingPacketListener extends AbstractPacketListener {

    /**
     * Delta
     */
    private static final double DELTA = 1f / 384;

    /**
     * The flight check
     */
    private final Flight flight;

    /**
     * The jesus check
     */
    private final Jesus jesus;

    /**
     * The MorePackets check
     */
    private final MorePackets morePackets;

    /**
     * The NoFall check
     */
    private final NoFall noFall;

    /**
     * The speed check
     */
    private final Speed speed;

    public MovingPacketListener() {
        flight = (Flight) Arc.arc().checks().getCheck(CheckType.FLIGHT);
        jesus = (Jesus) Arc.arc().checks().getCheck(CheckType.JESUS);
        morePackets = (MorePackets) Arc.arc().checks().getCheck(CheckType.MORE_PACKETS);
        noFall = (NoFall) Arc.arc().checks().getCheck(CheckType.NOFALL);
        speed = (Speed) Arc.arc().checks().getCheck(CheckType.SPEED);
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

        updateMovement(player, data, player.getLocation(), packet.getX(), packet.getY(), packet.getZ());
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

        updateMovement(player, data, player.getLocation(), packet.getX(), packet.getY(), packet.getZ());
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
     * Check if the player has moved at all.
     *
     * @param location the player location
     * @param newX     new X
     * @param newY     new Y
     * @param newZ     new Z
     * @return {@code true} if so
     */
    private boolean hasMoved(Location location, double newX, double newY, double newZ) {
        return location.getX() != newX || location.getY() != newY || location.getZ() != newZ;
    }

    /**
     * Update player movement
     *
     * @param player   the player
     * @param data     their data
     * @param location the location
     * @param newX     new X
     * @param newY     new Y
     * @param newZ     new Z
     */
    private void updateMovement(Player player, MovingData data, Location location, double newX, double newY, double newZ) {
        if (!NumberConversions.isFinite(newX) || !NumberConversions.isFinite(newY) || !NumberConversions.isFinite(newZ) || (Permissions.canBypassChecks(player)))
            return;

        if (hasMoved(location, newX, newY, newZ)) {
            // we have moved, check delta first.
            final double delta = Math.pow(location.getX() - newX, 2) + Math.pow(location.getY() - newY, 2) + Math.pow(location.getZ() - newZ, 2);
            if (delta > DELTA) { // 0.002 ish, 1/ 256 was too high of a number
                // retrieve from and to locations
                final Location from = data.to() != null ? data.to().clone() : new Location(player.getWorld(), newX, newY, newZ);
                final Location to = new Location(player.getWorld(), newX, newY, newZ);
                // calculate if this move was from one block to another
                final boolean wasBlockMovement = from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ();

                // calc player movement
                MovingUtil.calculateMovement(data, from, to);

                // run checks
                runChecks(player, data);

                if (wasBlockMovement) runBlockChecks(player, data);
            }
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

    /**
     * Run movement related checks
     *
     * @param player the player
     * @param data   their data
     */
    private void runChecks(Player player, MovingData data) {
        if (flight.enabled()) {
            flight.check(player, data);
        }

        if (noFall.enabled()) {
            noFall.check(player, data);
        }
    }

    /**
     * Run movement - but block restricted checks
     *
     * @param player the player
     * @param data   their data
     */
    private void runBlockChecks(Player player, MovingData data) {
        if (jesus.enabled()) jesus.check(player, data);
    }

}
