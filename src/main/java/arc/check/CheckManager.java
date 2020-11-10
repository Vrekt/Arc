package arc.check;

import arc.check.moving.MorePackets;
import arc.check.moving.NoFall;
import arc.check.player.BadEffects;

/**
 * A check manager
 */
public final class CheckManager {

    /**
     * The NoFall check
     */
    private final NoFall noFall;
    /**
     * The more packets check
     */
    private final MorePackets morePackets;

    /**
     * Bad effects check
     */
    private final BadEffects badEffects;

    public CheckManager() {
        noFall = new NoFall();
        morePackets = new MorePackets();
        badEffects = new BadEffects();
    }

    /**
     * @return nofall check
     */
    public NoFall noFall() {
        return noFall;
    }

    /**
     * @return more packets check
     */
    public MorePackets morePackets() {
        return morePackets;
    }
}
