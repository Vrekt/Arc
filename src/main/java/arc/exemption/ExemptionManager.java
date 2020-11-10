package arc.exemption;

import arc.Arc;
import arc.check.CheckType;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Manages player exemptions
 */
public final class ExemptionManager {

    /**
     * Test if a check is exempt while the player is flying
     */
    private final Predicate<CheckType> exemptWhenFlying = type -> type == CheckType.NOFALL || type == CheckType.FLIGHT || type == CheckType.SPEED;

    /**
     * Exemptions by player
     */
    private final Map<UUID, Exemptions> exemptions = new ConcurrentHashMap<>();

    /**
     * Invoked when a player joins
     *
     * @param player the player
     */
    public void onPlayerJoin(Player player) {
        exemptions.put(player.getUniqueId(), new Exemptions());
    }

    /**
     * Invoked when a player leaves
     *
     * @param player the player
     */
    public void onPlayerLeave(Player player) {
        final var exemptions = this.exemptions.get(player.getUniqueId());
        exemptions.dispose();
        this.exemptions.remove(player.getUniqueId());
    }

    /**
     * Check if a player is exempt via permissions
     *
     * @param player the player
     * @param debug  if {@code true} this function will return {@code false}
     * @return {@code true} if so
     */
    public boolean isPlayerExempt(Player player, boolean debug) {
        if (debug) return false;
        return Arc.arc().permissions().canBypassChecks(player);
    }

    /**
     * Check if a player is exempt
     *
     * @param player the player
     * @param check  the check
     * @return {@code true} if so
     * TODO: Not done yet!
     */
    public boolean isPlayerExempt(Player player, CheckType check) {
        // return if we are flying and the provided check is exempt while flying
        if (isFlying(player) && exemptWhenFlying.test(check)) return true;
        final var exemptions = this.exemptions.get(player.getUniqueId());
        return exemptions.isExempt(check);
    }

    /**
     * Add an exemption
     *
     * @param player   the player
     * @param check    the check
     * @param duration the duration
     */
    public void addExemption(Player player, CheckType check, long duration) {
        final var exemptions = this.exemptions.get(player.getUniqueId());
        exemptions.addExemption(check, duration);
    }

    /**
     * Check if the player is flying
     *
     * @param player the player
     * @return {@code true}
     */
    private boolean isFlying(Player player) {
        return player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR || player.getAllowFlight() || player.isFlying();
    }

}
