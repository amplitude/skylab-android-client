package com.amplitude.skylab;

public interface Storage {

    public String put(String key, String value);

    public String get(String key);

    public void clear();

}
