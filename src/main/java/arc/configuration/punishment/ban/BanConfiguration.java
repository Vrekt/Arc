package arc.configuration.punishment.ban;

import org.bukkit.BanList;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * The ban configuration
 */
public final class BanConfiguration {

    /**
     * The various messages.
     */
    private final String banMessage, banBroadcastMessage;

    /**
     * The delay and length
     */
    private final int banDelay, banLength;

    /**
     * The ban type
     */
    private final BanList.Type banType;

    /**
     * If the ban should be broadcasted.
     */
    private final boolean broadcastBan;

    /**
     * Initialize
     *
     * @param configuration the configuration
     */
    public BanConfiguration(FileConfiguration configuration) {
        banMessage = configuration.getString("ban-message");
        banBroadcastMessage = configuration.getString("broadcast-ban-message");

        banDelay = configuration.getInt("ban-delay");
        banLength = configuration.getInt("ban-length");

        banType = BanList.Type.valueOf(configuration.getString("ban-type"));
        broadcastBan = configuration.getBoolean("broadcast-ban");
    }

}
