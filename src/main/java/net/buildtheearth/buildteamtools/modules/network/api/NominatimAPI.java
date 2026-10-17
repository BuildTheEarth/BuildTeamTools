package net.buildtheearth.buildteamtools.modules.network.api;

import net.buildtheearth.model.GeographicalCoordinate;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class NominatimAPI {
    private static final String BASE_URL = "https://nominatim.openstreetmap.org/";

    public static @NotNull CompletableFuture<String> getAddressFromCoordinatesAsync(
            @NotNull GeographicalCoordinate coordinates
    ) {
        CompletableFuture<String> future = new CompletableFuture<>();

        String url = BASE_URL + "reverse?format=jsonv2&lat=" + coordinates.latitude()
                + "&lon=" + coordinates.longitude();

        API.getAsync(url, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onFailure(IOException e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }
}
