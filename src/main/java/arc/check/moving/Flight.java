package arc.check.moving;

import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import arc.utility.MovingUtil;
import arc.utility.block.Blocks;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Checks various vertical movement/flying stuff.
 */
public final class Flight extends Check {

    /**
     * The max jump distance.
     */
    private double maxJumpDistance;

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

        if (enabled()) load();
    }

    /**
     * Check the player
     *
     * @param player the player
     * @param data   their data
     */
    public void check(Player player, MovingData data) {
        if (data.onGround()) {
            resetDataGround(data);
            return;
        }

        final CheckResult result = new CheckResult();
        final double vertical = data.vertical();
        final Location to = data.to();

        // check if we have a slab.
        final boolean hasSlab = MovingUtil.hasBlock(to, 0.3, -0.1, 0.3, Blocks::isSlab);

        // check if its a valid vertical move.
        final boolean hasVerticalMove = vertical > 0.0
                && !player.isInsideVehicle()
                && !hasSlab
                && !data.inLiquid()
                && !data.hasClimbable();

        // check vertical distance moves,
        // basically anything over 0.42
        if (hasVerticalMove) checkVerticalDistance(player, data, to, vertical);

        player.sendMessage("Vertical: " + vertical);

    }

    /**
     * Check vertical distance
     *
     * @param player   the player
     * @param data     the data
     * @param to       the location moved to
     * @param vertical the vertical
     */
    private void checkVerticalDistance(Player player, MovingData data, Location to, double vertical) {
        if (data.ascending()) {
            // ensure we didn't walk up a block that modifies your vertical
            final boolean hasFence = MovingUtil.hasBlock(to, 0.5, -1, 0.5, block -> (Blocks.isFence(block) || Blocks.isFenceGate(block)));
            final boolean hasStair = MovingUtil.hasBlock(to, 0, -0.5, 0, Blocks::isStair);
        }
    }

    /**
     * Reset data
     *
     * @param data the data
     */
    private void resetDataGround(MovingData data) {

    }

    /**
     * Retrieve the jump height.
     * TODO: Only 1.8.8
     *
     * @param player the player
     * @return the jump height.
     */
    private double getJumpHeight(Player player) {
        double current = maxJumpDistance;
        if (player.hasPotionEffect(PotionEffectType.JUMP)) {
            current += 0.4;
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
    }
}
