package arc.check.block.blockplace;

import arc.check.block.AbstractBlockReachCheck;
import arc.check.types.CheckType;

/**
 * Checks for block place reach.
 */
public final class Reach extends AbstractBlockReachCheck {

    public Reach() {
        super(CheckType.BLOCK_PLACE_REACH);
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