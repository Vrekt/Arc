package bridge1_12.api;

import bridge.utility.BoundingBox;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Bukkit API implementation for 1.12
 */
public final class BukkitAccess implements bridge.api.BukkitAccess {
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
        return new BoundingBox(nms.a, nms.b, nms.c, nms.d, nms.e, nms.f);
    }

    @Override
    public PotionEffect getPotionEffect(Player player, PotionEffectType type) {
        return player.getPotionEffect(type);
    }

    @Override
    public boolean isFlyingWithElytra(Player player) {
        return player.isGliding();
    }

    @Override
    public boolean hasItemInHand(Player player, Material material) {
        return player.getInventory().getItemInMainHand().getType() == material
                || player.getInventory().getItemInOffHand().getType() == material;
    }

    @Override
    public boolean hasSlowFalling(Player player) {
        return false;
    }

    @Override
    public boolean hasLevitation(Player player) {
        return player.hasPotionEffect(PotionEffectType.LEVITATION);
    }

}