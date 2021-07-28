package arc.utility;

import arc.data.moving.MovingData;
import arc.utility.block.BlockAccess;
import arc.utility.math.MathUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;

/**
 * Utility for calculating various movement related things.
 */
public final class MovingAccess {

    /**
     * Check if the location is on a solid block
     * 0.5, 0.3, 0.1
     *
     * @param location the location
     * @return {@code true} if so
     */
    public static boolean onGround(Location location) {
        final Block block = location.subtract(0, 0.5, 0).getBlock();
        location.add(0, 0.5, 0);

        return BlockAccess.isConsideredGround(block) ||
                BlockAccess.hasSolidGroundAt(location, location.getWorld(), 0.3, -0.1, 0.3);
    }

    /**
     * Check if the player has a climbable block at this location
     *
     * @param location the location
     * @return {@code true} if so
     */
    public static boolean hasClimbable(Location location) {
        return BlockAccess.isClimbable(location.getBlock()) ||
                BlockAccess.hasClimableAt(location, location.getWorld(), 0.1, -0.06, 0.1);
    }

    /**
     * @return if we are in or on liquid.
     */
    public static boolean isInOrOnLiquid(Location location) {
        return BlockAccess.isLiquid(location.getBlock()) ||
                BlockAccess.isLiquid(location.getBlock().getRelative(BlockFace.DOWN)) ||
                BlockAccess.hasLiquidAt(location, location.getWorld(), 0.3, -0.1, 0.3);
    }

    /**
     * @param location the location
     * @return {@code true} if the location is on ice
     */
    public static boolean isOnIce(Location location) {
        return BlockAccess.isIce(location.getBlock()) ||
                BlockAccess.isIce(location.getBlock().getRelative(BlockFace.DOWN)) ||
                BlockAccess.hasIceAt(location, location.getWorld(), 0.1, -0.01, 0.1);
    }

    /**
     * Check if a player is on a boat.
     *
     * @param player the player
     * @return {@code true} if so
     */
    public static boolean isOnBoat(Player player) {
        return player.getNearbyEntities(0.1, 0.1, 0.1)
                .stream()
                .anyMatch(entity -> entity instanceof Boat && entity.getLocation().getY() < player.getLocation().getY());
    }

    /**
     * Update moving data for the provided data-set.
     *
     * @param data their data
     * @param from from
     * @param to   to
     */
    public static void updatePlayerMovingData(MovingData data, Location from, Location to) {
        final long now = System.currentTimeMillis();
        // prevent cloning multiple times to save performance
        final Location cloneFrom = from.clone();
        final Location cloneTo = to.clone();

        data.from(cloneFrom);
        data.to(cloneTo);

        // calculate ground
        final boolean currentOnGround = data.onGround();
        final boolean previousOnGround = data.wasOnGround();
        final boolean onGround = MovingAccess.onGround(cloneTo);

        data.onGround(onGround);
        data.wasOnGround(!onGround || (currentOnGround && previousOnGround));

        // calculate ground stuff.
        if (onGround) {
            // set initial safe location if null.
            if (data.getSafeLocation() == null) {
                data.setSafeLocation(cloneTo);
            }

            data.ground(cloneTo);
            // make sure ladder is reset once we touch ground again.
            data.ladderLocation(null);
            data.incrementOnGroundTime();

            // slime block checking
            final boolean hasSlimeblock = BlockAccess.isSlimeblock(to.getBlock().getRelative(BlockFace.DOWN));
            data.hasSlimeblock(hasSlimeblock);

            final boolean isOnIce = MovingAccess.isOnIce(cloneTo);
            final boolean wasOnIce = MovingAccess.isOnIce(cloneFrom);

            data.onIce(isOnIce);

            if (isOnIce) {
                data.incrementOnIceTime();
                data.offIceTime(0);
            } else {
                data.onIceTime(0);
                if (!wasOnIce) data.incrementOffIceTime();
            }

            data.setInAirTime(0);
        } else {
            data.onGroundTime(0);
            data.setInAirTime(data.getInAirTime() + 1);

            // extra modifier
            final boolean hasSlimeblock = BlockAccess.isSlimeblock(to.getBlock().getRelative(0, -2, 0));
            data.hasSlimeblock(hasSlimeblock);

            data.onIce(false);

            if (data.getSafeLocation() == null) data.setSafeLocation(from);
        }

        // calculate sprinting and sneaking times
        final boolean sprinting = data.sprinting();
        final boolean sneaking = data.sneaking();
        if (sprinting) {
            data.incrementSprintTime();
        } else {
            data.sprintTime(0);
        }

        if (sneaking) {
            data.incrementSneakTime();
        } else {
            data.sneakTime(0);
        }

        // distance moved vertically.
        final double distance = MathUtil.vertical(cloneFrom, cloneTo);
        data.lastVertical(data.vertical());
        data.vertical(distance);

        // calculate ascending/descending
        final boolean ascending = distance > 0.0 && cloneTo.getY() > cloneFrom.getY();
        final boolean descending = distance > 0.0 && cloneTo.getY() < cloneFrom.getY();

        data.ascending(ascending);
        data.descending(descending);
        if (ascending) {
            data.incrementAscendingTime();
        } else {
            data.ascendingTime(0);
        }

        if (descending) {
            data.incrementDescendingTime();
        } else {
            data.descendingTime(0);
        }

        // calculate climbing
        final boolean hasClimbable = MovingAccess.hasClimbable(cloneTo);
        final boolean hadClimbable = MovingAccess.hasClimbable(cloneFrom);
        final boolean climbing = (hasClimbable || hadClimbable) && (ascending || descending);
        data.hasClimbable(hasClimbable);
        data.hadClimbable(hadClimbable);
        data.climbing(climbing);

        if (data.climbing()) {
            data.ladderLocation(to);
        }

        data.climbTime(hasClimbable ? data.climbTime() + 1 : 0);

        // calculate liquids
        final boolean inLiquid = MovingAccess.isInOrOnLiquid(cloneTo);
        data.inLiquid(inLiquid);
        data.lastMovingUpdate(now);
    }

}
