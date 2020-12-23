package arc.check.moving;

import arc.check.Check;
import arc.check.CheckType;

/**
 * Checks if the player is moving too fast.
 */
public final class Speed extends Check {

    /**
     * If large distances should be cancelled.
     */
    private boolean cancelLargeMovements;

    /**
     * Large movements distance to cancel
     * Ice slipperiness
     */
    private double largeMovementsDistance, iceSlipperiness;

    /**
     * The default move speed sprinting
     * The default move speed walking
     * The default move speed sneaking
     */
    private double baseMoveSpeedSprinting, baseMoveSpeedWalking, baseMoveSpeedSneaking;

    /**
     * The teleport cooldown time.
     */
    private long teleportCooldownMs;

    /**
     * The time to wait before checking sneak
     */
    private int sneakTimeDelayIce, sneakTimeDelay;

    public Speed() {
        super(CheckType.SPEED);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();

        addConfigurationValue("base-move-speed-sprinting", 0.2873);
        addConfigurationValue("base-move-speed-walking", 0.2166);
        addConfigurationValue("base-move-speed-sneaking", 0.0666);
        addConfigurationValue("cancel-large-movements", true);
        addConfigurationValue("large-movements", 3);
        addConfigurationValue("teleport-cooldown-ms", 500);
        addConfigurationValue("ice-slipperiness", 0.98);
        addConfigurationValue("sneak-time-delay-ice", 20);
        addConfigurationValue("sneak-time-delay", 15);

        if (enabled()) load();
    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public void load() {

    }
}
