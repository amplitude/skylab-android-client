package com.amplitude.skylab;

public class Utils {

    public static boolean isEmptyString(String s) {
        return (s == null || s.length() == 0);
    }

    static String normalizeInstanceName(String instance) {
        if (isEmptyString(instance)) {
            instance = Skylab.DEFAULT_INSTANCE;
        }
        return instance.toLowerCase();
    }
}
