package arc.check.block;

import arc.check.Check;
import arc.check.result.CheckResult;
import arc.check.types.CheckType;
import arc.utility.math.MathUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class AbstractBlockReachCheck extends Check {

    /**
     * Distances
     */
    private double survivalDistance, creativeDistance;

    public AbstractBlockReachCheck(CheckType checkType) {
        super(checkType);

        buildCheckConfiguration();
        addConfigurationValue("survival-distance", 5.2);
        addConfigurationValue("creative-distance", 6.0);

        if (enabled()) load();
    }

    /**
     * Build the check configuration for whatever check this is.
     */
    protected abstract void buildCheckConfiguration();

    /**
     * Check.
     *
     * @param player the player
     * @param origin the players location
     * @param block  the block or interaction point.
     * @return the result.
     */
    public boolean check(Player player, Location origin, Location block) {
        if (!enabled() || exempt(player)) return false;

        final double y = origin.getY() + player.getEyeHeight();
        final double distance = MathUtil.distance(origin.getX(), y, origin.getZ(), block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5);

        // check distance based on game-mode.
        final double maxDistance = player.getGameMode() == GameMode.CREATIVE ? creativeDistance : survivalDistance;
        if (distance > maxDistance) {
            // flag.
            final CheckResult result = new CheckResult();
            result.setFailed("Attempted to break a block too far away.")
                    .withParameter("gameMode", player.getGameMode())
                    .withParameter("distance", distance)
                    .withParameter("max", maxDistance);
            return checkViolation(player, result);
        }

        return false;
    }

    @Override
    public void reloadConfig() {
        load();
    }

    @Override
    public void load() {
        survivalDistance = configuration.getDouble("survival-distance");
        creativeDistance = configuration.getDouble("creative-distance");
    }
}
