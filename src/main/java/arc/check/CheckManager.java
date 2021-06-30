package arc.check;

import arc.check.block.blockbreak.BlockBreakReach;
import arc.check.block.blockinteract.BlockInteractReach;
import arc.check.block.blockplace.BlockPlaceReach;
import arc.check.combat.Criticals;
import arc.check.combat.KillAura;
import arc.check.combat.NoSwing;
import arc.check.combat.AttackReach;
import arc.check.moving.*;
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
public final class CheckManager extends Configurable implements Closeable {

    /**
     * All the checks
     */
    private final Set<Check> checks = new HashSet<>();

    /**
     * Populate the check map.
     */
    public void initialize() {
        add(new Criticals());
        add(new MorePackets());
        add(new NoFall());
        add(new PayloadFrequency());
        add(new SwingFrequency());
        add(new Regeneration());
        add(new FastUse());
        add(new Jesus());
        add(new AttackReach());
        add(new NoSwing());
        add(new KillAura());
        add(new Flight());
        add(new Speed());
        add(new BlockBreakReach());
        add(new BlockPlaceReach());
        add(new BlockInteractReach());
    }

    /**
     * Add a check
     *
     * @param check the check
     */
    private void add(Check check) {
        checks.add(check);
    }

    @Override
    public void reload(ArcConfiguration configuration) {
        checks.forEach(check -> check.reload(configuration));
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
                .orElseThrow(() -> new NoSuchElementException("Could not find check " + check.getName()));
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
