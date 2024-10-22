package arc.configuration.check;

import arc.Arc;
import arc.check.types.CheckSubType;
import arc.check.types.CheckType;
import arc.configuration.ArcConfiguration;
import arc.configuration.Configurable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A check configuration format.
 */
public final class CheckConfiguration implements Configurable {

    /**
     * The check for this configuration.
     */
    private final CheckType check;

    /**
     * The map of sub-type sections.
     */
    private final Map<CheckSubType, ConfigurationSection> subTypeSections = new HashMap<>();

    /**
     * The section for this check
     */
    private ConfigurationSection section;

    /**
     * Boolean check values
     */
    private boolean enabled, cancel, notify, ban, kick;

    /**
     * Violation levels
     */
    private int cancelLevel, notifyLevel, banLevel, kickLevel;

    /**
     * Initialize this check configuration
     *
     * @param check   the check type
     * @param section the section
     */
    public CheckConfiguration(CheckType check, ConfigurationSection section) {
        this.check = check;
        this.section = section;

        readFromFile(null);
    }

    @Override
    public void readFromFile(FileConfiguration configuration) {
        // retrieve booleans
        enabled = section.getBoolean("enabled");
        cancel = section.getBoolean("cancel");
        notify = section.getBoolean("notify");
        ban = section.getBoolean("ban");
        kick = section.getBoolean("kick");

        // retrieve levels
        cancelLevel = section.getInt("cancel-level");
        notifyLevel = section.getInt("notify-every");
        banLevel = section.getInt("ban-level");
        kickLevel = section.getInt("kick-level");

        // retrieve sub-types
        CheckSubType.getSubTypesFor(check).forEach(subType -> {
            // retrieve the section.
            final ConfigurationSection subTypeSection = section.getConfigurationSection(subType.getName());
            // put it in the map
            if (subTypeSection != null) {
                subTypeSections.put(subType, subTypeSection);
            }
        });
    }

    @Override
    public void reloadConfiguration(ArcConfiguration configuration) {
        this.section = Arc.getPlugin().getConfig().getConfigurationSection(check.getConfigurationName());
        readFromFile(null);
    }

    /**
     * Get the sub-type section
     * If the section is not found, will return the default {@code section} within this configuration.
     *
     * @param subType the sub-type
     * @return the section
     */
    public ConfigurationSection getSubType(CheckSubType subType) {
        return subTypeSections.getOrDefault(subType, section);
    }

    /**
     * Create a sub-type section
     *
     * @param subType the sub-type
     */
    public void createSubTypeSection(CheckSubType subType) {
        if (section.isConfigurationSection(subType.getName())) return;
        subTypeSections.put(subType, section.createSection(subType.getName()));
    }

    /**
     * Add a configuration value
     *
     * @param valueName the value name
     * @param value     the value
     */
    public void addConfigurationValue(String valueName, Object value) {
        if (containsValue(valueName)) return;
        section.set(valueName, value);
    }

    /**
     * Add a configuration value to a sub-type
     *
     * @param type      the type
     * @param valueName the value name
     * @param value     the value
     */
    public void addConfigurationValueTo(CheckSubType type, String valueName, Object value) {
        final ConfigurationSection section = getSubType(type);

        // basic comparison
        if (section.getName().equals(this.section.getName())) {
            Arc.getPlugin().getLogger().warning("Failed to write to sub-type: " + type.getName());
            return;
        }
        if (section.contains(valueName)) return;
        section.set(valueName, value);
    }

    /**
     * Get a long
     *
     * @param name the name
     * @return a long
     */
    public long getLong(String name) {
        return section.getLong(name);
    }

    /**
     * Get a double
     *
     * @param name the name
     * @return the double
     */
    public double getDouble(String name) {
        return section.getDouble(name);
    }

    /**
     * Get a boolean
     *
     * @param name the name
     * @return the boolean
     */
    public boolean getBoolean(String name) {
        return section.getBoolean(name);
    }

    /**
     * Get a int
     *
     * @param name the name
     * @return the int
     */
    public int getInt(String name) {
        return section.getInt(name);
    }

    /**
     * Get a list
     *
     * @param name the name
     * @return the list
     */
    public List<String> getList(String name) {
        return section.getStringList(name);
    }

    /**
     * @return if the check is enabled
     */
    public boolean enabled() {
        return enabled;
    }

    /**
     * @return if the check should cancel
     */
    public boolean cancel() {
        return cancel;
    }

    /**
     * @return if the check should notify
     */
    public boolean notifyViolation() {
        return notify;
    }

    /**
     * @return if the check should ban
     */
    public boolean ban() {
        return ban;
    }

    /**
     * @return if the check should kick.
     */
    public boolean kick() {
        return kick;
    }

    /**
     * If the check should be cancelled.
     *
     * @param violationLevel the players violation level
     * @return {@code true} if so
     */
    public boolean shouldCancel(int violationLevel) {
        return cancel() && violationLevel >= cancelLevel;
    }

    /**
     * Check if this check should notify right now
     *
     * @param violationLevel the players violation level
     * @return {@code true} if so
     */
    public boolean shouldNotify(int violationLevel) {
        return notifyViolation() && (notifyLevel == 1 || violationLevel % notifyLevel == 0);
    }

    /**
     * If the player should be banned.
     *
     * @param violationLevel the violation level
     * @return {@code true} if so
     */
    public boolean shouldBan(int violationLevel) {
        return ban() && violationLevel >= banLevel;
    }

    /**
     * If the player should be kicked
     *
     * @param violationLevel the violation level
     * @return {@code true} if so
     */
    public boolean shouldKick(int violationLevel) {
        return kick() && violationLevel >= kickLevel;
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
