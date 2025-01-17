package arc.check.types;

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
     * KillAura Direction
     */
    KILL_AURA_DIRECTION(CheckType.KILL_AURA, "direction", "Direction"),

    /**
     * KillAura Speed
     */
    KILL_AURA_ATTACK_SPEED(CheckType.KILL_AURA, "attackspeed", "AttackSpeed"),

    /**
     * FastUse FastBow
     */
    FAST_USE_FAST_BOW(CheckType.FAST_USE, "fastbow", "FastBow"),

    /**
     * FastUse FastConsume
     */
    FAST_USE_FAST_CONSUME(CheckType.FAST_USE, "fastconsume", "FastConsume"),

    /**
     * Flight BoatFly
     */
    FLIGHT_BOATFLY(CheckType.FLIGHT, "boatfly", "BoatFly"),

    /**
     * Flight ElytraFly
     */
    FLIGHT_ELYTRAFLY(CheckType.FLIGHT, "elytrafly", "ElytraFly");

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
     * The fancy name
     * The permission to bypass
     */
    private final String name, prettyName, bypassPermission;

    /**
     * Initialize the sub-type
     *
     * @param from the super type
     * @param name the name of the sub-type.
     */
    CheckSubType(CheckType from, String name, String prettyName) {
        this.from = from;
        this.name = name;
        this.prettyName = prettyName;

        this.bypassPermission = from.getBypassPermission() + "." + name;
    }

    /**
     * @return the super type
     */
    public CheckType getCheck() {
        return from;
    }

    /**
     * @return the sub-type name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the fancy name
     */
    public String getPrettyName() {
        return prettyName;
    }

    /**
     * @return the bypass permission
     */
    public String getBypassPermission() {
        return bypassPermission;
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
