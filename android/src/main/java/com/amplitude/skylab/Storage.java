package com.amplitude.skylab;

import java.util.Map;

public interface Storage {

    public String put(String key, String value);

    public String get(String key);

    public Map<String, String> getAll();

    public void clear();

}
