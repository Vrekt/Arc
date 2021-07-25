package arc.punishment.litebans;

import arc.configuration.types.BanLengthType;
import arc.configuration.types.Placeholders;
import org.apache.commons.lang.StringUtils;

/**
 * Represents a lite ban command.
 */
public final class LiteBansCommand {

    /**
     * The value, command.
     */
    private String value, command;

    /**
     * The flag to use.
     */
    private String flag;

    public LiteBansCommand(String command) {
        this.value = command;
    }

    /**
     * Replace player value
     *
     * @param player the player
     * @return this
     */
    public LiteBansCommand playerOrIpAddress(String player) {
        value = StringUtils.replace(value, Placeholders.PLAYER.placeholder(), player);
        return this;
    }

    /**
     * Replace length value
     * https://www.spigotmc.org/resources/litebans.3715/
     *
     * @param type   the length type.
     * @param length the length.
     * @return this
     */
    public LiteBansCommand length(BanLengthType type, int length) {
        switch (type) {
            case DAYS:
                value = StringUtils.replace(value, Placeholders.LENGTH.placeholder(), length + "d");
                break;
            case YEARS:
                value = StringUtils.replace(value, Placeholders.LENGTH.placeholder(), length + "years");
                break;
            case PERM:
                value = StringUtils.replace(value, Placeholders.LENGTH.placeholder(), "");
                break;
        }
        return this;
    }

    /**
     * Replace reason
     *
     * @param reason the reason
     * @return this
     */
    public LiteBansCommand reason(String reason) {
        value = StringUtils.replace(value, Placeholders.REASON.placeholder(), reason);
        return this;
    }

    /**
     * Set the command
     *
     * @param command the command
     * @return the thi
     */
    public LiteBansCommand command(String command) {
        this.command = command;
        return this;
    }

    @Override
    public String toString() {
        return command + value;
    }
}
