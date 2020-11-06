package arc.configuration.punishment.ban;

/**
 * The ban length type.
 */
public enum BanLengthType {

    /**
     * How long a player should be banned for.
     */
    DAYS, YEARS, PERM;

    /**
     * Parse the {@link BanLengthType} from configuration
     *
     * @param input the input
     * @return the ban length type, {@code PERM} for default.
     */
    public static BanLengthType parse(String input) {
        final var actualInput = input.toUpperCase().strip();
        return actualInput.equals("DAYS") ? DAYS : actualInput.equals("YEARS") ? YEARS : PERM;
    }

}
