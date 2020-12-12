package arc.check.player;

import arc.bridge.packets.BridgePlayClientBlockPlace;
import arc.check.CheckSubType;
import arc.check.CheckType;
import arc.check.PacketCheck;
import arc.check.result.CheckResult;
import arc.data.player.PlayerData;
import arc.violation.result.ViolationResult;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Checks if a player is using an item too fast.
 * use-delta-min: If higher, check is more relaxed.
 * delta-shot-min: If higher, check is more relaxed.
 * consume-time-ms: If less, check is more strict. (avg time with 0 ping is 1500ms)
 * TODO: Disable with versions > 1.8?
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
                .build();

        createSubTypeSections(CheckSubType.FAST_USE_FAST_BOW, CheckSubType.FAST_USE_FAST_CONSUME);
        addConfigurationValue(CheckSubType.FAST_USE_FAST_BOW, "use-delta-min", 100);
        addConfigurationValue(CheckSubType.FAST_USE_FAST_BOW, "delta-shot-min", 200);
        addConfigurationValue(CheckSubType.FAST_USE_FAST_CONSUME, "consume-time-ms", 1400);

        if (enabled()) load();
    }

    /**
     * Invoked when a player uses an item
     *
     * @param event the event
     */
    private void onBlockPlace(PacketEvent event) {
        final BridgePlayClientBlockPlace packet = new BridgePlayClientBlockPlace(event.getPacket());
        final Player player = event.getPlayer();

        final Material item = packet.getHeldItem(player).getType();
        if (item == Material.BOW) {
            if (exempt(player, CheckSubType.FAST_USE_FAST_BOW)) return;
            PlayerData.get(player).lastBowUse(System.currentTimeMillis());
        } else if (item.isEdible() || item == Material.POTION) {
            if (exempt(player, CheckSubType.FAST_USE_FAST_CONSUME)) return;
            PlayerData.get(player).consumeStartTime(System.currentTimeMillis());
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
        if (!enabled() || exempt(player) || exempt(player, CheckSubType.FAST_USE_FAST_BOW))
            return ViolationResult.EMPTY;

        final long lastUse = data.lastBowUse();
        final long lastShot = data.lastBowShoot();

        final long deltaUseToShot = Math.abs(lastUse - lastShot);
        final long useDelta = System.currentTimeMillis() - lastUse;

        // check the delta times against the configuration values.
        if (deltaUseToShot < deltaShotMinimum && useDelta < useDeltaMinimum) {
            final CheckResult result = new CheckResult(CheckResult.Result.FAILED, CheckSubType.FAST_USE_FAST_BOW);
            result.info("Used a bow too fast.");
            result.parameter("deltaShot", deltaUseToShot);
            result.parameter("minDeltaShow", deltaShotMinimum);
            result.parameter("useDelta", useDelta);
            result.parameter("minUseDelta", useDeltaMinimum);

            return checkViolation(player, result);
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
        if (!enabled() || exempt(player) || exempt(player, CheckSubType.FAST_USE_FAST_CONSUME))
            return ViolationResult.EMPTY;

        // the time it took to consume the item
        final long delta = System.currentTimeMillis() - data.consumeStartTime();
        if (delta < consumeTime) {
            final CheckResult result = new CheckResult(CheckResult.Result.FAILED, CheckSubType.FAST_USE_FAST_CONSUME);
            result.info("Consumed an item too fast.");
            result.parameter("delta", delta);
            result.parameter("min", consumeTime);
            return checkViolation(player, result);
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
        final ConfigurationSection fastBowSection = configuration.subTypeSection(CheckSubType.FAST_USE_FAST_BOW);
        useDeltaMinimum = fastBowSection.getLong("use-delta-min");
        deltaShotMinimum = fastBowSection.getLong("delta-shot-min");
        final ConfigurationSection fastConsumeSection = configuration.subTypeSection(CheckSubType.FAST_USE_FAST_CONSUME);
        consumeTime = fastConsumeSection.getLong("consume-time-ms");
        registerPacketListener(PacketType.Play.Client.BLOCK_PLACE, this::onBlockPlace);
    }

    @Override
    public void unload() {
        unregisterPacketListeners();
    }
}
