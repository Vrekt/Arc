package arc.check.block.blockbreak;

import arc.check.block.AbstractBlockReachCheck;
import arc.check.types.CheckType;

/**
 * Checks for block breaking reach.
 */
public final class BlockBreakReach extends AbstractBlockReachCheck {

    public BlockBreakReach() {
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
