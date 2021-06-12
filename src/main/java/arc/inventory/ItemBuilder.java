package arc.inventory;

import arc.Arc;
import bridge.Version;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Easy building of items
 */
public final class ItemBuilder {

    /**
     * The item
     */
    private final ItemStack item;

    /**
     * The item meta
     */
    private final ItemMeta meta;

    /**
     * The item lore
     */
    private final List<String> lore = new ArrayList<>();

    /**
     * Initialize
     *
     * @param item the name of the item
     */
    public ItemBuilder(String item) {
        this.item = Arc.bridge().material().createItem(item);
        this.meta = this.item.getItemMeta();
    }

    /**
     * Initialize
     *
     * @param legacyItem the legacy name
     * @param newItem    the new name
     * @param data       the possible data
     */
    public ItemBuilder(String legacyItem, String newItem, int data) {
        if (Arc.version().isNewerThan(Version.VERSION_1_8)) {
            item = Arc.bridge().material().createItem(newItem);
        } else {
            item = Arc.bridge().material().createItem(legacyItem, (short) data);
        }

        this.meta = this.item.getItemMeta();
    }

    /**
     * Set the display name
     * TODO @Deprecated
     *
     * @param name the name
     * @return this
     */
    public ItemBuilder displayName(String name) {
        meta.setDisplayName(ChatColor.RESET + (name));
        return this;
    }

    /**
     * Add to the item lore
     *
     * @param lore the lore
     * @return this
     */
    public ItemBuilder lore(String lore) {
        this.lore.add(lore);
        return this;
    }

    /**
     * Build
     * TODO @Deprecated
     *
     * @return the internal {@link ItemStack}
     */
    public ItemStack build() {
        meta.setLore(lore);
        item.setItemMeta(meta);
        lore.clear();
        return item;
    }

}
