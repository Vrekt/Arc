package arc.check;

import arc.check.combat.Criticals;
import arc.check.moving.MorePackets;
import arc.check.moving.NoFall;
import arc.check.network.PayloadFrequency;
import arc.check.network.SwingFrequency;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A check manager
 */
public final class CheckManager {

    /**
     * Combat checks.
     */
    private final Map<String, Check> combatChecks = new HashMap<>();

    /**
     * Movement checks.
     */
    private final Map<String, Check> movingChecks = new HashMap<>();

    /**
     * Network checks.
     */
    private final Map<String, Check> networkChecks = new HashMap<>();

    /**
     * Player checks
     */
    private final Map<String, Check> playerChecks = new HashMap<>();


    public CheckManager() {
        combatChecks.put("Criticals", new Criticals());
        movingChecks.put("MorePackets", new MorePackets());
        movingChecks.put("NoFall", new NoFall());
        networkChecks.put("PayloadFrequency", new PayloadFrequency());
        networkChecks.put("SwingFrequency", new SwingFrequency());
    }

    /**
     * Get checks within the provided category.
     *
     * @param category the category.
     * @return the collection
     */
    public Collection<Check> withinCategory(CheckCategory category) {
        switch (category) {
            case COMBAT:
                return combatChecks.values();
            case MOVING:
                return movingChecks.values();
            case NETWORK:
                return networkChecks.values();
            case PLAYER:
                return playerChecks.values();
        }
        return Collections.emptyList();
    }

    /**
     * Get a moving check.
     *
     * @param name the name
     * @return the check
     */
    public Check getMovingCheck(String name) {
        return movingChecks.get(name);
    }

}
