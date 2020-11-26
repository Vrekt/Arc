package arc.utility;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows a hashmap to be built.
 */
public class MapBuilder {

    /**
     * The map
     */
    private final Map<String, String> map = new HashMap<>();

    public MapBuilder() {
    }

    public MapBuilder(String key, String value) {
        map.put(key, value);
    }

    /**
     * Add a pair of key and value
     *
     * @param key   K
     * @param value V
     * @return this
     */
    public MapBuilder pair(String key, String value) {
        map.put(key, value);
        return this;
    }

    /**
     * @return the internal map
     */
    public Map<String, String> build() {
        return map;
    }

}
