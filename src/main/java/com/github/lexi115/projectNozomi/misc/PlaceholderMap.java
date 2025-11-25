package com.github.lexi115.projectNozomi.misc;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderMap {

    private final Map<String, String> map;

    public PlaceholderMap() {
        map = new HashMap<>();
    }

    public Map<String, String> map() {
        return map;
    }

    public String set(final @NonNull String key) {
        return map.get(key);
    }

    public void unset(final @NonNull String key) {
        map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    public PlaceholderMap set(final @NonNull String key, final Object value) {
        map.put(key, value != null ? value.toString() : null);
        return this;
    }
}
