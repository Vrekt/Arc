package arc.check.block.blockinteract;

import arc.check.block.AbstractBlockReachCheck;
import arc.check.types.CheckType;

/**
 * Check for interaction reach.
 */
public final class Reach extends AbstractBlockReachCheck {

    public Reach() {
        super(CheckType.BLOCK_INTERACT_REACH);
    }

    @Override
    protected void buildCheckConfiguration() {
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(4)
                .ban(false)
                .kick(false)
                .build();
    }

}