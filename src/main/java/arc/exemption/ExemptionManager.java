package arc.exemption;

import arc.Arc;
import arc.check.Check;
import arc.check.CheckCategory;
import arc.check.CheckType;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
     * Keeps track of players who are not exempt.
     */
    private final List<Player> playerList = new CopyOnWriteArrayList<>();

    /**
     * Invoked when a player joins
     *
     * @param player the player
     */
    public void onPlayerJoin(Player player) {
        exemptions.put(player.getUniqueId(), new Exemptions());

        // TODO: Maybe more later.
        // TODO: This is for the moving task, we just want a easy list to grab.
        // TODO: Instead of looping all players and constantly checking permissions.
        // TODO: May need to be changed or a reload permissions command.
        if (!Arc.arc().permissions().canBypassChecks(player)
                && !isPlayerExemptInCategory(player, CheckCategory.MOVING)) {
            playerList.add(player);
        }
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
        playerList.remove(player);
    }

    /**
     * Check if a player is exempt
     *
     * @param player the player
     * @param check  the check
     * @return {@code true} if so
     * TODO: Not done yet!
     */
    public boolean isPlayerExempt(Player player, CheckCategory category, CheckType check) {
        // check permissions
        if (Arc.arc().permissions().canBypassChecks(player, category, check)) return true;

        // check flying status
        if (isFlying(player) && exemptWhenFlying.test(check)) return true;
        // check other general exemptions.
        final var exemptions = this.exemptions.get(player.getUniqueId());
        return exemptions.isExempt(check);
    }

    /**
     * Check if a player is exempt from any check in a category.
     *
     * @param player   the player
     * @param category the category
     * @return {@code true} if so
     */
    public boolean isPlayerExemptInCategory(Player player, CheckCategory category) {
        for (Check check : Arc.arc().checks().withinCategory(category)) {
            if (Arc.arc().permissions().canBypassChecks(player, category, check.type())) return true;
        }
        return false;
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

    /**
     * @return non exempt player list
     */
    public List<Player> playerList() {
        return playerList;
    }
}
