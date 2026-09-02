package net.buildtheearth.buildteamtools.modules.network.api;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.model.GeographicalCoordinate;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PhotonAPI {
    private static final String BASE_URL = "https://photon.komoot.io/";

    public static @NotNull CompletableFuture<String> getAddressFromCoordinatesAsync(
            @NotNull GeographicalCoordinate coordinates
    ) {
        CompletableFuture<String> future = new CompletableFuture<>();

        String url = BASE_URL + "reverse?lat=" + coordinates.latitude()
                + "&lon=" + coordinates.longitude()
                + "&lang=en";

        API.getAsync(url, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                String address = formatAddressFromResponse(response);
                future.complete(address);
            }

            @Override
            public void onFailure(IOException e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    private static String formatAddressFromResponse (String response) {
        JSONObject jsonObject = API.createJSONObject(response);

        ChatHelper.logDebug("Response from Photon: %s", jsonObject);

        JSONArray features = (JSONArray) jsonObject.get("features");

        if (features == null || features.isEmpty()) {
            ChatHelper.logError("No address data found for these coordinates.");
            return "";
        }

        JSONObject feature = (JSONObject) features.getFirst();
        JSONObject properties = (JSONObject) feature.get("properties");

        if (properties == null) {
            ChatHelper.logError("No properties found in Photon response.");
            return "";
        }

        return Stream.of(
                        properties.get("street"),
                        properties.get("city"),
                        properties.get("country")
                )
                .filter(Objects::nonNull)
                .map(Object::toString)
                .filter(value -> !value.isBlank())
                .collect(Collectors.joining(", "));
    }

    public static @NotNull CompletableFuture<GeographicalCoordinate> getCoordinatesFromAddressAsync(
            @NotNull String address,
            @NotNull String lang
    ) {
        CompletableFuture<GeographicalCoordinate> future = new CompletableFuture<>();

        String url = BASE_URL + "api/?q="
                + URLEncoder.encode(address, StandardCharsets.UTF_8)
                + "&lang="
                + lang;

        API.getAsync(url, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    GeographicalCoordinate coordinate =
                            getGeoCoordinateFromResponse(response);

                    future.complete(coordinate);
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
    }

    private static @NotNull GeographicalCoordinate getGeoCoordinateFromResponse(
            @NotNull String response
    ) {
        JSONObject jsonObject = API.createJSONObject(response);

        ChatHelper.logDebug("Response from Photon: %s", jsonObject);

        JSONArray features = (JSONArray) jsonObject.get("features");

        if (features == null || features.isEmpty()) {
            throw new IllegalArgumentException(
                    "No address data found for this address."
            );
        }

        JSONObject feature = (JSONObject) features.getFirst();

        JSONObject geometry = (JSONObject) feature.get("geometry");

        if (geometry == null) {
            throw new IllegalArgumentException(
                    "No geometry found in Photon response."
            );
        }

        JSONArray coordinates = (JSONArray) geometry.get("coordinates");

        if (coordinates == null || coordinates.size() < 2) {
            throw new IllegalArgumentException(
                    "Invalid coordinates in Photon response."
            );
        }

        double longitude = ((Number) coordinates.get(0)).doubleValue();
        double latitude = ((Number) coordinates.get(1)).doubleValue();

        return new GeographicalCoordinate(latitude, longitude);
    }
}
