package arc.check.block.blockbreak;

import arc.check.block.AbstractBlockReachCheck;
import arc.check.types.CheckType;

/**
 * Checks for breaking reach
 */
public final class Reach extends AbstractBlockReachCheck {
    public Reach() {
        super(CheckType.BLOCK_BREAK_REACH);
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
