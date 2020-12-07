package arc.configuration.check;

import arc.Arc;
import arc.check.CheckType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * A builder for the default check configuration template.
 */
public final class CheckConfigurationBuilder {

    /**
     * The check
     */
    private final CheckType check;

    /**
     * The section
     */
    private final ConfigurationSection section;

    /**
     * Initialize the builder
     *
     * @param check the check
     */
    public CheckConfigurationBuilder(CheckType check) {
        this.check = check;

        final FileConfiguration configuration = Arc.arc().getConfig();
        final String name = check.getName();
        if (configuration.isConfigurationSection(name)) {
            section = configuration.getConfigurationSection(name);
        } else {
            section = configuration.createSection(name);
        }
    }

    /**
     * Set if a check is enabled
     *
     * @param enabled enabled
     * @return this
     */
    public CheckConfigurationBuilder enabled(boolean enabled) {
        if (containsValue("enabled")) return this;
        section.set("enabled", enabled);
        return this;
    }

    /**
     * Set if a check should cancel
     *
     * @param cancel cancel
     * @return this
     */
    public CheckConfigurationBuilder cancel(boolean cancel) {
        if (containsValue("cancel")) return this;
        section.set("cancel", cancel);
        return this;
    }

    /**
     * Set the cancel level
     *
     * @param cancelLevel the level
     * @return this
     */
    public CheckConfigurationBuilder cancelLevel(int cancelLevel) {
        if (containsValue("cancel-level")) return this;
        section.set("cancel-level", cancelLevel);
        return this;
    }

    /**
     * Set if a check should notify
     *
     * @param notify notify
     * @return return this
     */
    public CheckConfigurationBuilder notify(boolean notify) {
        if (containsValue("notify")) return this;
        section.set("notify", notify);
        return this;
    }

    /**
     * Set the notify level
     *
     * @param notifyLevel the level
     * @return this
     */
    public CheckConfigurationBuilder notifyEvery(int notifyLevel) {
        if (containsValue("notify-every")) return this;
        section.set("notify-every", notifyLevel);
        return this;
    }

    /**
     * Set if a check should ban
     *
     * @param ban ban
     * @return this
     */
    public CheckConfigurationBuilder ban(boolean ban) {
        if (containsValue("ban")) return this;
        section.set("ban", ban);
        return this;
    }

    /**
     * Set the ban level
     *
     * @param banLevel the level
     * @return this
     */
    public CheckConfigurationBuilder banLevel(int banLevel) {
        if (containsValue("ban-level")) return this;
        section.set("ban-level", banLevel);
        return this;
    }

    /**
     * Set if a check should kick
     *
     * @param kick kick
     * @return this
     */
    public CheckConfigurationBuilder kick(boolean kick) {
        if (containsValue("kick")) return this;
        section.set("kick", kick);
        return this;
    }

    /**
     * Set the kick level
     *
     * @param kickLevel the level
     * @return this
     */
    public CheckConfigurationBuilder kickLevel(int kickLevel) {
        if (containsValue("kick-level")) return this;
        section.set("kick-level", kickLevel);
        return this;
    }

    /**
     * Build a new {@link CheckConfiguration}
     *
     * @return the new {@link CheckConfiguration}
     */
    public CheckConfiguration build() {
        return new CheckConfiguration(check, section);
    }

    /**
     * Check if a value exists.
     *
     * @param valueName the value name
     * @return {@code true} if so.
     */
    private boolean containsValue(String valueName) {
        return section.contains(valueName);
    }

}
