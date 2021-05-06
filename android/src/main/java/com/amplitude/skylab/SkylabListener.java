package com.amplitude.skylab;

import java.util.Map;

/**
 * Classes can implement this interface to handle changes in assigned variants for a user.
 * The {@link #onVariantsChanged(SkylabUser, Map)} method is called when a
 * {@link SkylabClient} fetches variant values that are different from previously fetched
 * values.
 */
public interface SkylabListener {

    public void onVariantsChanged(SkylabUser skylabUser, Map<String, Variant> variants);

}
