package com.amplitude.skylab;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Default {@link SkylabClient} implementation. Use the {@link Skylab} class to initialize and
 * access
 * instances.
 */
public class DefaultSkylabClient implements SkylabClient {
    public static final String LIBRARY = "skylab-android/" + BuildConfig.VERSION_NAME;

    private static final int BASE_64_DEFAULT_FLAGS =
            Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE;
    static final Object STORAGE_LOCK = new Object();

    String instanceName;
    Application application;
    String apiKey;
    String enrollmentId;
    SkylabConfig config;
    HttpUrl serverUrl;

    SkylabUser skylabUser;
    SkylabListener skylabListener;
    ContextProvider contextProvider;

    Storage storage;
    OkHttpClient httpClient;
    ScheduledThreadPoolExecutor executorService;
    ScheduledFuture<?> pollFuture;

    Runnable pollTask = new Runnable() {
        @Override
        public void run() {
            fetchAll();
        }
    };

    Callable<SkylabClient> fetchAllCallable = new Callable<SkylabClient>() {
        @Override
        public SkylabClient call() throws Exception {
            return fetchAll().get();
        }
    };

    DefaultSkylabClient(Application application, String apiKey,
                        SkylabConfig config, Storage storage
            , OkHttpClient httpClient,
                        ScheduledThreadPoolExecutor executorService) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            // guarantee that apiKey exists
            throw new IllegalArgumentException("SkylabClient initialized with null or empty " +
                    "apiKey.");
        }
        this.instanceName = config.getInstanceName();
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.executorService = executorService;
        this.storage = storage;
        this.config = config;
        this.pollFuture = null;
        this.application = application;
        serverUrl = HttpUrl.parse(config.getServerUrl());
    }

    /**
     * See {@link SkylabClient#start(SkylabUser)}
     */
    @Override
    public Future<SkylabClient> start(SkylabUser skylabUser) {
        loadFromStorage();
        this.skylabUser = skylabUser;
        return executorService.submit(fetchAllCallable);
    }

    /**
     * See {@link SkylabClient#start(SkylabUser, long)}
     */
    @Override
    public void start(SkylabUser skylabUser, long timeoutMs) {
        try {
            Future<SkylabClient> future = start(skylabUser);
            future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            Log.i(Skylab.TAG, "Timeout while initializing client, evaluations may not be " +
                    "ready");
        } catch (Exception e) {
            Log.w(Skylab.TAG, "Exception in client initialization", e);
        }
    }

    public String loadFromStorage() {
        // Use a common SharedPreferences across all instances for the enrollment ID
        SharedPreferences sharedPreferences =
                application.getSharedPreferences(SkylabConfig.SHARED_PREFS_SHARED_NAME,
                        Context.MODE_PRIVATE);
        enrollmentId = sharedPreferences.getString(SkylabConfig.ENROLLMENT_ID_KEY, null);
        if (enrollmentId == null) {
            Log.i(Skylab.TAG, "Creating new id for enrollment");
            enrollmentId = generateEnrollmentId();
            sharedPreferences.edit().putString(SkylabConfig.ENROLLMENT_ID_KEY, enrollmentId).apply();
        }
        return enrollmentId;
    }

    private static String generateEnrollmentId() {
        return uuidToBase36(UUID.randomUUID());
    }

    private static String uuidToBase36(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return new BigInteger(1, bb.array()).toString(36);
    }

    /**
     * See {@link SkylabClient#setUser}
     */
    @Override
    public Future<SkylabClient> setUser(SkylabUser skylabUser) {
        if ((this.skylabUser == null && skylabUser == null) || (this.skylabUser != null && !this.skylabUser.equals(skylabUser))) {
            final AsyncFuture<SkylabClient> future = new AsyncFuture<>();
            future.complete(this);
            return future;
        } else {
            this.skylabUser = skylabUser;
            storage.clear();
            return executorService.submit(fetchAllCallable);
        }
    }

    /**
     * See {@link SkylabClient#getUser}
     * @return
     */
    @Override
    public SkylabUser getUser() {
        return this.skylabUser;
    }

    /**
     * See {@link SkylabClient#getUserWithContext}
     * @return
     */
    @Override
    public SkylabUser getUserWithContext() {
        SkylabUser.Builder builder = SkylabUser.builder();
        if (contextProvider != null) {
            String deviceId = contextProvider.getDeviceId();
            if (!TextUtils.isEmpty(deviceId)) {
                builder.setDeviceId(deviceId);
            }
            String userId = contextProvider.getUserId();
            if (!TextUtils.isEmpty(userId)) {
                builder.setUserId(userId);
            }
            builder.setPlatform(contextProvider.getPlatform());
            builder.setVersion(contextProvider.getVersion());
            builder.setLanguage(contextProvider.getLanguage());
            builder.setOs(contextProvider.getOs());
            builder.setDeviceBrand(contextProvider.getBrand());
            builder.setDeviceManufacturer(contextProvider.getManufacturer());
            builder.setDeviceModel(contextProvider.getModel());
            builder.setCarrier(contextProvider.getCarrier());
        }
        builder.setLibrary(LIBRARY);
        builder.copyUser(this.skylabUser);
        return builder.build();
    }

    @Override
    public SkylabClient startPolling() {
        if (pollFuture == null) {
            Log.d(Skylab.TAG, "Starting polling every " + config.getPollIntervalSecs() + " " +
                    "seconds");
            pollFuture = executorService.scheduleAtFixedRate(pollTask,
                    config.getPollIntervalSecs(), config.getPollIntervalSecs(), TimeUnit.SECONDS);
        }
        return this;
    }

    @Override
    public SkylabClient stopPolling() {
        if (pollFuture != null) {
            Log.d(Skylab.TAG, "Stopping polling");
            pollFuture.cancel(false);
            executorService.purge();
            this.pollFuture = null;
        }
        return this;
    }

    public Future<SkylabClient> refetchAll() {

        return executorService.submit(fetchAllCallable);
    }

    /**
     * Fetches all variants and returns a future that will complete when the network call is
     * complete.
     *
     * @return
     */
    Future<SkylabClient> fetchAll() {
        final AsyncFuture<SkylabClient> future = new AsyncFuture<>();
        final long start = System.nanoTime();
        final SkylabUser user = getUserWithContext();
        if (user.userId == null && user.deviceId == null) {
            Log.w(Skylab.TAG, "user id and device id are null; amplitude will not be able to resolve identity");
        }
        final JSONObject jsonContext = user.toJSONObject();
        final String jsonString = jsonContext.toString();
        final byte[] srcData = jsonString.getBytes(Charset.forName("UTF-8"));
        final String base64Encoded = Base64.encodeToString(srcData, BASE_64_DEFAULT_FLAGS);
        final HttpUrl url =
                serverUrl.newBuilder().addPathSegments("sdk/vardata/" + base64Encoded).build();
        Log.d(Skylab.TAG,
                "Requesting variants from " + url.toString() + " for user " + jsonContext.toString());
        Request request = new Request.Builder().url(url).addHeader("Authorization",
                "Api-Key " + this.apiKey)
                .get().build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(Skylab.TAG, e.getMessage(), e);
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                try {
                    if (response.isSuccessful()) {
                        Map<String, Variant> newValues = new HashMap<>();
                        synchronized (STORAGE_LOCK) {
                            storage.clear();
                            JSONObject result = new JSONObject(responseString);
                            Iterator<String> flags = result.keys();
                            while (flags.hasNext()) {
                                String flag = flags.next();

                                JSONObject variantJsonObj = result.getJSONObject(flag);
                                Variant newValue = Variant.fromJsonObject(variantJsonObj);
                                storage.put(flag, newValue);
                                newValues.put(flag, newValue);
                            }
                        }
                        fireOnVariantsReceived(skylabUser, newValues);
                    } else {
                        Log.w(Skylab.TAG, responseString);
                    }
                } catch (JSONException e) {
                    Log.e(Skylab.TAG, "Could not parse JSON: " + responseString);
                    Log.e(Skylab.TAG, e.getMessage(), e);
                } catch (Exception e) {
                    Log.e(Skylab.TAG, "Exception handling response: " + responseString);
                    Log.e(Skylab.TAG, e.getMessage(), e);
                } finally {
                    response.close();
                }
                future.complete(DefaultSkylabClient.this);
                long end = System.nanoTime();
                Log.d(Skylab.TAG, String.format("Fetched all: %s for user %s in %.3f ms",
                        responseString, jsonContext.toString(), (end - start) / 1000000.0));
            }
        });
        return future;
    }


    private JSONObject addContext(SkylabUser skylabUser) {
        return getUserWithContext().toJSONObject();
    }

    /**
     * Fetches the variant for the given flagKey from local storage. If no such flag
     * is found, returns the fallback variant set in the {@link SkylabConfig}.
     *
     * @param flagKey
     * @return
     */
    @Override
    public Variant getVariant(String flagKey) {
        return getVariant(flagKey, config.getFallbackVariant());
    }

    /**
     * Fetches the variant for the given flagKey from local storage. If no such flag
     * is found, returns fallback.
     *
     * @param flagKey
     * @return
     */
    @Override
    public Variant getVariant(String flagKey, Variant fallback) {
        Variant variant;
        synchronized (STORAGE_LOCK) {
            variant = storage.get(flagKey);
        }
        if (variant == null || variant.value() == null) {
            variant = fallback;
            Log.d(Skylab.TAG, String.format("Variant for %s not found, returning fallback variant" +
                    " %s", flagKey, variant));
        }
        return variant;
    }

    /**
     * Fetches all variants from local storage.
     * @return
     */
    public Map<String, Variant> getVariants() {
        synchronized (STORAGE_LOCK) {
            return storage.getAll();
        }
    }

    @Override
    public SkylabClient setContextProvider(ContextProvider provider) {
        this.contextProvider = provider;
        return this;
    }

    @Override
    public SkylabClient setListener(SkylabListener skylabListener) {
        this.skylabListener = skylabListener;
        return this;
    }

    private void fireOnVariantsReceived(SkylabUser skylabUser, Map<String, Variant> variants) {
        if (this.skylabListener != null) {
            this.skylabListener.onVariantsChanged(skylabUser, variants);
        }
    }

}
