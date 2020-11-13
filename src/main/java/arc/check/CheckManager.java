package arc.check;

import arc.check.moving.MorePackets;
import arc.check.moving.NoFall;
import arc.check.player.BadEffects;
import arc.check.player.PayloadFrequency;
import arc.check.player.SwingFrequency;

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

    /**
     * Swing frequency check
     */
    private final SwingFrequency swingFrequency;

    /**
     * Payload frequency check
     */
    private final PayloadFrequency payloadFrequency;

    public CheckManager() {
        noFall = new NoFall();
        morePackets = new MorePackets();
        badEffects = new BadEffects();
        swingFrequency = new SwingFrequency();
        payloadFrequency = new PayloadFrequency();
    }

    /**
     * @return nofall check
     */
    public NoFall noFall() {
        return noFall;
    }

}
