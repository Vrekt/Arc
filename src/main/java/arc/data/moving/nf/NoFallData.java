package arc.data.moving.nf;

import org.bukkit.Location;

/**
 * Stores no fall data
 */
public final class NoFallData {

    /**
     * The location for damage calculations.
     */
    private Location location, lastCheckLocation;

    /**
     * If we have failed
     */
    private boolean hasFailed;

    /**
     * The last time NoFall was checked
     */
    private long lastCheck;

    public Location location() {
        return location;
    }

    public void location(Location location) {
        this.location = location;
    }

    public Location lastCheckLocation() {
        return lastCheckLocation;
    }

    public void lastCheckLocation(Location lastCheckLocation) {
        this.lastCheckLocation = lastCheckLocation;
    }

    public boolean hasFailed() {
        return hasFailed;
    }

    public void hasFailed(boolean hasFailed) {
        this.hasFailed = hasFailed;
    }

    public long lastCheck() {
        return lastCheck;
    }

    public void lastCheck(long lastCheck) {
        this.lastCheck = lastCheck;
    }

    /**
     * Reset this data
     */
    public void reset() {
        location = null;
        hasFailed = false;
    }

}
