package arc.check.player;

import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.player.PlayerData;
import com.comphenix.packetwrapper.WrapperPlayServerEntityEffect;
import com.comphenix.packetwrapper.WrapperPlayServerRemoveEntityEffect;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Checks if the player is removing bad effects.
 * TODO: Call this Zoot? I feel like BadEffects is better.
 * TODO: Not totally perfect but does flag.
 * TODO: Doesn't work with /effect command (will flag)
 * TODO: Fix duration, (save duration from initial potion effect add)
 */
public final class BadEffects extends PacketCheck {

    /**
     * TODO: Configurable
     */
    private final List<PotionEffectType> badEffects = List.of(
            PotionEffectType.BLINDNESS,
            PotionEffectType.CONFUSION,
            PotionEffectType.POISON,
            PotionEffectType.WEAKNESS,
            PotionEffectType.SLOW,
            PotionEffectType.CONFUSION,
            PotionEffectType.WITHER,
            PotionEffectType.SLOW_DIGGING,
            PotionEffectType.HUNGER);

    /**
     * The tolerance allowed to account for lag.
     */
    private final int tolerance;

    public BadEffects() {
        super("BadEffects", CheckType.BAD_EFFECTS);
        enabled(false).
                cancel(true).
                cancelLevel(0).
                notify(true).
                notifyEvery(1).
                ban(false).
                kick(false).
                write();

        addConfigurationValue("tolerance", 1000);
        tolerance = getValueInt("tolerance");

        if (enabled()) {
            registerListener(PacketType.Play.Server.ENTITY_EFFECT, this::onEntityEffectAdded);
            registerListener(PacketType.Play.Server.REMOVE_ENTITY_EFFECT, this::onEntityEffectRemoved);
        }
    }

    /**
     * Invoked when a entity effect is added.
     *
     * @param event the event.
     */
    private void onEntityEffectAdded(PacketEvent event) {
        final var packet = new WrapperPlayServerEntityEffect(event.getPacket());
        final var effect = PotionEffectType.values()[packet.getEffectID()];
        final var player = event.getPlayer();

        // if its not a bad effect or isn't the player, return
        if (!badEffects.contains(effect) || packet.getEntityID() != player.getEntityId()) return;
        final var data = PlayerData.get(player);
        // add the effect, divide duration by 20 since its in ticks.
        data.addEffect(effect, new PotionEffect(effect, packet.getDuration(), packet.getAmplifier(), false, packet.getHideParticles()));
    }

    /**
     * Invoked when a entity effect is removed
     *
     * @param event the event
     */
    private void onEntityEffectRemoved(PacketEvent event) {
        final var packet = new WrapperPlayServerRemoveEntityEffect(event.getPacket());
        final var player = event.getPlayer();
        if (packet.getEntityID() != player.getEntityId()) return;
        final var result = new CheckResult();

        // retrieve data and expected time value.
        final var data = PlayerData.get(player);
        final var effect = packet.getEffect();
        final var timed = data.getEffect(effect);
        if (timed == null) return;
        data.removeEffect(effect);

        // potion was used way too fast, flag.
        final var time = System.currentTimeMillis() + tolerance;
        if (time < timed.time()) {
            result.setFailed("Player potion effect disappeared too quickly. t=" + time + ", e=" + timed.time() + " d=" + (time - timed.time()));
        }

        final var violation = result(player, result);
        if (violation.cancel()) {
            // give the player the potion effect back.
            player.addPotionEffect(timed.effect());
        }
    }
}
