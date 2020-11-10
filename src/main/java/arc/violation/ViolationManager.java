package arc.violation;

import arc.Arc;
import arc.check.Check;
import arc.check.result.CheckResult;
import arc.violation.result.ViolationResult;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages violations
 */
public final class ViolationManager {

    /**
     * Violation history
     */
    private final Map<UUID, Violations> history = new ConcurrentHashMap<>();

    /**
     * Invoked when a player joins
     *
     * @param player the player
     */
    public void onPlayerJoin(Player player) {
        history.put(player.getUniqueId(), new Violations());
    }

    /**
     * Invoked when a player leaves
     *
     * @param player the player
     */
    public void onPlayerLeave(Player player) {
        final var violations = history.get(player.getUniqueId());
        violations.dispose();
        history.remove(player.getUniqueId());
    }

    /**
     * Process a violation
     *
     * @param player the player
     * @param result the result
     * @return the result
     */
    public ViolationResult violation(Player player, Check check, CheckResult result) {
        final var violationResult = new ViolationResult();
        final var violations = history.get(player.getUniqueId());
        final var level = violations.incrementViolationLevel(check.getName());

        if (check.configuration().notifyViolation() && check.configuration().shouldNotify(level)) {
            violationResult.addResult(ViolationResult.Result.NOTIFY);
            final var violationMessage = translate(Arc.arc().configuration().violationMessage(), player.getName(), check.getName(), level, result.information());
            Arc.arc().permissions().violationViewers().forEach(viewer -> viewer.sendMessage(violationMessage));
        }

        if (check.configuration().cancel() && check.configuration().shouldCancel(level)) {
            violationResult.addResult(ViolationResult.Result.CANCEL);
        }

        if (check.configuration().ban() && check.configuration().shouldBan(level)) {
            // TODO:
        }

        if (check.configuration().kick() && check.configuration().shouldKick(level)) {
            // TODO:
        }

        return violationResult;
    }

    /**
     * Replace placeholders within the configuration
     *
     * @param message     the message
     * @param player      the player name
     * @param check       the check name
     * @param level       the violation level
     * @param information the information
     * @return the string
     */
    private String translate(String message, String player, String check, int level, String information) {
        message = message.replace("%player%", player);
        message = message.replace("%check%", check);
        message = message.replace("%level%", level + "");
        message = message.replace("%information%", information == null ? "\nNo information" : "\n" + information);
        return message;
    }

}
