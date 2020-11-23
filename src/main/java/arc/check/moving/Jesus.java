package arc.check.moving;

import arc.check.Check;
import arc.check.CheckType;
import arc.check.result.CheckCallback;
import arc.check.result.CheckResult;
import arc.data.moving.MovingData;
import arc.utility.MovingUtil;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 * Checks if the player is walking on water.
 * TODO: Lag checking
 */
public final class Jesus extends Check {

    public Jesus() {
        super(CheckType.JESUS);
        enabled(true).
                cancel(true).
                cancelLevel(0).
                notify(true).
                notifyEvery(1).
                ban(false).
                kick(false).
                write();

        if (enabled()) load();
    }

    /**
     * Check this player
     *
     * @param player   the player
     * @param data     their data
     * @param callback the callback
     */
    public void check(Player player, MovingData data, CheckCallback callback) {
        if (exempt(player) || !enabled()) return;

        if (!data.onGround() && MovingUtil.isInOrOnLiquid(data.to())) {
            final var blockFaceDown = data.to().getBlock().getRelative(BlockFace.DOWN).isLiquid();
            final var blockFaceDown2 = data.to().getBlock().getRelative(0, -2, 0).isLiquid();
            final var result = new CheckResult();

            // we are on ground while on a liquid, check
            if (data.clientOnGround()) {
                if (data.vertical() == 0.0) {
                    // not possible, i don't think
                    result.setFailed("Client on ground and vertical 0.0 while in or on liquid.");
                }

                // on ground and at-least a decent amount of liquid below us.
                if (blockFaceDown || blockFaceDown2) {
                    result.setFailed("Client on ground while on layers of water.");
                }
            } else {
                // the client isn't on ground, check further
                final var blockFaceDown2Modifier = data.to().clone().add(0, -1.5, 0).getBlock().isLiquid();
                if (data.vertical() == 0.0 && (blockFaceDown || blockFaceDown2Modifier)) {
                    // no vertical but on layers of water.
                    result.setFailed("Client has no vertical while on layers of water.");
                }
            }
            callback.onResult(result(player, result));
        }
    }

    @Override
    public void reloadConfig() {
        // No reloading required.
    }

    @Override
    public void load() {
        // No loading required
    }
}
