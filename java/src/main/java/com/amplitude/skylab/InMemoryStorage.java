package com.amplitude.skylab;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage implements Storage {

    private final Map<String, String> data = new ConcurrentHashMap<>();

    @Override
    public String put(String key, String value) {
        if (key != null) {
            return data.put(key, value);
        }
        return null;
    }

    @Override
    public String get(String key) {
        if (key != null) {
            return data.get(key);
        }
        return null;
    }

    @Override
    public void clear() {
        data.clear();
    }
}
