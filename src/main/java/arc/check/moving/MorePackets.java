package arc.check.moving;

import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import org.bukkit.Bukkit;
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
    private final int maxFlyingPackets, maxPositionPackets, maxPacketsToKick;

    public MorePackets() {
        super("MorePackets", CheckType.MORE_PACKETS);
        writeConfiguration(true, 0, true, 1, true, 20, false, 0);

        addConfigurationValue("max-flying-packets", 30);
        addConfigurationValue("max-position-packets", 30);
        addConfigurationValue("max-packets-kick", 50);
        maxFlyingPackets = getValueInt("max-flying-packets");
        maxPositionPackets = getValueInt("max-position-packets");
        maxPacketsToKick = getValueInt("max-packets-kick");

        scheduledCheck(() -> {
            for (var player : Bukkit.getOnlinePlayers()) {
                check(player, MovingData.get(player));
            }
        }, 20, 20);
    }

    /**
     * Check this player
     *
     * @param player the player
     * @param data   the data
     */
    public void check(Player player, MovingData data) {
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
