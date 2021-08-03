package arc.bridge.packets;

import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.MinecraftKey;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * https://github.com/dmulloy2/PacketWrapper/blob/master/PacketWrapper/src/main/java/com/comphenix/packetwrapper/WrapperPlayClientCustomPayload.java
 */
public final class WrapperPlayClientCustomPayload16 extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.CUSTOM_PAYLOAD;

    public WrapperPlayClientCustomPayload16() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientCustomPayload16(PacketContainer packet) {
        super(packet, TYPE);
    }

    public MinecraftKey getChannel() {
        return handle.getMinecraftKeys().readSafely(0);
    }

    /**
     * Starting in 1.13, channel names need to be lower case, in the new identifier format,
     * i.e. {@code minecraft:brand}. The previously standard {@code |} is no longer allowed.
     */
    public void setChannel(MinecraftKey value) {
        handle.getMinecraftKeys().writeSafely(0, value);
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

    /**
     * Update payload contents with a Netty buffer
     *
     * @param contents - new payload contents
     */
    public void setContentsBuffer(ByteBuf contents) {
        if (MinecraftReflection.is(MinecraftReflection.getPacketDataSerializerClass(), contents)) {
            handle.getModifier().withType(ByteBuf.class).write(0, contents);
        } else {
            Object serializer = MinecraftReflection.getPacketDataSerializer(contents);
            handle.getModifier().withType(ByteBuf.class).write(0, serializer);
        }
    }

    /**
     * Update payload contents with a byte array
     *
     * @param content - new payload content
     */
    public void setContents(byte[] content) {
        setContentsBuffer(Unpooled.copiedBuffer(content));
    }
}
