package arc.permissions;

import arc.check.types.CheckCategory;
import arc.check.types.CheckType;
import org.bukkit.entity.Player;

/**
 * Arc permissions
 */
public final class Permissions {

    /**
     * The violations permission
     */
    public static final String ARC_VIOLATIONS = "arc.violations";

    /**
     * The bypass permission
     */
    public static final String ARC_BYPASS = "arc.bypass";

    /**
     * The base command permission.
     */
    public static final String ARC_COMMANDS_BASE = "arc.commands.base";

    /**
     * The permission to toggle violations
     */
    public static final String ARC_COMMANDS_TOGGLE_VIOLATIONS = "arc.commands.toggleviolations";

    /**
     * The permission to cancel player bans
     */
    public static final String ARC_COMMANDS_CANCEL_BAN = "arc.commands.cancelban";

    /**
     * The permission to reload the config
     */
    public static final String ARC_COMMANDS_RELOAD_CONFIG = "arc.commands.reloadconfig";

    /**
     * The permission to execute all arc commands
     */
    public static final String ARC_COMMANDS_ALL = "arc.commands.all";

    /**
     * The permission to view player summaries.
     */
    public static final String ARC_COMMANDS_SUMMARY = "arc.commands.summary";

    /**
     * The permission to view timings
     */
    public static final String ARC_COMMANDS_TIMINGS = "arc.commands.timings";

    /**
     * The permission to exempt from certain checks
     */
    public static final String ARC_COMMANDS_EXEMPT = "arc.commands.exempt";

    /**
     * The permission to enable/disable debug.
     */
    public static final String ARC_COMMANDS_DEBUG = "arc.commands.debug";

    /**
     * Check if the player can view violations
     *
     * @param player the player
     * @return {@code true} if so.
     */
    public static boolean canViewViolations(Player player) {
        if (player == null || !player.isOnline()) return false;
        return player.hasPermission(ARC_VIOLATIONS);
    }

    /**
     * Check if the player can bypass checks
     *
     * @param player the player
     * @return {@code true} if so.
     */
    public static boolean canBypassAllChecks(Player player) {
        if (player == null || !player.isOnline()) return true;
        return player.hasPermission(ARC_BYPASS);
    }

    /**
     * Check if a player can bypass a category all together
     *
     * @param player   the player
     * @param category the category
     * @return {@code true} if so
     */
    public static boolean canBypassCategory(Player player, CheckCategory category) {
        if (player == null || !player.isOnline() || canBypassAllChecks(player)) return true;
        return player.hasPermission(category.getBypassPermission());
    }

    /**
     * Check if the player can bypass the provided checks.
     *
     * @param player the player
     * @param check  check
     * @return {@code true} if so.
     */
    public static boolean canBypassCheck(Player player, CheckType check) {
        if (player == null || !player.isOnline() || canBypassAllChecks(player)) return true;
        return player.hasPermission(check.getCategory().getBypassPermission())
                || player.hasPermission(check.getBypassPermission());
    }
}
