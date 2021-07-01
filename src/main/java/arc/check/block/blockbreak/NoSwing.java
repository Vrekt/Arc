package arc.check.block.blockbreak;

import arc.check.block.AbstractBlockNoSwingCheck;
import arc.check.types.CheckType;

/**
 * Checks for NoSwing when breaking blocks.
 */
public final class NoSwing extends AbstractBlockNoSwingCheck {
    public NoSwing() {
        super(CheckType.BLOCK_BREAK_NO_SWING);
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
