package arc.check.moving;

import arc.Arc;
import arc.check.Check;
import arc.check.types.CheckType;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Ensures too many packets aren't being sent every second
 */
public final class MorePackets extends Check {

    /**
     * The max flying packets allowed per second.
     * The max position packets allowed per second
     * The max look packets allowed per second
     * The max threshold allowed before kicking
     */
    private int maxFlyingPacketsPerSecond, maxPositionPacketsPerSecond, maxLookPacketsPerSecond, packetKickThreshold;
    /**
     * If this check should kick for reaching the threshold
     */
    private boolean kickIfThresholdReached;

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

        addConfigurationValue("max-flying-packets-per-second", 25);
        addConfigurationValue("max-position-packets-per-second", 25);
        addConfigurationValue("max-look-packets-per-second", 25);
        addConfigurationValue("kick-if-threshold-reached", true);
        addConfigurationValue("packet-kick-threshold", 50);

        if (enabled()) load();
    }

    /**
     * Check this player
     *
     * @param player the player
     * @param data   their data
     */
    private void check(Player player, MovingData data) {
        if (exempt(player)) return;

        // position+look are combined, look packets
        final int flyingCount = data.flyingPackets();
        final int positionCount = data.positionPackets();
        final int positionLookCount = data.positionLookPackets();
        final int lookCount = data.lookPackets();
        final CheckResult result = new CheckResult();

        // check flying counts
        if (flyingCount > maxFlyingPacketsPerSecond) {
            populateResult(result, "Too many flying packets per second", flyingCount, maxFlyingPacketsPerSecond);
            data.cancelFlying(checkViolation(player, result));
            kickPlayerIfThresholdReached(player, flyingCount);
        } else {
            data.cancelFlying(false);
        }

        // check position and position_look counts
        if (positionCount > maxPositionPacketsPerSecond) {
            populateResult(result, "Too many position packets per second", positionCount, maxPositionPacketsPerSecond);
            data.cancelPosition(checkViolation(player, result));
            kickPlayerIfThresholdReached(player, positionCount);
        } else if (positionLookCount > maxPositionPacketsPerSecond) {
            populateResult(result, "Too many position look packets per second", positionLookCount, maxPositionPacketsPerSecond);
            data.cancelPosition(checkViolation(player, result));
            kickPlayerIfThresholdReached(player, positionLookCount);
        } else {
            data.cancelPosition(false);
        }

        // check look counts
        if (lookCount > maxLookPacketsPerSecond) {
            populateResult(result, "Too many look packets per second", lookCount, maxLookPacketsPerSecond);
            data.cancelLook(checkViolation(player, result));
            kickPlayerIfThresholdReached(player, lookCount);
        } else {
            data.cancelLook(false);
        }

        data.flyingPackets(0);
        data.positionPackets(0);
        data.positionLookPackets(0);
        data.lookPackets(0);
    }

    /**
     * Populate the check result with information
     *
     * @param result      the result
     * @param information the information
     * @param count       the count
     * @param max         the max
     */
    private void populateResult(CheckResult result, String information, int count, int max) {
        result.setFailed(information)
                .withParameter("count", count)
                .withParameter("max", max);
    }

    /**
     * Kick the player if the threshold is reached
     *
     * @param player the player
     * @param count  the count
     */
    private void kickPlayerIfThresholdReached(Player player, int count) {
        if (kickIfThresholdReached && count >= packetKickThreshold
                && !Arc.getInstance().getPunishmentManager().hasPendingKick(player)) {
            Arc.getInstance().getPunishmentManager().kickPlayer(player, this);
        }
    }

    /**
     * Check if the sent packet should be cancelled.
     *
     * @param data       their data
     * @param isFlying   if the packet is flying
     * @param isPosition if the packet is position
     * @param isLook     if the packet is look
     * @return {@code true} if the packet should be cancelled.
     */
    public boolean cancelPacket(MovingData data, boolean isFlying, boolean isPosition, boolean isLook) {
        return isFlying ? data.cancelFlying() : isPosition ? data.cancelPosition() : isLook && data.cancelLook();
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void unload() {
        cancelScheduled();
    }

    @Override
    public void load() {
        maxFlyingPacketsPerSecond = configuration.getInt("max-flying-packets-per-second");
        maxPositionPacketsPerSecond = configuration.getInt("max-position-packets-per-second");
        maxLookPacketsPerSecond = configuration.getInt("max-look-packets-per-second");
        kickIfThresholdReached = configuration.getBoolean("kick-if-threshold-reached");
        packetKickThreshold = configuration.getInt("packet-kick-threshold");

        schedule(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                check(player, MovingData.get(player));
            }
        }, 0, 20);
    }
}
