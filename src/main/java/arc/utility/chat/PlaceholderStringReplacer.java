package arc.utility.chat;

import arc.Arc;
import arc.check.Check;
import arc.configuration.types.Placeholders;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Basic string utility
 */
public final class PlaceholderStringReplacer {

    /**
     * The initial text
     */
    private String text;

    public PlaceholderStringReplacer(String text) {
        this.text = text;
    }

    /**
     * Replace player name
     *
     * @param player the player
     * @return this
     */
    public PlaceholderStringReplacer replacePlayer(Player player) {
        text = StringUtils.replace(text, Placeholders.PLAYER.placeholder(), player.getName());
        return this;
    }

    /**
     * Replace check name
     *
     * @param check  the check
     * @param append the append
     * @return this
     */
    public PlaceholderStringReplacer replaceCheck(Check check, String append) {
        if (append != null) {
            text = StringUtils.replace(text, Placeholders.CHECK.placeholder(), check.getName() + ChatColor.GRAY + " " + append + " ");
        } else {
            text = StringUtils.replace(text, Placeholders.CHECK.placeholder(), check.getName());
        }
        return this;
    }

    /**
     * Replace level
     *
     * @param level the level
     * @return this
     */
    public PlaceholderStringReplacer replaceLevel(int level) {
        text = StringUtils.replace(text, Placeholders.LEVEL.placeholder(), Integer.toString(level));
        return this;
    }

    /**
     * Replace prefix
     *
     * @return this
     */
    public PlaceholderStringReplacer replacePrefix() {
        text = StringUtils.replace(text, Placeholders.PREFIX.placeholder(), Arc.arc().configuration().prefix());
        return this;
    }

    /**
     * Replace time
     *
     * @param time the time
     * @return this
     */
    public PlaceholderStringReplacer replaceTime(String time) {
        text = StringUtils.replace(text, Placeholders.PREFIX.placeholder(), time);
        return this;
    }

    /**
     * @return the text
     */
    public String build() {
        return text;
    }

}
