package arc.check;

import arc.check.combat.Criticals;
import arc.check.combat.NoSwing;
import arc.check.combat.Reach;
import arc.check.moving.Jesus;
import arc.check.moving.MorePackets;
import arc.check.moving.NoFall;
import arc.check.network.PayloadFrequency;
import arc.check.network.SwingFrequency;
import arc.check.player.FastUse;
import arc.check.player.Regeneration;
import arc.configuration.ArcConfiguration;
import arc.configuration.Reloadable;

import java.io.Closeable;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A check manager
 */
public final class CheckManager implements Closeable, Reloadable {

    /**
     * All the checks
     */
    private final Set<Check> checks = new HashSet<>();

    /**
     * Populate the check map.
     */
    public void initialize() {
        checks.add(new Criticals());
        checks.add(new MorePackets());
        checks.add(new NoFall());
        checks.add(new PayloadFrequency());
        checks.add(new SwingFrequency());
        checks.add(new Regeneration());
        checks.add(new FastUse());
        checks.add(new Jesus());
        checks.add(new Reach());
        checks.add(new NoSwing());
    }

    @Override
    public void reloadConfiguration(ArcConfiguration configuration) {
        checks.forEach(check -> check.reloadConfigInternal(configuration.fileConfiguration()));
    }

    /**
     * Get a check
     *
     * @param checkType the type
     * @return the check
     */
    public Check getCheck(CheckType checkType) {
        return checks.stream().filter(check -> check.type() == checkType).findAny().orElseThrow(NoSuchElementException::new);
    }

    /**
     * @return all checks
     */
    public Set<Check> getAllChecks() {
        return checks;
    }

    @Override
    public void close() {
        checks.forEach(Check::unload);
        checks.clear();
    }
}
