package arc.permissions;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
     * The administrator permission
     */
    public static final String ARC_ADMINISTRATOR = "arc.administrator";

    /**
     * A list of UUIDs who can view violations.
     */
    private final List<Player> violationViewers = new CopyOnWriteArrayList<>();

    /**
     * Invoked when a player joins
     *
     * @param player the player
     */
    public void onPlayerJoin(Player player) {
        if (canViewViolations(player)) violationViewers.add(player);
    }

    /**
     * Invoked when a player leaves
     *
     * @param player the player
     */
    public void onPlayerLeave(Player player) {
        if (canViewViolations(player)) violationViewers.remove(player);
    }

    /**
     * A list of violation viewers
     *
     * @return the violation viewers
     */
    public List<Player> violationViewers() {
        return violationViewers;
    }

    /**
     * Check if the player can view violations
     *
     * @param player the player
     * @return {@code true} if so.
     */
    public boolean canViewViolations(Player player) {
        return player.hasPermission(ARC_VIOLATIONS);
    }

    /**
     * Check if the player can bypass checks
     *
     * @param player the player
     * @return {@code true} if so.
     */
    public boolean canBypassChecks(Player player) {
        return player.hasPermission(ARC_BYPASS);
    }

    /**
     * Check if the player is an administrator.
     *
     * @param player the player
     * @return {@code true} if so
     */
    public boolean isAdministrator(Player player) {
        return player.hasPermission(ARC_ADMINISTRATOR);
    }

}
