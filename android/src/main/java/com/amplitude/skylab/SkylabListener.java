package com.amplitude.skylab;

import java.util.Map;

public interface SkylabListener {

    public void onVariantsChanged(SkylabUser skylabUser, Map<String, String> variants);

}
