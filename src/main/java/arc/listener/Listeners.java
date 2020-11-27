package arc.listener;

import arc.Arc;
import arc.listener.connection.ConnectionListener;
import arc.listener.moving.MovingListener;
import arc.listener.moving.tasks.MovingUpdateTask;
import arc.listener.network.CombatPacketListener;
import arc.listener.network.MovingPacketListener;
import arc.listener.player.PlayerListener;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

/**
 * Utility class to handle listeners.
 */
public final class Listeners {

    /**
     * Moving listener.
     */
    private static final MovingListener MOVING_LISTENER = new MovingListener();

    /**
     * Connection listener.
     */
    private static final ConnectionListener CONNECTION_LISTENER = new ConnectionListener();

    /**
     * Moving packets listener
     */
    private static final MovingPacketListener MOVING_PACKET_LISTENER = new MovingPacketListener();

    /**
     * Player listener
     */
    private static final PlayerListener PLAYER_LISTENER = new PlayerListener();

    /**
     * The moving update task.
     */
    private static final MovingUpdateTask MOVING_UPDATE_TASK = new MovingUpdateTask();

    /**
     * Combat listener
     */
    private static final CombatPacketListener COMBAT_PACKET_LISTENER = new CombatPacketListener();

    /**
     * Register all listeners
     *
     * @param plugin   the plugin
     * @param protocol the protocol
     */
    public static void register(Plugin plugin, ProtocolManager protocol) {
        plugin.getServer().getPluginManager().registerEvents(MOVING_LISTENER, plugin);
        plugin.getServer().getPluginManager().registerEvents(PLAYER_LISTENER, plugin);
        plugin.getServer().getPluginManager().registerEvents(CONNECTION_LISTENER, plugin);
        MOVING_PACKET_LISTENER.createPacketListeners(protocol);
        COMBAT_PACKET_LISTENER.createPacketListeners(protocol);
        MOVING_UPDATE_TASK.start();
    }

    /**
     * Unregister all listeners
     *
     * @param protocol the protocol
     */
    public static void unregister(ProtocolManager protocol) {
        HandlerList.unregisterAll(MOVING_LISTENER);
        HandlerList.unregisterAll(PLAYER_LISTENER);
        HandlerList.unregisterAll(CONNECTION_LISTENER);
        protocol.removePacketListeners(Arc.arc());
        MOVING_UPDATE_TASK.stop();
    }


}
