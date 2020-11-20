package arc.check.moving;

import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import arc.data.moving.nf.NoFallData;
import arc.utility.MathUtil;
import org.bukkit.entity.Player;

/**
 * Checks if the player is taking no fall damage.
 * <p>
 * This check is not entirely perfect.
 * In some cases certain NoFalls will bypass just because of how Minecraft works and insufficient data at the checking stage.
 * <p>
 * TODO: Expected distance checking regardless of client ground state?
 */
public final class NoFall extends Check {

    /**
     * The tolerance amount to allow when fall distance is greater than calculated vertical distance
     */
    private final double tolerance;

    public NoFall() {
        super(CheckType.NOFALL);
        enabled(true).
                cancel(true).
                cancelLevel(0).
                notify(true).
                notifyEvery(1).
                ban(false).
                kick(false).
                write();

        addConfigurationValue("tolerance", 0.1);
        tolerance = getValueDouble("tolerance");
    }

    /**
     * Check the player for NoFall
     *
     * @param player the player
     * @param data   the data
     */
    public void check(Player player, MovingData data) {
        if (exempt(player) || !enabled()) return;

        final var result = new CheckResult();
        final var nf = data.nf();
        cancelIf(player, nf, data);

        // Checks if the client is saying we are not on the ground
        // TODO: Constantly flagged if cheat is left on.
        // TODO: Can probably be improved but will take some work and brain
        if (data.onGround() && data.onGroundTime() >= 5) {
            // client still isn't on the ground, we still have a fall distance and we were descending
            if (!data.clientOnGround()
                    && player.getFallDistance() > 3.0 &&
                    (System.currentTimeMillis() - nf.lastCheck()) <= 60000 && !nf.hasFailed()) {
                result.setFailed("client faked onGround state, fDist=" + player.getFallDistance());
                nf.hasFailed(true);

                nf.location(nf.lastCheckLocation());
                nf.lastCheckLocation(null);
                nf.lastCheck(System.currentTimeMillis());
            } else {
                // reset
                nf.lastCheck(System.currentTimeMillis());
                nf.lastCheckLocation(null);
                nf.location(null);
            }
        }

        if (data.descending() && !data.onGround() && !data.climbing()) {
            // if we have no ground and haven't been descending for that long just return
            if (nf.location() == null) nf.location(data.from());
            // set our first check location, if we haven't already.
            // if we don't have ground we want to set it to the previous move.
            // calculate the distance we have fallen
            final var distance = Math.floor(MathUtil.distance(nf.location(), data.from()) * 100) / 100;
            // make sure we have fallen a bit before checking
            if (distance > 1) {
                nf.lastCheck(System.currentTimeMillis());
                if (nf.lastCheckLocation() == null) nf.lastCheckLocation(data.from());
                final var clientHasGround = data.clientOnGround();

                // Fixes regular NoFall and "Packet" types
                if (clientHasGround && player.getFallDistance() == 0.0) {
                    result.setFailed("client faked onGround state, fDist=" + player.getFallDistance());
                    nf.hasFailed(true);
                } else if (!clientHasGround && player.getFallDistance() == 0.0) {
                    result.setFailed("client faked fall distance, fDist=" + player.getFallDistance());
                    nf.hasFailed(true);
                }

                // Fixes other NoFall types where fall distance is accumulated but client is never on the ground
                if (!clientHasGround) {
                    final var difference = player.getFallDistance() - distance;
                    // we have too much off a difference between expected fall distance + player fall dist
                    if (difference > tolerance) {
                        result.setFailed("fall distance not expected, fDist=" + player.getFallDistance() + ", e=" + distance + ", t=" + tolerance);
                        nf.hasFailed(true);
                    }
                }
            }
        }
        resultIgnore(player, result);
    }

    /**
     * Cancel if necessary
     *
     * @param player the player
     * @param nf     nf data
     * @param data   moving data
     */
    private void cancelIf(Player player, NoFallData nf, MovingData data) {
        if (nf.hasFailed() && data.onGround()) {
            // if we have failed previously, we need to cancel.
            // calculate the distance fallen, and damage the player.
            if (nf.location() == null) {
                nf.reset();
                return;
            }
            final var distance = MathUtil.distance(nf.location(), data.from()) - 3.0;
            player.damage(distance);
            // reset and return
            nf.reset();
        } else if (!nf.hasFailed() && data.onGround()) {
            nf.reset();
        }
    }

}
