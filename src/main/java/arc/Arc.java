package arc;

import arc.check.CheckManager;
import arc.command.CommandArc;
import arc.configuration.ArcConfiguration;
import arc.exemption.ExemptionManager;
import arc.listener.connection.ConnectionListener;
import arc.listener.moving.MovingListener;
import arc.listener.network.MovingPacketListener;
import arc.listener.player.PlayerListener;
import arc.violation.ViolationManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main entry point for Arc.
 */
public final class Arc extends JavaPlugin {

    /**
     * The version of Arc.
     */
    public static final String VERSION = "1.0.6";

    /**
     * The file configuration
     */
    private static Arc arc;

    /**
     * The arc configuration
     */
    private ArcConfiguration arcConfiguration;

    /**
     * The violation manager.
     */
    private ViolationManager violationManager;

    /**
     * The check manager
     */
    private CheckManager checkManager;

    /**
     * The exemption manager
     */
    private final ExemptionManager exemptionManager = new ExemptionManager();

    /**
     * The protocol manager.
     */
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        arc = this;

        getLogger().info(ChatColor.RED + "Arc version " + VERSION);
        getLogger().info(ChatColor.RED + "[1] Loading configuration");

        saveDefaultConfig();
        arcConfiguration = new ArcConfiguration(getConfig());
        protocolManager = ProtocolLibrary.getProtocolManager();
        final var movingPacketListener = new MovingPacketListener(protocolManager);

        getLogger().info(ChatColor.RED + "[2] Registering checks and listeners");
        checkManager = new CheckManager();
        violationManager = new ViolationManager(arcConfiguration);

        getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new MovingListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        getCommand("arc").setExecutor(new CommandArc());

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
}
