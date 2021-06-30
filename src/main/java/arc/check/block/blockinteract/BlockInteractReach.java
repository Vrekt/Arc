package arc.check.block.blockinteract;

import arc.check.block.AbstractBlockReachCheck;
import arc.check.types.CheckType;

/**
 * Check for interaction reach.
 */
public final class BlockInteractReach extends AbstractBlockReachCheck {

    public BlockInteractReach() {
        super(CheckType.BLOCK_INTERACT_REACH);
    }

    @Override
    protected void buildCheckConfiguration() {
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .build();
    }

}