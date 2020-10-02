package com.amplitude.skylab;

import android.text.TextUtils;

public class Utils {

    static String normalizeInstanceName(String instance) {
        if (TextUtils.isEmpty(instance)) {
            instance = Skylab.DEFAULT_INSTANCE;
        }
        return instance.toLowerCase();
    }
}
