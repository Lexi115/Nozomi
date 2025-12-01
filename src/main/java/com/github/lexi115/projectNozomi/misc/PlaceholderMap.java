package com.github.lexi115.projectNozomi.misc;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A map of placeholders (special words inside of strings meant to be replaced with some other value, often generated
 * dynamically).
 *
 * @author Lexi115
 * @since 1.0
 */
public class PlaceholderMap {

    /**
     * The actual internal map.
     */
    private final Map<String, String> map;

    /**
     * Constructor.
     *
     * @since 1.0
     */
    public PlaceholderMap() {
        map = new HashMap<>();
    }

    /**
     * Returns the actual map.
     *
     * @return The map.
     * @since 1.0
     */
    public Map<String, String> map() {
        return map;
    }

    /**
     * Gets a placeholder value.
     *
     * @param key The placeholder name.
     * @return The corresponding value.
     * @since 1.0
     */
    public String get(final @NonNull String key) {
        return map.get(key);
    }

    /**
     * Sets a placeholder.
     *
     * @param key   The placeholder name (acting as a key inside the map).
     * @param value The placeholder value.
     * @return <code>this</code> object, to easily concatenate multiple operations.
     * @since 1.0
     */
    public PlaceholderMap set(final @NonNull String key, final Object value) {
        map.put(key, value != null ? value.toString() : null);
        return this;
    }

    /**
     * Unsets a placeholder.
     *
     * @param key The placeholder name.
     * @return <code>this</code> object, to easily concatenate multiple operations.
     * @since 1.0
     */
    public PlaceholderMap unset(final @NonNull String key) {
        map.remove(key);
        return this;
    }

    /**
     * Removes every placeholder in the map.
     *
     * @return <code>this</code> object, to easily concatenate multiple operations.
     * @since 1.0
     */
    public PlaceholderMap clear() {
        map.clear();
        return this;
    }
}
