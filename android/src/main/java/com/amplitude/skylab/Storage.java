package com.amplitude.skylab;

import java.util.Map;

public interface Storage {

    Variant put(String key, Variant variant);

    Variant get(String key);

    Map<String, Variant> getAll();

    void clear();

}
