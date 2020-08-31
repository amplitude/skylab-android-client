package com.amplitude.skylab;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SkylabClientImpl implements SkylabClient {

    static final Logger LOGGER = Logger.getLogger(SkylabClientImpl.class.getName());
    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static final String FALLBACK_VARIANT = "false";
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

    SkylabClientImpl(String apiKey, SkylabConfig config, OkHttpClient httpClient,
                     ScheduledThreadPoolExecutor executorService, Storage storage) {
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
    public void start(SkylabContext context, long timeoutMs) {
        try {
            Future<SkylabClient> future = start(context);
            future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            LOGGER.log(Level.INFO, "Timeout while initializing client, evaluations may not be ready");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception in client initialization", e);
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
        final HttpUrl url = serverUrl.newBuilder().addPathSegments("sdk/variants").build();
        final JSONObject jsonContext = (context != null) ? context.toJSONObject() :
                new JSONObject();
        LOGGER.info("Requesting variants from " + url.toString() + " for context " + jsonContext.toString());
        Request request = new Request.Builder().url(url).addHeader("Api-Key", this.apiKey)
                .post(RequestBody.create(jsonContext.toString(), JSON)).build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();

                try {
                    if (response.isSuccessful()) {
                        synchronized (STORAGE_LOCK) {
                            storage.clear();
                            JSONObject result = new JSONObject(responseString);
                            for (String flag : result.keySet()) {
                                storage.put(flag, result.getString(flag));
                            }
                        }
                    } else {
                        LOGGER.warning(responseString);
                    }
                } catch (JSONException e) {
                    LOGGER.severe("Could not parse JSON: " + responseString);
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                } catch (Exception e) {
                    LOGGER.severe("Exception handling response: " + responseString);
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                } finally {
                    response.close();
                }
                future.complete(SkylabClientImpl.this);
                long end = System.nanoTime();
                LOGGER.info(String.format("Fetched all: %s for context %s in %.3f ms",
                        responseString, jsonContext.toString(), (end - start) / 1000000.0));
            }
        });
        return future;
    }

    /**
     * Fetches the variant for the given flagKey from local storage
     *
     * @param flagKey
     * @return
     */
    @Override
    public String getVariant(String flagKey) {
        return getVariant(flagKey, FALLBACK_VARIANT);
    }

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
        LOGGER.info(String.format("Fetched %s in %.3f ms", flagKey, (end - start) / 1000000.0));
        fireOnVariantReceived(userId, flagKey, variant);
        return variant;
    }

    @Override
    public SkylabClient setListener(SkylabListener skylabListener) {
        this.skylabListener = skylabListener;
        return this;
    }

    private void fireOnVariantReceived(String identity, String flagKey, String variant) {
        if (this.skylabListener != null) {
            this.skylabListener.onVariantReceived(identity, flagKey, variant);
        }
    }

}
