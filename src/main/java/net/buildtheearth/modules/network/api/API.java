package net.buildtheearth.modules.network.api;

import net.buildtheearth.Main;
import net.buildtheearth.modules.utils.ChatHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
                } else if(Main.getBuildTeamTools().isDebug())
                    callback.onFailure(new IOException("\nUnexpected code: \n" + response + "\n Response Body:\n" + response.body().string()));
                else
                    callback.onFailure(new IOException("Unexpected code " + response.code()));
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
                } else if(Main.getBuildTeamTools().isDebug()) {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);
                    callback.onFailure(new IOException("\nUnexpected code: \n" + response + "\n Request Body:\n" + buffer.readUtf8() +"\n Response Body:\n" + response.body().string()));
                }else {
                    callback.onFailure(new IOException("Unexpected code " + response.code()));
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
                } else if(Main.getBuildTeamTools().isDebug()) {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);
                    callback.onFailure(new IOException("\nUnexpected code: \n" + response + "\n Request Body:\n" + buffer.readUtf8() +"\n Response Body:\n" + response.body().string()));
                }else
                    callback.onFailure(new IOException("Unexpected code: " + response.code()));
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }
        });
    }

    /**
     * Sends a delete request to an API with a request body
     * @param url The URL to perform a delete request on
     * @param requestBody The request body to include in the delete request
     * @return The result of the delete request as a string
     * @throws IOException When something went wrong performing the delete request
     */
    public static String delete(String url, RequestBody requestBody) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .delete(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * Sends an asynchronous delete request to an API with a request body
     * @param url The URL to perform a delete request on
     * @param requestBody The request body to include in the delete request
     * @param callback The callback to handle the API response or failure
     */
    public static void deleteAsync(String url, RequestBody requestBody, ApiResponseCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .delete(requestBody)
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



    public static JSONArray createJSONArray(String jsonString) {
        JSONArray jsonArray = new JSONArray();
        JSONParser jsonParser = new JSONParser();
        if ((jsonString != null) && !(jsonString.isEmpty())) {
            try {
                jsonArray = (JSONArray) jsonParser.parse(jsonString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    public static JSONObject createJSONObject(String jsonString) {
        JSONObject jsonObject = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        if ((jsonString != null) && !(jsonString.isEmpty())) {
            try {
                jsonObject = (JSONObject) jsonParser.parse(jsonString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

}
