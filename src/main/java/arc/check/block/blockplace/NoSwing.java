package arc.check.block.blockplace;

import arc.check.block.AbstractBlockNoSwingCheck;
import arc.check.types.CheckType;

/**
 * Block place NoSwing
 */
public final class NoSwing extends AbstractBlockNoSwingCheck {

    public NoSwing() {
        super(CheckType.BLOCK_PLACE_NO_SWING);
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
