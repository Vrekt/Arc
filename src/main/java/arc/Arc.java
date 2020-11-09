package arc;

import arc.check.CheckManager;
import arc.configuration.ArcConfiguration;
import arc.exemption.ExemptionManager;
import arc.listener.ConnectionListener;
import arc.listener.moving.MovingListener;
import arc.packet.PacketManager;
import arc.permissions.Permissions;
import arc.violation.ViolationManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main entry point for Arc.
 */
public final class Arc extends JavaPlugin {

    /**
     * The version of Arc.
     */
    private static final String VERSION = "1.0.0-b1";

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
    private final CheckManager checkManager = new CheckManager();

    /**
     * The packet manager
     */
    private final PacketManager packetManager = new PacketManager();

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
        getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new MovingListener(), this);

        // save the configuration now since checks were registered.
        getLogger().info(ChatColor.RED + "[3] Saving configuration");
        saveConfig();

        getLogger().info(ChatColor.RED + "[4] Registering packet listeners");
        packetManager.register();

        getLogger().info(ChatColor.RED + "[5] Ready!");
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
    public ArcConfiguration arcConfiguration() {
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
}
