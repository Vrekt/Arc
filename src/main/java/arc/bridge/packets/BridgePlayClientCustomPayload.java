package arc.bridge.packets;

import arc.Arc;
import bridge.Version;
import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import io.netty.buffer.ByteBuf;

/**
 * https://github.com/dmulloy2/PacketWrapper/blob/master/PacketWrapper/src/main/java/com/comphenix/packetwrapper/WrapperPlayClientCustomPayload.java
 */
public final class BridgePlayClientCustomPayload extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.CUSTOM_PAYLOAD;

    public BridgePlayClientCustomPayload() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public BridgePlayClientCustomPayload(PacketContainer packet) {
        super(packet, TYPE);
    }

    public String getChannel() {
        return Arc.getMCVersion().isNewerThan(Version.VERSION_1_12) ?
                handle.getMinecraftKeys().readSafely(0).getFullKey() : handle.getStrings().read(0);
    }

    /**
     * Retrieve payload contents as a raw Netty buffer
     *
     * @return Payload contents as a Netty buffer
     */
    public ByteBuf getContentsBuffer() {
        return (ByteBuf) handle.getModifier().withType(ByteBuf.class).read(0);
    }

    /**
     * Retrieve payload contents
     *
     * @return Payload contents as a byte array
     */
    public byte[] getContents() {
        ByteBuf buffer = getContentsBuffer().copy();
        byte[] array = new byte[buffer.readableBytes()];
        buffer.readBytes(array);
        return array;
    }
}
