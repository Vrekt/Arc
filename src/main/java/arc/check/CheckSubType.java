package arc.check;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents checks that have multiple checks.
 * For example FastUse; FastBow and FastConsume
 * KillAura; Direction, Angle, etc.
 * etc.
 */
public enum CheckSubType {

    /**
     * Kill aura direction
     */
    KILL_AURA_DIRECTION(CheckType.KILL_AURA, "direction"),

    /**
     * FastUse fastbow
     */
    FAST_USE_FAST_BOW(CheckType.FAST_USE, "fastbow"),

    /**
     * FastUse FastConsume
     */
    FAST_USE_FAST_CONSUME(CheckType.FAST_USE, "fastconsume");

    /**
     * Values
     */
    private static final List<CheckSubType> VALUES = Arrays.asList(values());

    /**
     * The super type
     */
    private final CheckType from;

    /**
     * The name
     */
    private final String name;

    /**
     * Initialize the sub-type
     *
     * @param from the super type
     * @param name the name of the sub-type.
     */
    CheckSubType(CheckType from, String name) {
        this.from = from;
        this.name = name;
    }

    /**
     * @return the super type
     */
    public CheckType from() {
        return from;
    }

    /**
     * @return the sub-type name
     */
    public String getName() {
        return name;
    }

    /**
     * Get sub-types that match the provided {@code check}
     *
     * @param check the check
     * @return the list of types.
     */
    public static List<CheckSubType> getSubTypesFor(CheckType check) {
        return VALUES.stream().filter(type -> type.from == check).collect(Collectors.toList());
    }

}
