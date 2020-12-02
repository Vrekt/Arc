package arc.bridge.packets;

import arc.Arc;
import arc.bridge.Version;
import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class BridgePlayClientBlockPlace extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.BLOCK_PLACE;

    /**
     * The version of this packet.
     */
    private final Version version;

    public BridgePlayClientBlockPlace(PacketContainer packet) {
        super(packet, TYPE);
        this.version = Arc.version();
    }

    /**
     * @return if the item is main hand.
     */
    public boolean isMainHand() {
        return !version.isNewerThan(Version.VERSION_1_8) || handle.getHands().read(0) == EnumWrappers.Hand.MAIN_HAND;
    }

    /**
     * Retrieve Held item.
     *
     * @return The current Held item
     */
    public ItemStack getHeldItem(Player player) {
        return version.isNewerThan(Version.VERSION_1_8) ? player.getInventory().getItemInMainHand() : handle.getItemModifier().read(0);
    }

}
