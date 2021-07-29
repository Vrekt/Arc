package arc.check;

import arc.check.block.Nuker;
import arc.check.combat.Reach;
import arc.check.combat.Criticals;
import arc.check.combat.KillAura;
import arc.check.moving.*;
import arc.check.moving.Flight;
import arc.check.network.PayloadFrequency;
import arc.check.network.SwingFrequency;
import arc.check.player.FastUse;
import arc.check.player.Regeneration;
import arc.check.types.CheckType;
import arc.configuration.ArcConfiguration;
import arc.configuration.Configurable;

import java.io.Closeable;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A check manager
 */
public final class CheckManager implements Configurable, Closeable {

    /**
     * All the checks
     */
    private final Set<Check> checks = new HashSet<>();

    /**
     * Populate the check map.
     */
    public void initializeAllChecks() {
        add(new Criticals());
        add(new MorePackets());
        add(new NoFall());
        add(new PayloadFrequency());
        add(new SwingFrequency());
        add(new Regeneration());
        add(new FastUse());
        add(new Jesus());
        add(new Reach());
        add(new arc.check.combat.NoSwing());
        add(new KillAura());
        add(new Flight());
        add(new Speed());
        add(new arc.check.block.blockbreak.Reach());
        add(new arc.check.block.blockplace.Reach());
        add(new arc.check.block.blockinteract.Reach());
        add(new arc.check.block.blockbreak.NoSwing());
        add(new arc.check.block.blockplace.NoSwing());
        add(new arc.check.block.blockinteract.NoSwing());
        add(new Nuker());
    }

    /**
     * Add a check
     *
     * @param check the check
     */
    private void add(Check check) {
        checks.add(check);
    }

    /**
     * Get a check
     *
     * @param check the check
     * @param <T>   the type
     * @return the check
     */
    @SuppressWarnings("unchecked")
    public <T extends Check> T getCheck(CheckType check) {
        return (T) checks
                .stream()
                .filter(c -> c.type() == check)
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Could not find check " + check.getPrettyName()));
    }

    /**
     * @return all checks
     */
    public Set<Check> getAllChecks() {
        return checks;
    }

    @Override
    public void reloadConfiguration(ArcConfiguration configuration) {
        checks.forEach(check -> check.reloadConfiguration(configuration));
    }

    @Override
    public void close() {
        checks.forEach(Check::unload);
        checks.clear();
    }

}
