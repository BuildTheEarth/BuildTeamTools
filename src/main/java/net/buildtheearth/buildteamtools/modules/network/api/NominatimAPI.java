package net.buildtheearth.buildteamtools.modules.network.api;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.model.GeographicalCoordinate;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class NominatimAPI {
    private static final String BASE_URL = "https://nominatim.openstreetmap.org/";
    private static final AsyncRequestQueue REQUEST_QUEUE =
            new AsyncRequestQueue(1_100, "nominatim-api", 500);

    public static @NotNull CompletableFuture<String> getAddressFromCoordinatesAsync(
            @NotNull GeographicalCoordinate coordinates,
            boolean international
    ) {
        StringBuilder urlBuilder = new StringBuilder(
                BASE_URL + "reverse?format=jsonv2"
                        + "&lat=" + coordinates.latitude()
                        + "&lon=" + coordinates.longitude()
                        + "&addressdetails=1"
        );

        if (international) {
            urlBuilder.append("&accept-language=en");
        }

        final String url = urlBuilder.toString();

        return REQUEST_QUEUE.submit(() -> {
            CompletableFuture<String> future = new CompletableFuture<>();

            API.getAsync(url, new API.ApiResponseCallback() {
                @Override
                public void onResponse(String response) {
                    try {
                        String address = formatAddressFromResponse(response);
                        future.complete(address);
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                }

                @Override
                public void onFailure(IOException e) {
                    future.completeExceptionally(e);
                }
            });

            return future;
        });
    }


    private static String formatAddressFromResponse(String response) {
        JSONObject jsonObject = API.createJSONObject(response);

        ChatHelper.logDebug(
                "Response from Nominatim: %s",
                jsonObject
        );

        Object displayName = jsonObject.get("display_name");

        if (displayName == null || displayName.toString().isBlank()) {
            ChatHelper.logError(
                    "No address data found for these coordinates."
            );
            return "";
        }

        return displayName.toString();
    }

    public static void shutdownQueue() {
        REQUEST_QUEUE.close();
    }
}
