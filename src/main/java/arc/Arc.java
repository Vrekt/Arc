package arc;

import arc.check.CheckManager;
import arc.configuration.ArcConfiguration;
import arc.exemption.ExemptionManager;
import arc.listener.ConnectionListener;
import arc.listener.inventory.InventoryListener;
import arc.listener.moving.MovingListener;
import arc.permissions.Permissions;
import arc.violation.ViolationManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main entry point for Arc.
 */
public final class Arc extends JavaPlugin {

    /**
     * Debug
     */
    public static final boolean DEBUG = false;

    /**
     * The version of Arc.
     */
    private static final String VERSION = "1.0.3";

    /**
     * The file configuration
     */
    private static Arc arc;

    /**
     * The arc configuration
     */
    private ArcConfiguration arcConfiguration;

    /**
     * The permissions utility
     */
    private final Permissions permissions = new Permissions();

    /**
     * The violation manager.
     */
    private final ViolationManager violationManager = new ViolationManager();

    /**
     * The check manager
     */
    private CheckManager checkManager;

    /**
     * The exemption manager
     */
    private final ExemptionManager exemptionManager = new ExemptionManager();

    @Override
    public void onEnable() {
        arc = this;

        getLogger().info(ChatColor.RED + "Arc version " + VERSION);
        getLogger().info(ChatColor.RED + "[1] Loading configuration");

        saveDefaultConfig();
        arcConfiguration = new ArcConfiguration(getConfig());

        getLogger().info(ChatColor.RED + "[2] Registering checks and listeners");
        checkManager = new CheckManager();

        getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new MovingListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketListener() {
            @Override
            public void onPacketSending(PacketEvent packetEvent) {

            }

            @Override
            public void onPacketReceiving(PacketEvent packetEvent) {
                System.err.println(packetEvent.getPacket());
            }

            @Override
            public ListeningWhitelist getSendingWhitelist() {
                return null;
            }

            @Override
            public ListeningWhitelist getReceivingWhitelist() {
                return null;
            }

            @Override
            public Plugin getPlugin() {
                return Arc.plugin();
            }
        });

        // save the configuration now since checks were registered.
        getLogger().info(ChatColor.RED + "[3] Saving configuration");
        saveConfig();

        getLogger().info(ChatColor.RED + "[4] Ready!");
    }

    @Override
    public void onDisable() {

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
     * @return the arc configuration
     */
    public ArcConfiguration configuration() {
        return arcConfiguration;
    }

    /**
     * @return the permissions utility
     */
    public Permissions permissions() {
        return permissions;
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

}
