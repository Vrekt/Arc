package arc.exemption;

import arc.Arc;
import arc.api.events.PlayerExemptionCheckEvent;
import arc.check.types.CheckSubType;
import arc.check.types.CheckType;
import arc.configuration.ArcConfiguration;
import arc.configuration.Configurable;
import arc.exemption.type.ExemptionType;
import arc.permissions.Permissions;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.io.Closeable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player exemptions
 */
public final class ExemptionManager implements Configurable, Closeable {

    /**
     * ExemptionHistory by player
     */
    private final Map<UUID, ExemptionHistory> exemptions = new ConcurrentHashMap<>();

    /**
     * if events should be used.
     */
    private boolean useEvents;

    @Override
    public void loadConfiguration(ArcConfiguration configuration) {
        useEvents = configuration.enableEventApi();
    }

    @Override
    public void reloadConfiguration(ArcConfiguration configuration) {
        useEvents = configuration.enableEventApi();
    }

    /**
     * Invoked when a player joins
     *
     * @param player the player
     */
    public void onPlayerJoin(Player player) {
        exemptions.put(player.getUniqueId(), new ExemptionHistory());
    }

    /**
     * Invoked when a player leaves
     *
     * @param player the player
     */
    public void onPlayerLeave(Player player) {
        final ExemptionHistory exemptions = this.exemptions.get(player.getUniqueId());
        this.exemptions.remove(player.getUniqueId());
        exemptions.clear();
    }

    /**
     * Check if a player is exempt
     *
     * @param player the player
     * @param check  the check
     * @return {@code true} if so
     */
    public boolean isPlayerExempt(Player player, CheckType check) {
        final boolean exemptFromCheck = isPlayerExemptFromCheck(player, check);
        if (exemptFromCheck) return true;

        final boolean exemptFlying = isFlying(player) && isExemptWhenFlying(check);
        if (exemptFlying) return true;

        final ExemptionHistory exemptions = this.exemptions.getOrDefault(player.getUniqueId(), ExemptionHistory.EMPTY);
        return isExemptFireEvent(player, check, null, exemptions.isExempt(check));
    }

    /**
     * Check if a player is exempt
     * <p>
     * TODO: Maybe include subType.getCheck() as apart of this
     *
     * @param player  the player
     * @param subType the sub-type
     * @return {@code true} if so
     */
    public boolean isPlayerExempt(Player player, CheckSubType subType) {
        final boolean permission = player.hasPermission(subType.getBypassPermission());
        return isExemptFireEvent(player, subType.getCheck(), subType, permission);
    }

    /**
     * Check if a player is exempt AFTER invoking the event/
     *
     * @param player  the player
     * @param check   the check
     * @param subType the sub-type
     * @return if the player is exempt
     */
    private boolean isExemptFireEvent(Player player, CheckType check, CheckSubType subType, boolean isExempt) {
        if (!useEvents) return isExempt;

        final PlayerExemptionCheckEvent event = new PlayerExemptionCheckEvent(player, check, subType, isExempt);
        Arc.triggerEvent(event);

        return event.setExempt();
    }

    /**
     * Check if a player is exempt from a check
     *
     * @param player the player
     * @param check  the check
     * @return {@code true} if so
     */
    public boolean isPlayerExemptFromCheck(Player player, CheckType check) {
        return Permissions.canBypassCheck(player, check);
    }

    /**
     * Check if a player is exempt from a certain type
     *
     * @param player the player
     * @param type   the check
     * @return {@code true} if so
     */
    public boolean isPlayerExempt(Player player, ExemptionType type) {
        return exemptions.get(player.getUniqueId()).isExempt(type);
    }

    /**
     * Add an exemption
     *
     * @param player   the player
     * @param check    the check
     * @param duration the duration
     */
    public void addExemption(Player player, CheckType check, long duration) {
        final ExemptionHistory exemptions = this.exemptions.get(player.getUniqueId());
        exemptions.addExemption(check, System.currentTimeMillis() + duration);
    }

    /**
     * Add an exemption permanently
     *
     * @param player the player
     * @param check  the check(s)
     */
    public void addExemptionPermanently(Player player, CheckType... check) {
        final ExemptionHistory exemptions = this.exemptions.get(player.getUniqueId());
        for (CheckType checkType : check) {
            exemptions.addExemptionPermanently(checkType);
        }
    }

    /**
     * Add an exemption type
     *
     * @param player the player
     * @param type   the type
     */
    public void addExemption(Player player, ExemptionType type) {
        exemptions.get(player.getUniqueId()).addExemption(type);
    }

    /**
     * Add an exemption type
     *
     * @param player   the player
     * @param type     the type
     * @param duration the duration
     */
    public void addExemption(Player player, ExemptionType type, long duration) {
        exemptions.get(player.getUniqueId()).addExemption(type, System.currentTimeMillis() + duration);
    }

    /**
     * Remove an exemption type
     *
     * @param player the player
     * @param type   the type
     */
    public void removeExemption(Player player, ExemptionType type) {
        exemptions.get(player.getUniqueId()).removeExemption(type);
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
        return check == CheckType.NOFALL
                || check == CheckType.FLIGHT
                || check == CheckType.SPEED
                || check == CheckType.JESUS
                || check == CheckType.CRITICALS;
    }

    @Override
    public void close() {
        exemptions.values().forEach(ExemptionHistory::clear);
        exemptions.clear();
    }
}
