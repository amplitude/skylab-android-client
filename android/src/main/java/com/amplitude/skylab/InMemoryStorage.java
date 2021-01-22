package com.amplitude.skylab;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage implements Storage {

    private final Map<String, Variant> data = new ConcurrentHashMap<>();

    @Override
    public Variant put(String key, Variant value) {
        if (key != null) {
            return data.put(key, value);
        }
        return null;
    }

    @Override
    public Variant get(String key) {
        if (key != null) {
            return data.get(key);
        }
        return null;
    }

    @Override
    public Map<String, Variant> getAll() {
        return new HashMap<>(data);
    }

    @Override
    public void clear() {
        data.clear();
    }
}
