package com.amplitude.skylab;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

public class SkylabClientImpl implements SkylabClient {
    private static final int BASE_64_DEFAULT_FLAGS = Base64.NO_WRAP | Base64.URL_SAFE;
    static final Object STORAGE_LOCK = new Object();
    OkHttpClient httpClient;
    String apiKey;
    SkylabConfig config;
    Storage storage;
    HttpUrl serverUrl;
    ScheduledThreadPoolExecutor executorService;
    Runnable pollTask = new Runnable() {
        @Override
        public void run() {
            fetchAll();
        }
    };
    ScheduledFuture<?> pollFuture;
    SkylabListener skylabListener;
    SkylabContext context;

    String userId;

    SkylabClientImpl(String apiKey, SkylabConfig config, Storage storage, OkHttpClient httpClient,
                     ScheduledThreadPoolExecutor executorService) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            // guarantee that apiKey exists
            throw new IllegalArgumentException("SkylabClient initialized with null or empty " +
                    "apiKey.");
        }
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.executorService = executorService;
        this.storage = storage;
        this.config = config;
        this.pollFuture = null;
        this.context = null;
        serverUrl = HttpUrl.parse(config.getServerUrl());
    }

    @Override
    public Future<SkylabClient> start(SkylabContext context) {
        this.context = context;
        return fetchAll();
    }

    @Override
    public Future<SkylabClient> start(final SkylabContext.Provider provider) {
        Callable<SkylabClient> callable = new Callable<SkylabClient>() {

            @Override
            public SkylabClient call() throws Exception {
                SkylabClientImpl.this.context = provider.get();
                return fetchAll().get();
            }
        };
        return executorService.submit(callable);
    }

    @Override
    public void start(SkylabContext context, long timeoutMs) {
        try {
            Future<SkylabClient> future = start(context);
            future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            Log.i(Skylab.TAG, "Timeout while initializing client, evaluations may not be " +
                    "ready");
        } catch (Exception e) {
            Log.w(Skylab.TAG, "Exception in client initialization", e);
        }
    }

    @Override
    public Future<SkylabClient> setContext(SkylabContext context) {
        final AsyncFuture<SkylabClient> future = new AsyncFuture<>();
        if ((this.context == null && context == null) || (this.context != null && this.context.equals(context))) {
            future.complete(this);
            return future;
        } else {
            this.context = context;
            storage.clear();
            return this.start(context);
        }
    }

    @Override
    public SkylabClient startPolling() {
        if (pollFuture == null) {
            pollFuture = executorService.scheduleAtFixedRate(pollTask,
                    config.getPollIntervalSecs(), config.getPollIntervalSecs(), TimeUnit.SECONDS);
        }
        return this;
    }

    @Override
    public SkylabClient stopPolling() {
        if (pollFuture != null) {
            pollFuture.cancel(false);
            executorService.purge();
        }
        return this;
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
        final JSONObject jsonContext = (context != null) ? context.toJSONObject() :
                new JSONObject();
        final String jsonString = jsonContext.toString();
        final byte[] srcData = jsonString.getBytes(Charset.forName("UTF-8"));
        final String base64Encoded = Base64.encodeToString(srcData, BASE_64_DEFAULT_FLAGS);
        final HttpUrl url =
                serverUrl.newBuilder().addPathSegments("sdk/variants/" + base64Encoded).build();
        Log.d(Skylab.TAG, "Requesting variants from " + url.toString() + " for context " + jsonContext.toString());
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
                        synchronized (STORAGE_LOCK) {
                            Map<String, String> oldValues = storage.getAll();
                            storage.clear();
                            JSONObject result = new JSONObject(responseString);
                            Map<String, String> changed = new HashMap<>();

                            // add any flags that disappeared
                            for (String flag : oldValues.keySet()) {
                                if (!result.has(flag) && !Skylab.DEFAULT_VARIANT.equals(oldValues.get(flag))) {
                                    changed.put(flag, Skylab.DEFAULT_VARIANT);
                                }
                            }

                            Iterator<String> flags = result.keys();
                            while (flags.hasNext()) {
                                String flag = flags.next();
                                String newValue = result.getString(flag);
                                storage.put(flag, newValue);
                                String oldValue = oldValues.get(flag);
                                if (!newValue.equals(oldValue)) {
                                    changed.put(flag, newValue);
                                }
                            }
                            fireOnVariantReceived(context, changed);
                        }
                    } else {
                        Log.w(Skylab.TAG,responseString);
                    }
                } catch (JSONException e) {
                    Log.e(Skylab.TAG, "Could not parse JSON: " + responseString);
                    Log.e(Skylab.TAG, e.getMessage(), e);
                } catch (Exception e) {
                    Log.e(Skylab.TAG,"Exception handling response: " + responseString);
                    Log.e(Skylab.TAG, e.getMessage(), e);
                } finally {
                    response.close();
                }
                future.complete(SkylabClientImpl.this);
                long end = System.nanoTime();
                Log.d(Skylab.TAG, String.format("Fetched all: %s for context %s in %.3f ms",
                        responseString, jsonContext.toString(), (end - start) / 1000000.0));
            }
        });
        return future;
    }

    /**
     * Fetches the variant for the given flagKey from local storage. If no such flag
     * is found, returns Skylab.DEFAULT_VARIANT ("false").
     *
     * @param flagKey
     * @return
     */
    @Override
    public String getVariant(String flagKey) {
        return getVariant(flagKey, Skylab.DEFAULT_VARIANT);
    }

    /**
     * Fetches the variant for the given flagKey from local storage. If no such flag
     * is found, returns fallback.
     *
     * @param flagKey
     * @return
     */
    @Override
    public String getVariant(String flagKey, String fallback) {
        long start = System.nanoTime();
        String variant;
        synchronized (STORAGE_LOCK) {
            variant = storage.get(flagKey);
        }
        if (variant == null) {
            variant = fallback;
        }
        long end = System.nanoTime();
        Log.d(Skylab.TAG, String.format("Fetched %s in %.3f ms", flagKey, (end - start) / 1000000.0));
        return variant;
    }

    @Override
    public SkylabClient setListener(SkylabListener skylabListener) {
        this.skylabListener = skylabListener;
        return this;
    }

    private void fireOnVariantReceived(SkylabContext context, Map<String, String> variants) {
        if (this.skylabListener != null) {
            this.skylabListener.onVariantsChanged(context, variants);
        }
    }

}
