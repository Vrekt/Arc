package arc;

import arc.bridge.Bridge;
import arc.bridge.Version;
import arc.bridge.version1_15.Bridge1_15;
import arc.bridge.version1_16.Bridge1_16;
import arc.bridge.version1_8.Bridge1_8;
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
    public static final String VERSION_STRING = "1.0.9";

    /**
     * The file configuration
     */
    private static Arc arc;

    /**
     * The version
     */
    private static Version version;

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
     * The bridge
     */
    private Bridge bridge;

    /**
     * The protocol manager.
     */
    private ProtocolManager protocolManager;

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

        getLogger().info("[INFO] Registering base command.");
        Objects.requireNonNull(getCommand("arc")).setExecutor(new CommandArc());

        getLogger().info("[INFO] Saving configuration");
        saveConfig();

        getLogger().info("[SUCCESS] Ready!");
    }

    /**
     * TODO: Will throw exceptions when loading non-compatible versions
     */
    @Override
    public void onDisable() {
        Listeners.unregister(protocolManager);

        exemptionManager.close();
        violationManager.close();
        checkManager.close();

        Bukkit.getOnlinePlayers().forEach(DataUtility::removeAll);
    }

    /**
     * Load compatible versions
     */
    private boolean loadCompatibleVersions() {
        if (Bukkit.getVersion().contains("1.8")) {
            getLogger().info("Initializing Arc for: " + Bukkit.getVersion());
            loadFor1_8_8();
            return true;
        } else if (Bukkit.getVersion().contains("1.16")) {
            getLogger().info(ChatColor.RED + "Initializing Arc for: " + Bukkit.getVersion());
            loadFor1_16();
            return true;
        } else if(Bukkit.getVersion().contains("1.15")) {
            getLogger().info(ChatColor.RED + "Initializing Arc for: " + Bukkit.getVersion());
            loadFor1_15();
            return true;
        } else {
            // TODO: Incompatible versions
        }
        return true;
    }

    /**
     * Load arc for 1.8.8
     */
    private void loadFor1_8_8() {
        version = Version.VERSION_1_8;
        bridge = new Bridge1_8();
    }

    /**
     * Load arc for 1.15
     */
    private void loadFor1_15() {
        version = Version.VERSION_1_15;
        bridge = new Bridge1_15();
    }

    /**
     * Load arc for 1.16
     */
    private void loadFor1_16() {
        version = Version.VERSION_1_16;
        bridge = new Bridge1_16();
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
     * @return the bridge
     */
    public Bridge bridge() {
        return bridge;
    }
}
