package arc;

import arc.check.CheckManager;
import arc.command.ArcCommand;
import arc.configuration.ArcConfiguration;
import arc.data.Data;
import arc.data.moving.MovingData;
import arc.exemption.ExemptionManager;
import arc.listener.block.BlockActionListener;
import arc.listener.combat.CombatPacketListener;
import arc.listener.connection.PlayerConnectionListener;
import arc.listener.moving.MovingEventListener;
import arc.listener.moving.MovingPacketListener;
import arc.listener.player.PlayerListener;
import arc.punishment.PunishmentManager;
import arc.utility.MovingUtil;
import arc.violation.ViolationManager;
import bridge.Bridge;
import bridge.Version;
import bridge1_12.Bridge1_12;
import bridge1_16.Bridge1_16;
import bridge1_8.Bridge1_8;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main entry point for Arc.
 * ....
 */
public final class Arc extends JavaPlugin {

    /**
     * The version of Arc.
     */
    public static final String VERSION_STRING = "2.3.3";

    /**
     * If sync events should be used.
     */
    private static boolean useSyncEvents;

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
     * Punishment manager.
     */
    private final PunishmentManager punishmentManager = new PunishmentManager();

    /**
     * The protocol manager.
     */
    private ProtocolManager protocolManager;

    /**
     * If the version is incompatible
     */
    private boolean incompatible;

    @Override
    public void onEnable() {
        arc = this;

        getLogger().info(ChatColor.RED + "This version of Arc is experimental!");
        getLogger().info(ChatColor.RED + "Expect random log messages, errors, crashes, and console spam.");
        getLogger().info(ChatColor.RED + "Please report any issues to GitHub.");

        getLogger().info("Checking server version...");
        if (!loadCompatibleVersions()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Reading configuration...");
        saveDefaultConfig();
        arcConfiguration.read(getConfig());
        getLogger().info("Debug state is: " + arcConfiguration.enableDebugMessages());

        getLogger().info("Registering checks and listeners...");
        loadExternalPlugins();

        checkManager.initialize();
        violationManager.initialize(arcConfiguration, punishmentManager);
        punishmentManager.initialize(arcConfiguration);
        exemptionManager.initialize(arcConfiguration);

        loadOnlinePlayers();
        registerListeners();

        getLogger().info("Registering base command...");
        verifyCommand();

        getLogger().info("Saving configuration...");
        saveConfig();

        getLogger().info("Ready!");
    }

    @Override
    public void onDisable() {
        if (incompatible) return;

        getLogger().info("Saving file configuration...");
        saveConfig();

        getLogger().info("Removing packet listeners and closing managers...");
        unregisterListeners();

        exemptionManager.close();
        violationManager.close();
        checkManager.close();
        punishmentManager.close();

        getLogger().info("Removing player data...");
        Bukkit.getOnlinePlayers().forEach(Data::removeAll);
        arc = null;

        getLogger().info("Goodbye.");
    }

    /**
     * Register all listeners
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new MovingEventListener(), this);
        getServer().getPluginManager().registerEvents(new BlockActionListener(), this);

        new MovingPacketListener().register(protocolManager);
        new CombatPacketListener().register(protocolManager);
    }

    /**
     * Unregister listeners
     */
    private void unregisterListeners() {
        protocolManager.removePacketListeners(this);
    }

    /**
     * Load external plugins.
     * TODO: Other plugin support like bans, etc.
     */
    private void loadExternalPlugins() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    /**
     * Verify the command /arc exists.
     */
    private void verifyCommand() {
        final PluginCommand command = getCommand("arc");
        if (command == null) {
            getLogger().severe("/arc command not found! You will not be able to use this command.");
        } else {
            command.setExecutor(new ArcCommand());
        }
    }

    /**
     * Load compatible versions
     */
    private boolean loadCompatibleVersions() {
        version = Version.isCompatible(Bukkit.getVersion());

        if (version == null) {
            getLogger().severe("Arc is not compatible with this version: " + Bukkit.getVersion());
            incompatible = true;
            return false;
        } else {
            switch (version) {
                case VERSION_1_8:
                    loadFor1_8();
                    break;
                case VERSION_1_12:
                    loadFor1_12();
                    break;
                case VERSION_1_16:
                    loadFor1_16();
                    break;
            }

            useSyncEvents = version.isNewerThan(Version.VERSION_1_8);
            getLogger().info("Initialized Arc for: " + Bukkit.getVersion());
            return true;
        }
    }

    /**
     * Load arc for 1.8.8
     */
    private void loadFor1_8() {
        version = Version.VERSION_1_8;
        bridge = new Bridge1_8();
    }

    /**
     * Load arc for 1.12
     */
    private void loadFor1_12() {
        version = Version.VERSION_1_12;
        bridge = new Bridge1_12();
    }

    /**
     * Load arc for 1.16
     */
    private void loadFor1_16() {
        version = Version.VERSION_1_16;
        bridge = new Bridge1_16();
    }

    /**
     * Load players who are online already.
     */
    private void loadOnlinePlayers() {
        Bukkit.getOnlinePlayers()
                .forEach(player -> {
                    violationManager.onPlayerJoin(player);
                    exemptionManager.onPlayerJoin(player);

                    // calculate initial data.
                    final MovingData data = MovingData.get(player);
                    MovingUtil.calculateMovement(data, player.getLocation(), player.getLocation());
                });
    }

    /**
     * @return the internal plugin
     */
    public static JavaPlugin getPlugin() {
        return arc;
    }

    /**
     * @return arc
     */
    public static Arc getInstance() {
        return arc;
    }

    /**
     * @return the MC version.
     */
    public static Version getMCVersion() {
        return version;
    }

    /**
     * @return the bridge
     */
    public static Bridge getBridge() {
        return bridge;
    }

    /**
     * @return the configuration
     */
    public ArcConfiguration getArcConfiguration() {
        return arcConfiguration;
    }

    /**
     * @return the violation manager
     */
    public ViolationManager getViolationManager() {
        return violationManager;
    }

    /**
     * @return the exemption manager
     */
    public ExemptionManager getExemptionManager() {
        return exemptionManager;
    }

    /**
     * @return the punishment manager
     */
    public PunishmentManager getPunishmentManager() {
        return punishmentManager;
    }

    /**
     * @return the check manager
     */
    public CheckManager getCheckManager() {
        return checkManager;
    }

    /**
     * @return the protocol manager
     */
    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    /**
     * Trigger a bukkit event sync.
     * TODO: Could cause a problem with a-lot of events/violations/exemptions?
     * TODO: Maybe move it into a queue with a thread constantly processing them.
     *
     * @param event the event
     */
    public static void triggerEvent(Event event) {
        if (useSyncEvents) {
            arc.getServer().getScheduler().runTask(Arc.arc, () -> arc.getServer().getPluginManager().callEvent(event));
        } else {
            arc.getServer().getPluginManager().callEvent(event);
        }
    }

}
