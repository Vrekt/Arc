package arc.exemption;

import arc.Arc;
import arc.check.CheckType;
import arc.permissions.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.io.Closeable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player exemptions
 */
public final class ExemptionManager implements Closeable {

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
        final boolean canBypassAll = Permissions.canBypassAllChecks(player);
        if (!canBypassAll) {
            // if we cannot bypass all checks add our exemptions map.
            exemptions.put(player.getUniqueId(), new Exemptions());
            doJoinExemptions(player);
        }
    }

    /**
     * Exempt players when joining.
     * MORE_PACKETS because sometimes when joining their is client lag that will batch and spam packets.
     *
     * @param player player
     */
    private void doJoinExemptions(Player player) {
        addExemption(player, CheckType.MORE_PACKETS, 500);
    }

    /**
     * Invoked when a player leaves
     *
     * @param player the player
     */
    public void onPlayerLeave(Player player) {
        final Exemptions exemptions = this.exemptions.get(player.getUniqueId());
        exemptions.close();
        this.exemptions.remove(player.getUniqueId());
    }

    /**
     * Check if a player is exempt
     *
     * @param player the player
     * @param check  the check
     * @return {@code true} if so
     */
    public boolean isPlayerExempt(Player player, CheckType check) {
        // check general exemption
        if (isPlayerExemptFromCheck(player, check)) return true;
        // check flying status
        if (isFlying(player) && isExemptWhenFlying(check)) return true;
        // check other added exemptions
        final Exemptions exemptions = this.exemptions.get(player.getUniqueId());
        return exemptions.isExempt(check);
    }

    /**
     * Check if a player is exempt from a check
     *
     * @param player the player
     * @param check  the check
     * @return {@code true} if so
     */
    public boolean isPlayerExemptFromCheck(Player player, CheckType check) {
        return Permissions.canBypassChecks(player, check);
    }

    /**
     * Add an exemption
     *
     * @param player   the player
     * @param check    the check
     * @param duration the duration
     */
    public void addExemption(Player player, CheckType check, long duration) {
        final Exemptions exemptions = this.exemptions.get(player.getUniqueId());
        exemptions.addExemption(check, System.currentTimeMillis() + duration);
    }

    /**
     * Exempt all players from all checks
     *
     * @param duration the duration
     */
    public void exemptAllPlayersFromAllChecksFor(long duration) {
        Arc.arc().checks().getAllChecks().forEach(check -> Bukkit.getOnlinePlayers().forEach(player -> addExemption(player, check.type(), duration)));
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
     * Check if the player from a check when flying.
     *
     * @param check the check
     * @return {@code true} if so
     */
    private boolean isExemptWhenFlying(CheckType check) {
        return check == CheckType.NOFALL || check == CheckType.FLIGHT || check == CheckType.SPEED || check == CheckType.JESUS;
    }

    @Override
    public void close() {
        exemptions.values().forEach(Exemptions::close);
        exemptions.clear();
    }
}
