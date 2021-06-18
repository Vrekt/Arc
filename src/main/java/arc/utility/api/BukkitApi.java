package arc.utility.api;

import arc.Arc;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Bukkit API compatibility
 */
public final class BukkitApi {

    private static final bridge.api.BukkitApi API = Arc.bridge().api();

    /**
     * Get potion effect
     *
     * @param player the player
     * @param type   the type
     * @return the potion effect or {@code null} if not found
     */
    public static PotionEffect getPotionEffect(Player player, PotionEffectType type) {
        return API.getPotionEffect(player, type);
    }

}
