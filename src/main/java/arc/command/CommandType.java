package arc.command;

import java.util.Arrays;
import java.util.List;

/**
 * The command type.
 */
public enum CommandType {

    VIOLATIONS("violations"),
    TIMINGS("timings"),
    RELOAD("reload");

    private static final List<CommandType> VALUES = Arrays.asList(values());
    /**
     * The name
     */
    private final String name;

    CommandType(String name) {
        this.name = name;
    }

    /**
     * Get the command type
     *
     * @param command the command
     * @return the type or {@code null}
     */
    public static CommandType get(String command) {
        return VALUES.stream().filter(type -> command.equalsIgnoreCase(type.name)).findAny().orElse(null);
    }

}
