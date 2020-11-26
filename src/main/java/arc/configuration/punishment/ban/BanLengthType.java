package arc.configuration.punishment.ban;

import org.apache.commons.lang3.StringUtils;

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
        final String actualInput = StringUtils.deleteWhitespace(input.toUpperCase());
        return actualInput.equals("DAYS") ? DAYS : actualInput.equals("YEARS") ? YEARS : PERM;
    }

}
