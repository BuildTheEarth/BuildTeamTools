package net.buildtheearth.modules.network.api;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

/**
 * A helper class to allow for smoother API usage
 */
public class API {

    private static OkHttpClient httpClient = new OkHttpClient();

    /**
     * A callback function to be able to handle the result of an API request
     */
    public interface ApiResponseCallback {
        void onResponse(String response);

        void onFailure(IOException e);
    }

    /**
     * Sends a get request to an API
     * @param url The URL to perform a get request on
     * @return The result of the get request as a string
     * @throws IOException When something went wrong performing the get request
     */
    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * Sends an asynchronous get request to an API
     * @param url The URL to perform a get request on
     * @return The result of the get request as a string
     * @throws IOException When something went wrong performing the get request
     */
    public static void getAsync(String url, ApiResponseCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    callback.onResponse(responseBody);
                } else {
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }
        });
    }


    /**
     * Sends a put request to an API
     * @param url The URL to perform a put request on
     * @param requestBody The request body to include in the put request
     * @return The result of the put request as a string
     * @throws IOException When something went wrong performing the put request
     */
    public static String put(String url, RequestBody requestBody) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * Sends an asynchronous put request to an API
     * @param url The URL to perform a put request on
     * @param requestBody The request body to include in the put request
     * @param callback The callback to handle the API response or failure
     */
    public static void putAsync(String url, RequestBody requestBody, ApiResponseCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    callback.onResponse(responseBody);
                } else {
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }
        });
    }

    /**
     * Sends a post request to an API
     * @param url The URL to perform a post request on
     * @param requestBody The request body to include in the post request
     * @return The result of the post request as a string
     * @throws IOException When something went wrong performing the post request
     */
    public static String post(String url, RequestBody requestBody) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * Sends an asynchronous post request to an API
     * @param url The URL to perform a post request on
     * @param requestBody The request body to include in the post request
     * @param callback The callback to handle the API response or failure
     */
    public static void postAsync(String url, RequestBody requestBody, ApiResponseCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    callback.onResponse(responseBody);
                } else {
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }
        });
    }
}
