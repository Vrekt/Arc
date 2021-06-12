package bridge1_16.api;

import bridge.utility.BoundingBox;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.minecraft.server.v1_16_R3.AxisAlignedBB;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Bukkit API implementation for 1.16
 */
public final class BukkitApi implements bridge.api.BukkitApi {
    @Override
    public Inventory createInventory(String title, int size) {
        return Bukkit.createInventory(null, size, Component.text(title));
    }

    @Override
    public void broadcast(String message, String permission) {
        Bukkit.broadcast(Component.text(message), permission);
    }

    @Override
    public void kickPlayer(Player player, String message) {
        player.kick(Component.text(message), PlayerKickEvent.Cause.PLUGIN);
    }

    @Override
    public void sendMessage(Player player, String message) {
        player.sendMessage(Component.text(message));
    }

    @Override
    public void sendMessage(Player player, TextComponent message) {
        player.sendMessage(Component.text(message.getText()));
    }

    @Override
    public void addHoverEvent(TextComponent component, String information) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(information)));
    }

    @Override
    public BoundingBox getBoundingBox(Entity entity) {
        final AxisAlignedBB nms = ((CraftEntity) entity).getHandle().getBoundingBox();
        return new BoundingBox(nms.minX, nms.minY, nms.minZ, nms.maxX, nms.maxY, nms.maxZ);
    }
}