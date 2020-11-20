package arc.check;

import arc.check.combat.Criticals;
import arc.check.moving.MorePackets;
import arc.check.moving.NoFall;
import arc.check.network.PayloadFrequency;
import arc.check.network.SwingFrequency;
import arc.check.player.Regeneration;

import java.util.*;

/**
 * A check manager
 */
public final class CheckManager {

    /**
     * All the checks
     */
    private final Set<Check> checks = new HashSet<>();

    public CheckManager() {
        checks.add(new Criticals());
        checks.add(new MorePackets());
        checks.add(new NoFall());
        checks.add(new PayloadFrequency());
        checks.add(new SwingFrequency());
        checks.add(new Regeneration());
    }

    /**
     * Get a check
     *
     * @param checkType the type
     * @return the check
     */
    public Check getCheck(CheckType checkType) {
        return checks.stream().filter(check -> check.type() == checkType).findAny().orElseThrow();
    }

}
