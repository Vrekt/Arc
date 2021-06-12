package bridge1_15.api;

import bridge.utility.BoundingBox;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_15_R1.AxisAlignedBB;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Bukkit API implementation for 1.15
 */
public final class BukkitApi implements bridge.api.BukkitApi {
    @Override
    public Inventory createInventory(String title, int size) {
        return Bukkit.createInventory(null, size, title);
    }

    @Override
    public void broadcast(String message, String permission) {
        Bukkit.broadcast(message, permission);
    }

    @Override
    public void broadcast(String message) {
        Bukkit.broadcastMessage(message);
    }

    @Override
    public void kickPlayer(Player player, String message) {
        player.kickPlayer(message);
    }

    @Override
    public void sendMessage(Player player, String message) {
        player.sendMessage(message);
    }

    @Override
    public void sendMessage(Player player, TextComponent message) {
        player.sendMessage(message);
    }

    @Override
    public void addHoverEvent(TextComponent component, String information) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(information).create()));
    }

    @Override
    public BoundingBox getBoundingBox(Entity entity) {
        final AxisAlignedBB nms = ((CraftEntity) entity).getHandle().getBoundingBox();
        return new BoundingBox(nms.minX, nms.minY, nms.minZ, nms.maxX, nms.maxY, nms.maxZ);
    }
}