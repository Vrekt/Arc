package arc;

import arc.check.CheckManager;
import arc.command.ArcCommand;
import arc.configuration.ArcConfiguration;
import arc.data.combat.CombatData;
import arc.data.moving.MovingData;
import arc.data.packet.PacketData;
import arc.data.player.PlayerData;
import arc.exemption.ExemptionManager;
import arc.inventory.InventoryRegister;
import arc.listener.Listeners;
import arc.violation.ViolationManager;
import bridge.Bridge;
import bridge.Version;
import bridge1_15.Bridge115;
import bridge1_16.Bridge116;
import bridge1_8.Bridge18;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * The main entry point for Arc.
 */
public final class Arc extends JavaPlugin {

    /**
     * The version of Arc.
     */
    public static final String VERSION_STRING = "2.0.0";

    /**
     * The file configuration
     */
    private static Arc arc;

    /**
     * The version
     */
    private static Version version;

    /**
     * The bridge
     */
    private static Bridge bridge;

    /**
     * The arc configuration
     */
    private final ArcConfiguration arcConfiguration = new ArcConfiguration();

    /**
     * The violation manager.
     */
    private final ViolationManager violationManager = new ViolationManager();

    /**
     * The check manager
     */
    private final CheckManager checkManager = new CheckManager();

    /**
     * The exemption manager
     */
    private final ExemptionManager exemptionManager = new ExemptionManager();

    /**
     * Inventory UI register.
     */
    private final InventoryRegister inventoryRegister = new InventoryRegister();

    /**
     * The protocol manager.
     */
    private ProtocolManager protocolManager;

    /**
     * If the version is incompatible
     */
    private boolean incompatible = false;

    @Override
    public void onEnable() {
        arc = this;
        getLogger().info("[INFO] Arc version " + VERSION_STRING);
        if (!loadCompatibleVersions()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("[INFO] Loading configuration");

        saveDefaultConfig();
        arcConfiguration.read(getConfig());
        protocolManager = ProtocolLibrary.getProtocolManager();

        getLogger().info("[INFO] Registering checks and listeners");
        checkManager.initialize();
        violationManager.initialize(arcConfiguration);

        Listeners.register(this, protocolManager);
        getServer().getPluginManager().registerEvents(inventoryRegister, this);

        getLogger().info("[INFO] Registering base command.");
        Objects.requireNonNull(getCommand("arc")).setExecutor(new ArcCommand());

        getLogger().info("[INFO] Saving configuration");
        saveConfig();

        getLogger().info("[SUCCESS] Ready!");
    }

    @Override
    public void onDisable() {
        if (incompatible) return;
        Listeners.unregister(protocolManager);

        exemptionManager.close();
        violationManager.close();
        checkManager.close();

        Bukkit.getOnlinePlayers().forEach(player -> {
            CombatData.remove(player);
            MovingData.remove(player);
            PacketData.remove(player);
            PlayerData.remove(player);
        });
    }

    /**
     * Load compatible versions
     * TODO: Specific 1.15.2 and 1.16.4
     */
    private boolean loadCompatibleVersions() {
        if (Bukkit.getVersion().contains("1.8")) {
            loadFor1_8_8();
        } else if (Bukkit.getVersion().contains("1.16")) {
            loadFor1_16();
        } else if (Bukkit.getVersion().contains("1.15")) {
            loadFor1_15();
        } else {
            getLogger().info("[INCOMPATIBLE] Arc is not compatible with this version: " + Bukkit.getVersion());
            incompatible = true;
            return false;
        }
        getLogger().info("[SUCCESS] Initialized Arc for: " + Bukkit.getVersion());
        return true;
    }

    /**
     * Load arc for 1.8.8
     */
    private void loadFor1_8_8() {
        version = Version.VERSION_1_8;
        bridge = new Bridge18();
    }

    /**
     * Load arc for 1.15
     */
    private void loadFor1_15() {
        version = Version.VERSION_1_15;
        bridge = new Bridge115();
    }

    /**
     * Load arc for 1.16
     */
    private void loadFor1_16() {
        version = Version.VERSION_1_16;
        bridge = new Bridge116();
    }

    /**
     * @return the internal plugin
     */
    public static JavaPlugin plugin() {
        return arc;
    }

    /**
     * @return arc
     */
    public static Arc arc() {
        return arc;
    }

    /**
     * @return the version
     */
    public static Version version() {
        return version;
    }

    /**
     * @return the bridge
     */
    public static Bridge bridge() {
        return bridge;
    }

    /**
     * @return the arc configuration
     */
    public ArcConfiguration configuration() {
        return arcConfiguration;
    }

    /**
     * @return the violation manager
     */
    public ViolationManager violations() {
        return violationManager;
    }

    /**
     * @return the exemptions manager
     */
    public ExemptionManager exemptions() {
        return exemptionManager;
    }

    /**
     * @return the check manager
     */
    public CheckManager checks() {
        return checkManager;
    }

    /**
     * @return the protocol manager
     */
    public ProtocolManager protocol() {
        return protocolManager;
    }

    /**
     * @return the inventory register
     */
    public InventoryRegister inventoryRegister() {
        return inventoryRegister;
    }
}
