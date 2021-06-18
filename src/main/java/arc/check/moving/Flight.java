package arc.check.moving;

import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CancelType;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import arc.utility.MovingUtil;
import arc.utility.api.BukkitApi;
import arc.utility.block.Blocks;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffectType;

/**
 * Checks various vertical movement/flying stuff.
 * TODO: calculate ground distance
 * TODO: Flag when falling onto ladder.
 */
public final class Flight extends Check {

    /**
     * The max jump distance.
     * Max climbing speeds
     * The amount of time a player has to be on a climbable
     */
    private double maxJumpDistance, maxClimbSpeedUp, maxClimbSpeedDown, climbingCooldown;

    /**
     * The max ascend time
     * The amount to add to {@code maxAscendTime} when the player has jump boost.
     */
    private int maxAscendTime, jumpBoostAscendAmplifier;

    public Flight() {
        super(CheckType.FLIGHT);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("max-jump-distance", 0.42);
        addConfigurationValue("max-climbing-speed-up", 0.12);
        addConfigurationValue("max-climbing-speed-down", 0.151);
        addConfigurationValue("climbing-cooldown", 5);
        addConfigurationValue("max-ascend-time", 7);
        addConfigurationValue("jump-boost-ascend-amplifier", 3);

        if (enabled()) load();
    }

    /**
     * Check the player
     *
     * @param player the player
     * @param data   their data
     */
    public void check(Player player, MovingData data) {
        if (!enabled() || exempt(player)) return;

        final CheckResult result = new CheckResult();
        final double vertical = data.vertical();
        final Location to = data.to();

        // check if we have a slab.
        final boolean hasSlab = MovingUtil.hasBlock(to, 0.3, -0.1, 0.3, Blocks::isSlab);
        final boolean hasStair = MovingUtil.hasBlock(to, 0, -0.5, 0, Blocks::isStair);
        final boolean hasFence = MovingUtil.hasBlock(to, 0.5, -1, 0.5, block -> (Blocks.isFence(block) || Blocks.isFenceGate(block)));

        // check if its a valid vertical move.
        final boolean hasVerticalMove = vertical > 0.0
                && !player.isInsideVehicle()
                && !hasSlab
                && !hasStair
                && !hasFence
                && !data.inLiquid()
                && !data.hasClimbable();

        // check vertical distance moves,
        // basically anything over 0.42
        if (hasVerticalMove) {
            if (checkVerticalMove(player, data, vertical, result)) handleCancel(player, data, result);
        }

        result.reset();
        if (data.hasClimbable()) {
            // wait for cooldown.
            // this helps in situations, for example when a player jumps onto a ladder.
            data.climbTime(data.climbTime() + 1);

            final double cooldown = climbingCooldown - (vertical * 2);
            if (data.climbTime() >= cooldown) {
                if (checkClimbingMovement(player, data, vertical, cooldown, result))
                    handleCancel(player, data, result);
            }
        } else {
            data.climbTime(0);
        }

        result.reset();
        // player.sendMessage("Vertical: " + vertical);
    }

    /**
     * Check vertical moves
     *
     * @param player   the player
     * @param data     the data
     * @param vertical the vertical
     * @param result   the result
     */
    private boolean checkVerticalMove(Player player, MovingData data, double vertical, CheckResult result) {
        if (data.ascending()) {
            // ensure we didn't walk up a block that modifies your vertical
            final double maxJumpHeight = getJumpHeight(player);

            if (vertical > maxJumpHeight) {
                result.setFailed("Vertical move greater than max jump height.");
                result.parameter("vertical", vertical);
                result.parameter("max", maxJumpHeight);
                return checkViolation(player, result, data.from(), CancelType.FROM).cancel();
            }

            final int modifier = player.hasPotionEffect(PotionEffectType.JUMP)
                    ? BukkitApi.getPotionEffect(player, PotionEffectType.JUMP).getAmplifier()
                    + jumpBoostAscendAmplifier : 0;
            if (data.ascendingTime() > (maxAscendTime + modifier)) {
                result.setFailed("Ascending for too long");
                result.parameter("vertical", vertical);
                result.parameter("time", data.ascendingTime());
                result.parameter("max", (maxAscendTime + modifier));
                return checkViolation(player, result, data.ground(), CancelType.GROUND).cancel();
            }

        }
        return false;
    }

    /**
     * Check climbing movement.
     *
     * @param player   the player
     * @param data     the data
     * @param vertical the vertical
     * @param result   the result
     */
    private boolean checkClimbingMovement(Player player, MovingData data, double vertical, double cooldown, CheckResult result) {
        if (data.ascending() && data.ascendingTime() >= cooldown) {
            if (vertical > maxClimbSpeedUp) {
                result.setFailed("Climbing a ladder too fast");
                result.parameter("vertical", vertical);
                result.parameter("max", maxClimbSpeedUp);
                result.parameter("cooldown", cooldown);
                return checkViolation(player, result, data.from(), CancelType.FROM).cancel();
            }
        } else if (data.descending() && data.descendingTime() >= cooldown) {
            if (vertical > maxClimbSpeedDown) {
                result.setFailed("Descending a ladder too fast");
                result.parameter("vertical", vertical);
                result.parameter("max", maxClimbSpeedDown);
                result.parameter("cooldown", cooldown);
                return checkViolation(player, result, data.from(), CancelType.FROM).cancel();
            }
        }
        return false;
    }

    /**
     * Handle cancel
     *
     * @param player the player
     * @param result the result
     */
    private void handleCancel(Player player, MovingData data, CheckResult result) {
        switch (result.cancelType()) {
            case FROM:
                player.teleport(data.from(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                break;
            case GROUND:
                player.teleport(result.cancel(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                break;
        }
    }

    /**
     * Retrieve the jump height.
     *
     * @param player the player
     * @return the jump height.
     */
    private double getJumpHeight(Player player) {
        double current = maxJumpDistance;
        if (player.hasPotionEffect(PotionEffectType.JUMP)) {
            current += (0.4 * BukkitApi.getPotionEffect(player, PotionEffectType.JUMP).getAmplifier());
        }
        return current;
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        maxJumpDistance = configuration.getDouble("max-jump-distance");
        maxClimbSpeedUp = configuration.getDouble("max-climbing-speed-up");
        maxClimbSpeedDown = configuration.getDouble("max-climbing-speed-down");
        climbingCooldown = configuration.getDouble("climbing-cooldown");
        maxAscendTime = configuration.getInt("max-ascend-time");
        jumpBoostAscendAmplifier = configuration.getInt("jump-boost-ascend-amplifier");
    }
}
