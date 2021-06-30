package arc.check.compatibility;

import arc.data.Data;
import org.bukkit.entity.Player;

/**
 * Represents a single check version.
 *
 * @param <T> the type of data required
 */
public interface CheckVersion<T extends Data> {

    /**
     * Check for this version.
     *
     * @param player the player
     * @param data   their data
     * @return the result
     */
    boolean check(Player player, T data);

}
