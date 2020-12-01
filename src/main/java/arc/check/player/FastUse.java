package arc.check.player;

import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.player.PlayerData;
import arc.violation.result.ViolationResult;
import com.comphenix.packetwrapper.WrapperPlayClientBlockPlace;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Checks if a player is using an item too fast.
 * use-delta-min: If higher, check is more relaxed.
 * delta-shot-min: If higher, check is more relaxed.
 * consume-time-ms: If less, check is more strict. (avg time with 0 ping is 1500ms)
 */
public final class FastUse extends PacketCheck {

    /**
     * The use time minimum
     * The shot time minimum
     * The time it takes to consume an item
     */
    private long useDeltaMinimum, deltaShotMinimum, consumeTime;

    public FastUse() {
        super(CheckType.FAST_USE);
        enabled(true)
                .cancel(true)
                .cancelLevel(0)
                .notify(true)
                .notifyEvery(1)
                .ban(false)
                .kick(false)
                .write();

        addConfigurationValue("use-delta-min", 100);
        addConfigurationValue("delta-shot-min", 200);
        addConfigurationValue("consume-time-ms", 1400);

        if (enabled()) load();
    }

    /**
     * Invoked when a player uses an item
     *
     * @param event the event
     */
    private void onBlockPlace(PacketEvent event) {
        final WrapperPlayClientBlockPlace packet = new WrapperPlayClientBlockPlace(event.getPacket());
        final Material item = packet.getHeldItem().getType();
        final Player player = event.getPlayer();

        if (item == Material.BOW) {
            if (exemptSubType(player, "fastbow")) return;
            PlayerData.get(event.getPlayer()).lastBowUse(System.currentTimeMillis());
        } else if (item.isEdible() || item == Material.POTION) {
            if (exemptSubType(player, "fastconsume")) return;
            PlayerData.get(event.getPlayer()).consumeStartTime(System.currentTimeMillis());
        }
    }

    /**
     * Check player using fast bow
     *
     * @param player the player
     * @param data   the data
     * @return the result
     */
    public ViolationResult checkFastBow(Player player, PlayerData data) {
        if (!enabled() || exempt(player) || exemptSubType(player, "fastbow")) return ViolationResult.EMPTY;

        final long lastUse = data.lastBowUse();
        final long lastShot = data.lastBowShoot();

        final long deltaUseToShot = Math.abs(lastUse - lastShot);
        final long useDelta = System.currentTimeMillis() - lastUse;

        // check the delta times against the configuration values.
        if (deltaUseToShot < deltaShotMinimum && useDelta < useDeltaMinimum) {
            final CheckResult result = new CheckResult(CheckResult.Result.FAILED);
            result.information("Used a bow too fast, deltaShot=" + deltaUseToShot + " min=" + deltaShotMinimum + " + useDelta=" + useDelta + " min=" + useDeltaMinimum, "(FastBow)");
            return result(player, result);
        }

        return ViolationResult.EMPTY;
    }

    /**
     * Check for fast consume
     *
     * @param player the player
     * @param data   the data
     * @return the result
     */
    public ViolationResult checkFastConsume(Player player, PlayerData data) {
        if (!enabled() || exempt(player) || exemptSubType(player, "fastconsume")) return ViolationResult.EMPTY;

        // the time it took to consume the item
        final long delta = System.currentTimeMillis() - data.consumeStartTime();
        if (delta < consumeTime) {
            final CheckResult result = new CheckResult(CheckResult.Result.FAILED);
            result.information("Consumed an item too fast, delta=" + delta + ", min=" + consumeTime, "(FastConsume)");
            return result(player, result);
        }

        return ViolationResult.EMPTY;
    }

    @Override
    public void reloadConfig() {
        unload();

        if (enabled()) load();
    }

    @Override
    public void load() {
        useDeltaMinimum = getValueLong("use-delta-min");
        deltaShotMinimum = getValueLong("delta-shot-min");
        consumeTime = getValueLong("consume-time-ms");
        registerPacketListener(PacketType.Play.Client.BLOCK_PLACE, this::onBlockPlace);
    }

    @Override
    public void unload() {
        unregisterPacketListeners();
    }
}
