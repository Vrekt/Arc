package arc;

import arc.check.CheckManager;
import arc.command.CommandArc;
import arc.configuration.ArcConfiguration;
import arc.data.DataUtility;
import arc.exemption.ExemptionManager;
import arc.listener.Listeners;
import arc.violation.ViolationManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main entry point for Arc.
 */
public final class Arc extends JavaPlugin {

    /**
     * The version of Arc.
     */
    public static final String VERSION = "1.0.8";

    /**
     * The file configuration
     */
    private static Arc arc;

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
     * The protocol manager.
     */
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        arc = this;

        getLogger().info("[INFO] Arc version " + VERSION + ". This build is for 1.8.8 only.");
        getLogger().info("[INFO] Loading configuration");

        saveDefaultConfig();
        arcConfiguration.read(getConfig());
        protocolManager = ProtocolLibrary.getProtocolManager();

        getLogger().info("[INFO] Registering checks and listeners");
        checkManager.initialize();
        violationManager.initialize(arcConfiguration);

        Listeners.register(this, protocolManager);

        getLogger().info("[INFO] Registering base command.");
        getCommand("arc").setExecutor(new CommandArc());

        getLogger().info("[INFO] Saving configuration");
        saveConfig();

        getLogger().info("[SUCCESS] Ready!");
    }

    @Override
    public void onDisable() {
        Listeners.unregister(protocolManager);

        exemptionManager.close();
        violationManager.close();
        checkManager.close();

        Bukkit.getOnlinePlayers().forEach(DataUtility::removeAll);
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
