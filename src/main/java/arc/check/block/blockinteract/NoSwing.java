package arc.check.block.blockinteract;

import arc.check.block.AbstractBlockNoSwingCheck;
import arc.check.types.CheckType;

/**
 * Block interact no swing
 */
public final class NoSwing extends AbstractBlockNoSwingCheck {

    public NoSwing() {
        super(CheckType.BLOCK_INTERACT_NO_SWING);
    }

    @Override
    protected void buildCheckConfiguration() {
        isEnabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(4)
                .ban(false)
                .kick(false)
                .build();
    }
}
