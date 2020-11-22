package arc.check;

import arc.check.combat.Criticals;
import arc.check.moving.MorePackets;
import arc.check.moving.NoFall;
import arc.check.network.PayloadFrequency;
import arc.check.network.SwingFrequency;
import arc.check.player.Regeneration;
import org.bukkit.configuration.file.FileConfiguration;

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
     * Reload check configurations
     *
     * @param configuration the configuration
     */
    public void reloadConfigurations(FileConfiguration configuration) {
        checks.forEach(check -> check.reloadConfigInternal(configuration));
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

    /**
     * @return all checks
     */
    public Set<Check> getAllChecks() {
        return checks;
    }
}
