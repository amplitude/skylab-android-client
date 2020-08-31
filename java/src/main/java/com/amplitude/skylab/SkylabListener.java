package com.amplitude.skylab;

import java.util.Map;

public interface SkylabListener {

    public void onVariantsChanged(SkylabContext context, Map<String, String> variants);

}
