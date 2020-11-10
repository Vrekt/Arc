package arc.data.player;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data related to player checks
 */
public final class PlayerData {

    /**
     * The register
     */
    private static final Map<UUID, PlayerData> REGISTER = new ConcurrentHashMap<>();

    /**
     * Get data
     *
     * @param player the player
     * @return the data
     */
    public static PlayerData get(Player player) {
        return REGISTER.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData());
    }

    /**
     * Remove data
     *
     * @param player the player
     */
    public static void remove(Player player) {
        REGISTER.remove(player.getUniqueId());
    }

    /**
     * Keeps track of potion effects
     */
    private final Map<PotionEffectType, TimedPotionEffect> potionEffects = new ConcurrentHashMap<>();

    /**
     * Add a potion effect
     *
     * @param type   the type
     * @param effect the effect
     */
    public void addEffect(PotionEffectType type, PotionEffect effect) {
        potionEffects.put(type, new TimedPotionEffect((System.currentTimeMillis() + ((effect.getDuration() * 1000) / 20)), effect));
    }

    /**
     * Remove an effect
     *
     * @param type the type
     */
    public void removeEffect(PotionEffectType type) {
        potionEffects.remove(type);
    }

    /**
     * Clear effects
     */
    public void removeEffects() {
        potionEffects.clear();
    }

    /**
     * Get the timed effect
     *
     * @param type the type
     * @return the {@link TimedPotionEffect}
     */
    public TimedPotionEffect getEffect(PotionEffectType type) {
        return potionEffects.get(type);
    }

    /**
     * A timed potion effect
     */
    public static final class TimedPotionEffect {

        /**
         * Time expected when to end
         */
        private final long time;

        /**
         * The potion effect
         */
        private final PotionEffect effect;

        public TimedPotionEffect(long time, PotionEffect effect) {
            this.time = time;
            this.effect = effect;
        }

        /**
         * @return the expected time
         */
        public long time() {
            return time;
        }

        /**
         * @return the effect
         */
        public PotionEffect effect() {
            return effect;
        }
    }

}
