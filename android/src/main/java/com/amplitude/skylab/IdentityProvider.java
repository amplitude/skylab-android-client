package com.amplitude.skylab;

/**
 *  Classes can implement this interface to provide a User ID and Device ID to the
 *  {@link SkylabUser} context object. This allows for connecting with Amplitude's
 *  identity system.
 *
 *  Also see {@link SkylabClient#setIdentityProvider(IdentityProvider)}
 */
public interface IdentityProvider {

    public String getUserId();

    public String getDeviceId();

}
