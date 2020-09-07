package com.amplitude.skylab;

import com.amplitude.util.Base64;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
import okhttp3.Response;

public class SkylabClientImpl implements SkylabClient {

    static final Logger LOGGER = Logger.getLogger(SkylabClientImpl.class.getName());
    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static final String FALLBACK_VARIANT = "false";
    OkHttpClient httpClient;
    String apiKey;
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

    String userId;

    SkylabClientImpl(String apiKey, SkylabConfig config, OkHttpClient httpClient,
                     ScheduledThreadPoolExecutor executorService, Storage storage) {
        if (apiKey == null) {
            LOGGER.warning("SkylabClient initialized with null apiKey.");
        }
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.executorService = executorService;
        this.storage = storage;
        serverUrl = HttpUrl.parse(config.getServerUrl());
    }

    @Override
    public Future<SkylabClientImpl> init() {
        return fetchAll();
    }

    @Override
    public void init(long timeoutMs) {
        try {
            Future<SkylabClientImpl> future = init();
            future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOGGER.warning("Exception in client initialization");
        }
    }

    @Override
    public SkylabClient identify(String userId) {
        this.userId = userId;
        storage.clear();
        return this;
    }

    @Override
    public SkylabClient startPolling() {
        pollFuture = executorService.scheduleAtFixedRate(pollTask, 10, 10, TimeUnit.SECONDS);
        return this;
    }

    @Override
    public SkylabClient stopPolling() {
        pollFuture.cancel(false);
        executorService.remove(pollTask);
        return this;
    }

    /**
     * Fetches all variants and returns a future that will complete when the network call is
     * complete.
     *
     * @return
     */
    Future<SkylabClientImpl> fetchAll() {
        final AsyncFuture<SkylabClientImpl> future = new AsyncFuture<>();
        if (this.apiKey == null) {
            future.complete(this);
            return future;
        }
        final long start = System.nanoTime();
        final JSONObject context = new JSONObject();
        context.put("id", userId);
        String jsonString = context.toString();

        byte[] srcData = jsonString.getBytes(Charset.forName("UTF-8"));
        String base64Encoded = new String(Base64.getEncoder().encode(srcData));
        final HttpUrl url = serverUrl.newBuilder().addPathSegments("sdk/variants/" + base64Encoded).build();
        LOGGER.info("Requesting variants from " + url.toString() + " for context " + context.toString());
        Request request = new Request.Builder().url(url).addHeader("Authorization", "Api-Key " + this.apiKey)
                .get().build();

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
                        storage.clear();
                        JSONObject jsonObj = new JSONObject(responseString);
                        JSONArray keys = jsonObj.names();
                        for (int i=0; i<keys.length(); i++) {
                            String key = keys.getString(i);
                            String value = jsonObj.getString(key);
                            storage.put(key, value);
                        }
                        LOGGER.info("Fetched all: " + jsonObj.toString());
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
                LOGGER.info(String.format("Fetched from %s for context %s in %.3f ms",
                        url.toString(), context.toString(), (end - start) / 1000000.0));
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
        if (this.apiKey == null) {
            return null;
        }
        return getVariant(flagKey, FALLBACK_VARIANT);
    }

    @Override
    public String getVariant(String flagKey, String fallback) {
        if (this.apiKey == null) {
            return null;
        }
        long start = System.nanoTime();
        String variant = storage.get(flagKey);
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
